import java.util.Stack;

public class Main{
    public static void main(String[] args){

        String expression = "[(((117|64)++)-(47&15))--]^!(12-95)";
        System.out.println(transformExpression(expression));
    }

    private static String transformExpression(String expression){

        StringBuilder transformedExpression = new StringBuilder();
        char current_character, previous_character = '$';

        for(int i = 0; i < expression.length(); i++){
            current_character = expression.charAt(i);

            if(previous_character == current_character){
                //We only want to transform increment and decrement operators.
                if(current_character == '+' || current_character == '-'){
                    transformedExpression.deleteCharAt(transformedExpression.length()-1);
                    transformedExpression.append((current_character == '+' ? '*' : '/'));
                    continue;
                }
            }

            transformedExpression.append(current_character);
            previous_character = current_character;
        }

        return transformedExpression.toString();
    }

    private static int precedence(char character){

        switch(character){

            case '|' :
                return 1;
            case '^' :
                return 2;
            case '&' :
                return 3;
            case '+' :
            case '-' :
                return 4;
            case '*' :
            case '/' :
                return 5;
            case '~' :
            case '!' :
                return 6;
        }

        return -1;
    }

    private static String infixToPostfix(String expression){

        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for(int i = 0; i < expression.length(); i++){

            char current_character = expression.charAt(i);

            if(Character.isDigit(current_character))
                postfix.append(current_character);
            else if(current_character == '(' || current_character == '[')
                stack.push(current_character);
            else if(current_character == ')'){

                //Pop until a '(' character is encountered.
                while(!stack.isEmpty() && stack.peek() != '(' && stack.peek() != '[')
                    postfix.append(stack.pop());

                if(!stack.isEmpty() && stack.peek() != '(')
                    return "Entered expression is not valid!";
                else
                    stack.pop();
            }else if(current_character == ']'){

                //Pop until a '[' character is encountered.
                while(!stack.isEmpty() && stack.peek() != '(' && stack.peek() != '[')
                    postfix.append(stack.pop());

                if(!stack.isEmpty() && stack.peek() != '[')
                    return "Entered expression is not valid!\n";
                else
                    stack.pop();
            }else{

                while(!stack.isEmpty() && precedence(current_character) <= precedence(stack.peek())){

                    if(stack.peek() == '(' || stack.peek() == '[')
                        return "Entered expression is not valid!";
                    else if(stack.peek() == current_character)
                        break;
                    else
                        postfix.append(stack.pop());
                }

                stack.push(current_character);
            }
        }

        //Pop all remaining elements of the stack.
        while(!stack.isEmpty()){

            if(stack.peek() == '(' || stack.peek() == '[')
                return "Entered expression is not valid!";
            postfix.append(stack.pop());
        }

        return postfix.toString();
    }
}
