package Main;

import java.util.*;

import Main.Expression_Tree.Node;

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

        System.out.println(Arrays.toString(assembly.toArray()));
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
        int firstLocation, secondLocation, resultLocation;

        Date[] resultLocations_dates = {null, null};
        Integer[] resultLocations_values = {null, null};

        for(String instruction : instructions){

            elements = instruction.split(" ");
            switch(elements.length){

                case 1:

                    operand = elements[0].charAt(0);

                    if(operand == '|' || operand == '^' || operand == '&' || operand == '+' || operand == '-'){

                        firstLocation = resultLocations_values[0];
                        secondLocation = resultLocations_values[1];
                        resultLocation = findEmptyRegister();

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation + " " + secondLocation);
                        registerStates.replace(firstLocation, Register_State.used);
                        registerStates.replace(secondLocation, Register_State.used);

                        resultLocations_values[0] = resultLocation;
                        resultLocations_dates[0] = new Date();
                        resultLocations_dates[1] = null;

                    }else if(operand == '*' || operand == '/'){

                        int found;

                        //We need to find the result location.
                        if(resultLocations_dates[0] != null && resultLocations_dates[1] != null){

                            if(resultLocations_dates[0].getTime() > resultLocations_dates[1].getTime())
                                found = 0;
                            else
                                found = 1;

                        }else if(resultLocations_dates[0] != null)
                            found = 0;
                        else
                            found = 1;

                        resultLocation = resultLocations_values[found];
                        resultLocations_dates[found] = new Date();

                        assembly.add(operandName(operand) + " " + resultLocation);

                    }else{

                        int found;

                        //We need to find the result location.
                        if(resultLocations_dates[0] != null && resultLocations_dates[1] != null){

                            if(resultLocations_dates[0].getTime() > resultLocations_dates[1].getTime())
                                found = 0;
                            else
                                found = 1;

                        }else if(resultLocations_dates[0] != null)
                            found = 0;
                        else
                            found = 1;

                        firstLocation = resultLocations_values[found];

                        resultLocation = findEmptyRegister();
                        resultLocations_dates[found] = new Date();
                        resultLocations_values[found] = resultLocation;

                        assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation);
                        registerStates.replace(firstLocation, Register_State.used);
                    }

                    break;
                case 2:
                    break;
                case 3:

                    firstLetter = elements[0].charAt(0);
                    secondLetter = elements[1].charAt(0);
                    operand = elements[2].charAt(0);

                    firstLocation = placeInRegister(firstLetter, Integer.parseInt(variables.get(firstLetter)));
                    secondLocation = placeInRegister(secondLetter, Integer.parseInt(variables.get(secondLetter)));
                    resultLocation = findEmptyRegister();

                    assembly.add(operandName(operand) + " " + resultLocation + " " + firstLocation + " " + secondLocation);

                    registerStates.replace(firstLocation, Register_State.used);
                    registerStates.replace(secondLocation, Register_State.used);
                    variableInRegister.replace(firstLetter, -1);
                    variableInRegister.replace(secondLetter, -1);

                    if(resultLocations_dates[0] == null){
                        resultLocations_values[0] = resultLocation;
                        resultLocations_dates[0] = new Date();
                    }else{
                        resultLocations_values[1] = resultLocation;
                        resultLocations_dates[1] = new Date();
                    }

                    break;
            }
        }
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

        //If no place is appropriate, the instruction is not suited for this ISA.
        if(registerNumber == registerStates.size()){
            System.out.println("The instruction is not suited for this ISA!!!");
            System.exit(-1);
        }

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
}
