import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Arithmetic_Recursive{
	private final static int PRECEDENCE_PARENTHESIS = 1;
	private final static int PRECEDENCE_FACTORIAL = 2;
	private final static int PRECEDENCE_POWER = 3;
	private final static int PRECEDENCE_MULTDIV = 4;
	private final static int PRECEDENCE_ADDSUB = 5;
	private final static int PRECEDENCE_GREATERLESSER = 6;
	private final static int PRECEDENCE_EQUALITY = 7;

	private static Stack<String> valueStack = new Stack<String>();
	private static Stack<String> operatorStack = new Stack<String>();
	private static String $lowestPrecedence = "";

	public static void main(String[] args) {

		//long startTime = System.nanoTime();
		//System.out.println(factorial(10));
		//long endTime = System.nanoTime();

		//imports a text file containing expressions in each line
		ArrayList<String> arithmeticEquation = new ArrayList<String>();
		/*	arithmeticEquation = importFile(arithmeticEquation);
		arithmeticEquation.trimToSize();
		 */

		//CHECK FOR -(4+5) where unary - before (
		//makes sure that the iterator works
		arithmeticEquation.add("3+(5*6)+7");
		//arithmeticEquation.add("");
		for (String s: arithmeticEquation)
		{
			System.out.println(s);
			String answer = evalExpression(s);
			System.out.println(answer);
		}
	}

	public static String evalExpression(String s)
	{

		//creates a regular expression to isolate operators and operands
		Pattern pattern = Pattern.compile("\\(|\\)|-?\\d+\\.?\\d*|(==)|(<=)|(>=)|[+-/<>^!*=x!]");
		Matcher matcher = pattern.matcher(s);

		//creates an arraylist of strings containing the isolated operators and operands
		ArrayList<String> ops = new ArrayList<String>();
		while(matcher.find())
		{
			ops.add(matcher.group());
		}
		ops.trimToSize();

		boolean previousIsOp = false;

		//checks for factorial value and computes it HAVE TO DO BRACKETS FIRST
		for(String sub: ops)
		{
			System.out.println(valueStack);
			System.out.println(operatorStack);
			if (isNumber(sub))
			{
				System.out.println("Number:" + sub);
				valueStack.push(Double.toString(Double.parseDouble(sub)));
				previousIsOp = false;
			}
			else 
			{
				System.out.println("Operation:" + sub);
				repeatOperations(sub);
				operatorStack.push(sub);
				previousIsOp = true;
			}
		}
		repeatOperations($lowestPrecedence); // DO THIS FOR END OF INPUT


		return valueStack.peek();

	}

	private static void checkOperator(String op) {

		// Once at a closing parenthesis, all operations are performed until the opening parenthesis is reached
		if (op.equals(")")) 
		{ 
			while (!operatorStack.peek().equals("(")) {
				doOperation();
			}
			operatorStack.pop(); // This pops the closing parentheses out of the opStack
		}	

	}

	public static void repeatOperations(String operator)
	{
		//while (!valueStack.empty() && !operatorStack.empty() && (convertToPrecedenceValue(operator) >= convertToPrecedenceValue((String)operatorStack.peek())))
		
		while (valueStack.size() > 1 && (convertToPrecedenceValue(operator) >= convertToPrecedenceValue((String)operatorStack.peek())))
		{
			if (operator.equals(")"))
			{
				while (!operatorStack.peek().equals("(")) 
				{
					doOperation();
				}
				operatorStack.pop(); //To pop closing operator out of the stack
			}
			
			else 
				{
				$lowestPrecedence = operator;
				doOperation();
				}
			
		}
	}

	/**
	 * Calculates an individual operation
	 * @return 
	 */
	public static void doOperation()
	{
		String operator = operatorStack.pop();

		//unary operation (- should be handled during parsing)
		if (operator.equals("!"))
		{
			double x = Double.parseDouble(valueStack.pop());
			valueStack.push(String.valueOf(factorial((int)x))); 
		}
		//binary operations
		else 
		{
			double x = Double.parseDouble(valueStack.pop());
			double y = Double.parseDouble(valueStack.pop());

			switch (operator)
			{
			case "(": break;	
			case "^": valueStack.push(String.valueOf(power(y, (int)x))); break;
			case "*": 
			case "x": valueStack.push((y * x) + ""); break;
			case "/": valueStack.push((y / x) + ""); break;
			case "+": valueStack.push((y + x) + ""); break;
			case "-": valueStack.push((y - x) + ""); break;
			case ">": valueStack.push((y > x) + ""); break;
			case "<": valueStack.push((y < x) + ""); break;
			case ">=": valueStack.push((y >= x) + ""); break;
			case "<=": valueStack.push((y <= x) + ""); break;
			case "==": valueStack.push((y == x) + ""); break;
			case "!=": valueStack.push((y != x) + ""); break;
			default:
				throw new IllegalArgumentException("Unrecognized operator: " + operator);

			}
		}

	}

	public static boolean isNumber(String sub)  
	{  
		try  
		{  
			Double.parseDouble(sub);  
		}  
		catch(NumberFormatException e)  
		{  
			return false;  
		}  
		return true;  
	}

	public static int convertToPrecedenceValue(String operator)
	{
		switch (operator)
		{
		case "(": 
		case ")": return PRECEDENCE_PARENTHESIS;
		case "!": return PRECEDENCE_FACTORIAL;
		case "^": return PRECEDENCE_POWER;
		case "*":
		case "x":
		case "/": return PRECEDENCE_MULTDIV;
		case "+":
		case "-": return PRECEDENCE_ADDSUB; 
		case ">":
		case "<":
		case ">=":
		case "<=": return PRECEDENCE_GREATERLESSER;
		case "==":
		case "!=": return PRECEDENCE_EQUALITY;
		default:
			throw new IllegalArgumentException("Unrecognized operator: " + operator);
		}

	}

	public static double power(double base, int exponent)
	{
		double total = 1;
		while(exponent != 0) 
		{
			if(exponent % 2 == 0) 
			{
				base *= base;
				exponent /= 2;
			} 
			else 
			{
				exponent -= 1;
				total *= base;
			}
		}
		return total;

	}

	public static int factorial(int f)
	{
		int counter = f;
		int sum = f;

		while(counter>0)
		{
			sum += sum*--counter;
		}
		return sum;
	}

	/**
	 * This function ask the user for an input file that contains arithmetic equations
	 * and returns the equations in an arrayList of Strings
	 * 
	 * @param arithmeticEquation is an initialized ArrayList of Strings
	 * @return the arithmeticEquation containing arithmetic equations from the
	 * individual lines of a given input file 
	 */
	public static ArrayList<String> importFile(ArrayList<String> arithmeticEquation)
	{
		Scanner fileIn = new Scanner(System.in);
		File inputFile; 
		try {

			System.out.print("Enter file name with extention: ");
			inputFile = new File(fileIn.nextLine());

			Scanner fileInput = new Scanner(inputFile);
			while (fileInput.hasNextLine())
			{
				arithmeticEquation.add(fileInput.nextLine());
			}
			fileInput.close();
		}
		catch(IOException e)
		{
			System.out.println("Error parsing file");
		}
		return arithmeticEquation;
	}
}
