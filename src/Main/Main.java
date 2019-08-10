package Main;

import java.util.*;

import Main.Expression_Tree.Node;

public class Main{

    private static HashMap<Character, String> variables;
    private static ArrayList<String> instructions;

    public static void main(String[] args){

        //Initializations
        variables = new HashMap<>();
        instructions = new ArrayList<>();
        Postfix_Creator postfix_creator = new Postfix_Creator(variables);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the expression that you want to create the assembly form of : ");

        String expression = scanner.nextLine();
        String postfixExpression = postfix_creator.postfix(expression);

        //Lets write the variables and their values...
        writeToScreen(postfixExpression);

        //Now, we need to construct the binary tree.
        Node tree = Expression_Tree.constructTree(postfixExpression);

        //Lets traverse over instructions and decide the priorities...
        traverse(tree);

        System.out.println(Arrays.toString(instructions.toArray()));
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
}
