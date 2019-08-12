package Main;

import Main.Expression_Tree.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main{

    private static HashMap<Character, String> variables;
    private static HashMap<Character, Integer> variableInRegister;
    private static HashMap<Integer, Integer> registerStates;
    private static ArrayList<String> instructions;
    private static ArrayList<String> assembly;

    public static void main(String[] args){

        //Initializations
        variables = new HashMap<>();
        variableInRegister = new HashMap<>();
        registerStates = new HashMap<>();
        instructions = new ArrayList<>();
        assembly = new ArrayList<>();
        Postfix_Creator postfix_creator = new Postfix_Creator(variables, variableInRegister);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the expression that you want to create the assembly form of : ");

        String expression = scanner.nextLine();
        String postfixExpression = postfix_creator.postfix(expression);
        postfix_creator.fillVariableInRegister();

        //Lets write the variables and their values...
        writeToScreen(postfixExpression);

        //Now, we need to construct the binary tree.
        Node tree = Expression_Tree.constructTree(postfixExpression);

        //Lets traverse over instructions and decide the priorities...
        traverse(tree);

        //Lets create the assembly instructions using instructions ArrayList.
        createRegisters();
        constructInstructions();

        System.out.println(Arrays.toString(assembly.toArray()) + "\n");

        writeToFile();
        System.out.println("The output is written into Instructions.txt file!");
    }

    private static void writeToScreen(String postfixExpression){

        if(postfixExpression.equals("Entered expression is not valid!")){
            System.out.println(postfixExpression);
            System.exit(-1);
        }

        //Lets write the variables and their values...
        for(int i = 0; i < variables.size(); i++){
            System.out.println(variables.keySet().toArray()[i] + " = " + variables.values().toArray()[i]);
        }

        System.out.println("\n" + postfixExpression + "\n");
    }

    private static void traverse(Node node){

        if(node != null){
            traverse(node.left);
            traverse(node.right);
            addToInstructions(node.value);
        }
    }

    private static void addToInstructions(char value){

        if(instructions.isEmpty())
            instructions.add(value + " ");
        else{

            //Get the last instruction.
            String instruction = instructions.get(instructions.size()-1);
            String[] elements = instruction.split(" ");
            List<String> list_elements = Arrays.asList(elements);

            if(list_elements.contains("|") || list_elements.contains("^") || list_elements.contains("&") || list_elements.contains("+") || list_elements.contains("-") ||
                    list_elements.contains("*") || list_elements.contains("/") || list_elements.contains("!") || list_elements.contains("~")){

                instructions.add(value + " ");

            }else if(list_elements.size() == 2 && operandName(value).equals("NULL")){ // If the third coming  value is also not an operand...

                instructions.remove(instructions.size()-1);
                instructions.add(list_elements.get(0) + " ");
                instructions.add(list_elements.get(1) + " " + value + " ");

            }else{

                instruction += value + " ";
                instructions.remove(instructions.size()-1);
                instructions.add(instruction);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void constructInstructions(){

        String[] elements;
        char firstLetter, secondLetter, operand;
        int firstLocation, secondLocation, resultLocation = -1;

        Integer[] previousResultLocations = {null, null};
        Integer backup = null;

        for(String instruction : instructions){

            elements = instruction.split(" ");
            switch(elements.length){

                case 1:

                    operand = elements[0].charAt(0);

                    if(operand == '|' || operand == '^' || operand == '&' || operand == '+' || operand == '-'){

                        if(previousResultLocations[0] != null){
                            firstLocation = previousResultLocations[0];
                            previousResultLocations[0] = null;
                        }else{
                            firstLocation = backup;
                            backup = null;
                        }

                        secondLocation = previousResultLocations[1];
                        resultLocation = findEmptyRegister();

                        if(resultLocation == -1) resultLocation = firstLocation;

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation + " " + secondLocation);
                        if(resultLocation != firstLocation) registerStates.replace(firstLocation, Register_State.used);
                        registerStates.replace(secondLocation, Register_State.used);

                        previousResultLocations[1] = resultLocation;

                    }else if(operand == '*' || operand == '/'){

                        resultLocation = previousResultLocations[1];

                        assembly.add(operandName(operand) + " " + resultLocation);

                    }else if(operand == '!' || operand == '~'){

                        firstLocation = previousResultLocations[1];
                        resultLocation = findEmptyRegister();

                        if(resultLocation == -1) resultLocation = firstLocation;

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation);
                        if(resultLocation != firstLocation) registerStates.replace(firstLocation, Register_State.used);

                        previousResultLocations[1] = resultLocation;
                    }else{

                        //If the operand actually is not an operand...
                        resultLocation = placeInRegister(operand, Integer.parseInt(variables.get(operand)));

                        previousResultLocations[1] = resultLocation;
                    }

                    break;
                case 2:

                    firstLetter = elements[0].charAt(0);
                    operand = elements[1].charAt(0);

                    if(operand == '|' || operand == '^' || operand == '&' || operand == '+' || operand == '-'){

                        firstLocation = previousResultLocations[1];
                        secondLocation = placeInRegister(firstLetter, Integer.parseInt(variables.get(firstLetter)));
                        resultLocation = findEmptyRegister();

                        if(resultLocation == -1) resultLocation = firstLocation;

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation + " " + secondLocation);
                        if(resultLocation != firstLocation) registerStates.replace(firstLocation, Register_State.used);
                        registerStates.replace(secondLocation, Register_State.used);

                        previousResultLocations[1] = resultLocation;

                    }else if(operand == '*' || operand == '/'){

                        firstLocation = placeInRegister(firstLetter, Integer.parseInt(variables.get(firstLetter)));
                        resultLocation = firstLocation;

                        assembly.add(operandName(operand) + " " + resultLocation);

                        //Move back one unit
                        backup = shiftPreviousLocation(previousResultLocations, backup);

                        previousResultLocations[1] = resultLocation;
                    }else if(operand == '!' || operand == '~'){

                        firstLocation = placeInRegister(firstLetter, Integer.parseInt(variables.get(firstLetter)));
                        resultLocation = findEmptyRegister();

                        if(resultLocation == -1) resultLocation = firstLocation;

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation);
                        if(resultLocation != firstLocation) registerStates.replace(firstLocation, Register_State.used);

                        //Move back one unit
                        backup = shiftPreviousLocation(previousResultLocations, backup);

                        previousResultLocations[1] = resultLocation;
                    }

                    break;
                case 3:

                    firstLetter = elements[0].charAt(0);
                    secondLetter = elements[1].charAt(0);
                    operand = elements[2].charAt(0);

                    firstLocation = placeInRegister(firstLetter, Integer.parseInt(variables.get(firstLetter)));
                    secondLocation = placeInRegister(secondLetter, Integer.parseInt(variables.get(secondLetter)));
                    resultLocation = findEmptyRegister();

                    if(resultLocation == -1) resultLocation = firstLocation;

                    assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation + " " + secondLocation);

                    if(resultLocation != firstLocation) registerStates.replace(firstLocation, Register_State.used);
                    registerStates.replace(secondLocation, Register_State.used);
                    variableInRegister.replace(firstLetter, -1);
                    variableInRegister.replace(secondLetter, -1);

                    //Move back one unit
                    backup = shiftPreviousLocation(previousResultLocations, backup);

                    previousResultLocations[1] = resultLocation;
                    break;
            }
        }

        //Lets move the lastLocation to the first register.
        assembly.add("mov 0 " + resultLocation);
    }

    private static void createRegisters(){

        registerStates.put(0, Register_State.empty);
        registerStates.put(1, Register_State.empty);
        registerStates.put(2, Register_State.empty);
        registerStates.put(3, Register_State.empty);
    }

    private static boolean checkInRegister(char letter){

        int inRegister = variableInRegister.get(letter);

        return inRegister != -1;
    }

    private static int placeInRegister(char letter, int value){

        if(checkInRegister(letter))
            return variableInRegister.get(letter);

        //Means this letter is not in any register, we need to place it.
        int registerNumber;

        for(registerNumber = 0; registerNumber < registerStates.size(); registerNumber++){

            if(registerStates.get(registerNumber) == Register_State.empty || registerStates.get(registerNumber) == Register_State.used){
                registerStates.replace(registerNumber, Register_State.initialized);
                variableInRegister.replace(letter, registerNumber);
                break;
            }
        }

        //If no place is appropriate, the instruction is not suited for this ISA.
        if(registerNumber == registerStates.size()){
            System.out.println("The instruction is not suited for this ISA!!!");
            System.exit(-1);
        }

        //Now, we need to add this to the assembly list.
        assembly.add("ldi " + registerNumber + " " + value);

        return registerNumber;
    }

    private static int findEmptyRegister(){

        int registerNumber;

        for(registerNumber = 0; registerNumber < registerStates.size(); registerNumber++){

            if(registerStates.get(registerNumber) == Register_State.empty || registerStates.get(registerNumber) == Register_State.used){
                registerStates.replace(registerNumber, Register_State.initialized);
                break;
            }
        }

        /*
        //If no place is appropriate, the instruction is not suited for this ISA.
        if(registerNumber == registerStates.size()){
            System.out.println("The instruction is not suited for this ISA!!!");
            System.exit(-1);
        }*/

        if(registerNumber == registerStates.size())
            registerNumber = -1;

        return registerNumber;
    }

    private static String operandName(char operand){

        if(operand == '|') return "or";
        if(operand == '^') return "xor";
        if(operand == '&') return "and";
        if(operand == '+') return "add";
        if(operand == '-') return "sub";
        if(operand == '*') return "inc";
        if(operand == '/') return "dec";
        if(operand == '!' || operand == '~') return "not";

        return "NULL";
    }

    private static Integer shiftPreviousLocation(Integer[] previousResultLocations, Integer backup){

        if(previousResultLocations[1] != null){
            if(previousResultLocations[0] != null){
                if(backup == null)
                    backup = previousResultLocations[0];
                else{
                    System.out.println("Please contact the programmer, there is an error!");
                    System.exit(-1);
                }
            }
            previousResultLocations[0] = previousResultLocations[1];
        }

        return backup;
    }

    private static void writeToFile(){

        try{
            FileWriter fw = new FileWriter("Instructions.txt");

            for(String instruction : assembly)
                fw.write(instruction + "\n");

            fw.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
