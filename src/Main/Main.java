package Main;

import java.util.*;

import Main.Expression_Tree.Node;

public class Main{

    private static HashMap<Character, String> variables;
    private static HashMap<Character, Integer> variableInRegister;
    private static HashMap<Integer, Integer> registerStates;
    private static ArrayList<String> instructions;
    private static int[] registers;

    public static void main(String[] args){

        //Initializations
        variables = new HashMap<>();
        variableInRegister = new HashMap<>();
        registerStates = new HashMap<>();
        instructions = new ArrayList<>();
        registers = new int[4];
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
        //constructInstructions();
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

        System.out.println("\n" + postfixExpression);
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

    /*[a b | , * , c d & , - , / , e f - , ! , ^ ]
    private static void constructInstructions(){

        String[] elements;
        char firstLetter, secondLetter, operand;
        int firstLocation, secondLocation;

        for(String instruction : instructions){

            elements = instruction.split(" ");
            switch(elements.length){

                case 1:
                    break;
                case 2:
                    break;
                case 3:

                    firstLetter = elements[0].charAt(0);
                    secondLetter = elements[1].charAt(0);
                    operand = elements[2].charAt(0);



                    break;
            }
        }
    }
*/
    private static void createRegisters(){

        registerStates.put(0, Register_State.empty);
        registerStates.put(1, Register_State.empty);
        registerStates.put(2, Register_State.empty);
        registerStates.put(3, Register_State.empty);
        registers[0] = registers[1] = registers[2] = registers[3] = 0;
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
                registers[registerNumber] = value;
                break;
            }
        }

        return registerNumber;
    }
}
