import java.util.HashMap;
import java.util.Scanner;

public class Main{

    private static HashMap<Character, String> variables;

    public static void main(String[] args){

        //Initializations
        variables = new HashMap<>();
        Postfix_Creator postfix_creator = new Postfix_Creator(variables);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the expression that you want to create the assembly form of : ");

        String expression = scanner.nextLine();
        String postfixExpression = postfix_creator.postfix(expression);

        //Lets write the variables and their values...
        writeToScreen(postfixExpression);
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
}
