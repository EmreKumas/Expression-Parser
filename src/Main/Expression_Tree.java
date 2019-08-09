package Main;

import java.util.Stack;

class Expression_Tree{

    static class Node{

        char value;
        Node left, right;

        Node(char item){
            value = item;
            left = right = null;
        }
    }

    private static boolean isOperator(char character){

        return character == '|' || character == '^' || character == '&' || character == '+' || character == '-' ||
                character == '*' || character == '/' || character == '~' || character == '!';
    }

    static Node constructTree(String postfixExpression){

        Stack<Node> stack = new Stack<>();
        Node t0, t1, t2;

        for(int i = 0; i < postfixExpression.length(); i++){

            char current_character = postfixExpression.charAt(i);
            t0 = new Node(current_character);

            if(isOperator(current_character)){

                //There are two possibilities:
                //If the operation is requires two item...
                if(current_character == '|' || current_character == '^' || current_character == '&' || current_character == '+' || current_character == '-'){

                    //Pop top two items and make them children.
                    t1 = stack.pop();
                    t2 = stack.pop();
                    t0.right = t1;
                    t0.left = t2;
                }//If the operation requires only one item...
                else{

                    //Pop one item and make it children.
                    t1 = stack.pop();
                    t0.left = t1;
                }
            }

            stack.push(t0);
        }

        return stack.pop();
    }
}
