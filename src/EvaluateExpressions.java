import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;


public class EvaluateExpressions {

	private static Stack<String> valueStack = new Stack<String>();
	private static Stack<String> operatorStack = new Stack<String>();
	private static boolean finalValue = false;
	
	public static void main(String[] args) {
		
		Scanner input = null;
		String expression;
		try {
			input = new Scanner(new FileInputStream("test.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot open test.txt");
			System.exit(0);
		}
		
		try {
			while (input.hasNextLine()) {
				expression = input.nextLine().replaceAll("\\s", "");
				System.out.println(expression + " " + expression.length());
				evalExpr3(expression);
			}
		} catch (NoSuchElementException e) {
			System.out.println("ERROR: Tried to read a line that doesn't exist");
			System.exit(0);
		} catch (IllegalStateException e) {
			System.out.println("ERROR: Input stream is close");
			System.exit(0);
		}
		input.close();
	}
	
	
	
	
	private static boolean isNumber(char c, boolean prevCharIsOp) {
		if (prevCharIsOp)
			return (Character.isDigit(c) || c == '.' || c == '-'  );
		else return (Character.isDigit(c) || c == '.');
	}
		

	
	private static void checkOps(String op) {
		System.out.println("Operator being checked is: " + op); // test statement
		//System.out.println("Top of operator stack is: " + opStack.peek()); // test statement
	
		// If we have reached a closing parentheses then we perform all operations until we find a closing parentheses
		if (op.equals(")")) { 
			while (!operatorStack.peek().equals("(")) {
				doOp();
			}
			operatorStack.pop(); // This pops the closing parentheses out of the opStack
			
		} if (op.equals("(")) {
			// do nothing because a closing parentheses doesn't perform an operation
			
			
			// This else if is needed for expressions with more than one comparison operator
		} else if(opPrec(op) < 3 && opPrec(op) > 0) {
			// This is executed when we reach a comparison operator
			// We want to perform all operations so we have a value on the left side of the comparison operator
			while (!operatorStack.empty() && opPrec(operatorStack.peek()) > 0 && !valueStack.peek().equals("false")) {
				System.out.println("Currently in conditional loop"); // test statement
				doOp();
			}			
			
			if (!operatorStack.empty()) {
				// Pop the comparison operator that was executed in the above while loop
				System.out.println("About to pop: " + operatorStack.peek()); // test statement
				operatorStack.pop();
			}
				
		} else { // Else follow the standard 
			while (!valueStack.empty() && (!operatorStack.empty()) && (opPrec(op) <= opPrec(operatorStack.peek()))) {
				doOp();			
			}			
		}
	}
	
	
	private static void doOp() {
		String op = operatorStack.pop();
		System.out.println("Popping operator: " + op); // test statement
		double x, y;
		if (op.equals("!")) { // unary operation
			x = Integer.parseInt(valueStack.pop());
			//calculate factorial of x;
		} else { // binary operation
			System.out.println("Number about to be popped: " + valueStack.peek()); // test statement
			x = Double.parseDouble(valueStack.pop());
			System.out.println("Number about to be popped: " + valueStack.peek()); // test statement
			y = Double.parseDouble(valueStack.pop());
			
			if (op.equals("^")) 		valueStack.push(Math.pow(y,x) + "");
			else if (op.equals("*"))	valueStack.push((y * x) + "");
			else if (op.equals("/"))	valueStack.push((y / x) + "");
			else if (op.equals("+"))	valueStack.push((y + x) + "");
			else if (op.equals("-"))	valueStack.push((y - x) + "");
			else if (op.equals(">")) {
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y > x && !finalValue)	valueStack.push(x + ""); 
				else	valueStack.push((y > x) + "");
			} else if (op.equals(">="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y >= x && !finalValue)	valueStack.push(x + "");
				else	valueStack.push((y >= x) + "");
			} else if (op.equals("<")) {
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y < x && !finalValue) valueStack.push(x + "");
				else	valueStack.push((y < x) + "");
			} else if (op.equals("<="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y <= x && !finalValue) valueStack.push(x + "");
				else	valueStack.push((y <= x) + "");
			} else if (op.equals("=="))	{
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y == x && !finalValue) valueStack.push(x + "");
				else	valueStack.push((y == x) + "");
			} else if (op.equals("!=")){
				//If the expression is true and there are still number terms left then we want the RHS value, not the truth of the expression
				if (y != x && !finalValue) valueStack.push(x + "");
				else	valueStack.push((y != x) + "");
			}
			
		} 
			
		System.out.println("result of last operation is: " + valueStack.peek()); // test statement
		System.out.println(); // test statement
	}
	
	private static int opPrec(String op) {
		switch (op) {
		case "(": return 0;
		case "!": return 7;
		case "^": return 5;
		case "*": return 4;
		case "/": return 4;
		case "+": return 3;
		case "-": return 3;
		case ">": return 2;
		case ">=": return 2;
		case "<": return 2;
		case "<=": return 2;
		case "==": return 1;
		case "!=": return 1;
		default: return 0;
		}
	}
	
	
	
	private static void evalExpr3(String expression) {
		
		// This flag is used to check if a minus sign is unary or binary
		// It is also used to determine if the current char is part of a new term or not
		boolean prevCharIsOp = false;
		boolean expressionIsFalse = false;
		int i = 0;
		// Numbers and operators will be stored as strings to acommodate numbers and operators that are larger than 1 char
		String term = "";
		
		try {
			// Initial for loop will go through the expression character by character, requiring n steps
			while (i < expression.length() && !expressionIsFalse) {
				term = "";	
				// We check if the character is part of a number term
				if (isNumber(expression.charAt(i), prevCharIsOp)) {
					
					term += expression.charAt(i);	
					
					// While the next character is also a number we add it to the term
					while ((i + 1) < expression.length() && isNumber(expression.charAt(i + 1), false))
						term += expression.charAt(++i);		
						
					System.out.println("About to push: " + term); // test statement
					
					// Push the number onto the stack
					valueStack.push(term);
					
					prevCharIsOp = false;
					
				} else { // character is an operator
					
					term += expression.charAt(i);
					
					// If the next character is also a non-number but not a parentheses
					// Then it must be the second character in a two-character comparison operator
					if ((i + 1) < expression.length() && !isNumber(expression.charAt(i + 1), true) && !term.equals(")") && !(expression.charAt(i + 1) == '('))
						term += expression.charAt(++i);
					
					System.out.println("Pre checkOps(), passing term: " + term); // test statement
					// Compare this operators precedence with ones before it
					checkOps(term);
					
					// Closing parentheses do not go on the opStack
					if (!term.equals(")")) {
						System.out.println("About to push operator: " + term); // test statement
						operatorStack.push(term);
					}
						
					
					prevCharIsOp = true;
				} // End of operator logic
				
				System.out.println("ABout to increment i"); // test statement
				i++;
				
				// If we reach one false comparison then the entire expression is false
				if (!valueStack.empty() && valueStack.peek().equals("false")) {
					expressionIsFalse = true;
				}
			} // End of main for loop
			
			finalValue = true;
		/*	if (!expressionIsFalse)
				checkOps("$");*/
			
			//numberStack.pop(term); // remove this line from final submission
		} catch (ArithmeticException e) {
			System.out.println("Division by zero, expression aborted");
		}
		
		
		
	
		System.out.println();
		
		System.out.println(valueStack.pop());
		System.out.println();
		
	} // end of method
	
	
}
