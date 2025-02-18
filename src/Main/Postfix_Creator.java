package Main;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class Postfix_Creator{

    private HashMap<Character, String> variables;
    private HashMap<Character, Integer> variableInRegister;

    Postfix_Creator(HashMap<Character, String> variables, HashMap<Character, Integer> variableInRegister){

        this.variables = variables;
        this.variableInRegister = variableInRegister;
    }

    String postfix(String expression){

        String transformedExpression = transformExpression(expression);

        return infixToPostfix(transformedExpression);
    }

    void fillVariableInRegister(){

        for(Map.Entry<Character, String> entry : variables.entrySet()){

            variableInRegister.put(entry.getKey(), -1);
        }
    }

    private int precedence(char character){

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

    private String infixToPostfix(String expression){

        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        int variable_index = 0;

        for(int i = 0; i < expression.length(); i++){

            char current_character = expression.charAt(i);

            if(Character.isDigit(current_character)){

                //If previously declared...
                if(variables.containsKey(alphabet[variable_index])){

                    String value = variables.get(alphabet[variable_index]);
                    value += current_character;
                    variables.put(alphabet[variable_index], value);
                }else{
                    variables.put(alphabet[variable_index], Character.toString(current_character));
                    postfix.append(alphabet[variable_index]);
                }

                continue;
            }else{

                //If we have already defined a variable, we need to increment variable_index...
                if(variables.containsKey(alphabet[variable_index]))
                    variable_index++;
            }

            if(current_character == '(' || current_character == '[')
                stack.push(current_character);
            else if(current_character == ')'){

                //Pop until a '(' character is encountered.
                while(!stack.isEmpty() && stack.peek() != '(' && stack.peek() != '[')
                    postfix.append(stack.pop());

                if(!stack.isEmpty() && stack.peek() != '(')
                    return "Entered expression is not valid!";
                else
                    try{
                        stack.pop();
                    }catch(EmptyStackException e) { return "Entered expression is not valid!"; }
            }else if(current_character == ']'){

                //Pop until a '[' character is encountered.
                while(!stack.isEmpty() && stack.peek() != '(' && stack.peek() != '[')
                    postfix.append(stack.pop());

                if(!stack.isEmpty() && stack.peek() != '[')
                    return "Entered expression is not valid!\n";
                else
                    try{
                        stack.pop();
                    }catch(EmptyStackException e) { return "Entered expression is not valid!"; }
            }else{

                while(!stack.isEmpty() && precedence(current_character) <= precedence(stack.peek())){

                    if(stack.peek() == '(' || stack.peek() == '[')
                        return "Entered expression is not valid!";
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

    /**
     * We convert the increment operator into multiplication operator. (++ -> *)
     * We convert the decrement operator into division operator. (-- -> /)
     */
    private String transformExpression(String expression){

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
}
