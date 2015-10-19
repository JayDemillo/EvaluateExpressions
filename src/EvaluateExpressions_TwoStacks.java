import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluateExpressions_TwoStacks {
	private final static boolean verbose = false;
	private final static int PRECEDENCE_PARENTHESIS = 9;
	private final static int PRECEDENCE_FACTORIAL = 2;
	private final static int PRECEDENCE_POWER = 3;
	private final static int PRECEDENCE_MULTDIV = 4;
	private final static int PRECEDENCE_ADDSUB = 5;
	private final static int PRECEDENCE_GREATERLESSER = 6;
	private final static int PRECEDENCE_EQUALITY = 7;

	private static Stack<String> valueStack = new Stack<String>();
	private static Stack<String> operatorStack = new Stack<String>();

	public static void main(String[] args) {

		//long startTime = System.nanoTime();
		//System.out.println(factorial(10));
		//long endTime = System.nanoTime();

		//imports a text file containing expressions in each line
		ArrayList<String> arithmeticEquation = new ArrayList<String>();
		arithmeticEquation = importFile(arithmeticEquation);
		arithmeticEquation.trimToSize();

		for (String s: arithmeticEquation)
		{
			if (verbose)
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

		//checks for factorial value and computes it 
		for(String sub: ops)
		{
			if (verbose)
			{
				System.out.println(valueStack);
				System.out.println(operatorStack);
			}
			if (isNumber(sub))
			{
				if (verbose)
					System.out.println("Number:" + sub);
				valueStack.push(Double.toString(Double.parseDouble(sub)));
			}
			else 
			{		
				if (verbose)
					System.out.println("Operation:" + sub);

				repeatOperations(sub);
				if (!sub.equals(")")) 
					operatorStack.push(sub);
			}
		}
		if (verbose)
		{
			System.out.println("Top of the number stack is " + valueStack.peek());
			System.out.println("Size of valueStack is: " + valueStack.size());
			System.out.println(valueStack);
		}

		repeatOperations("$");

		return valueStack.peek();

	}

	public static void repeatOperations(String operator)
	{
		if (verbose)
		{
			System.out.println();
			System.out.println();
			System.out.println("repeatOperations argument operator = " + operator); 
			System.out.println("!valueStack.empty() = " + !valueStack.empty() 
					+ " - !operatorStack.empty() = " + !operatorStack.empty() );	
			if (!operatorStack.empty())
				System.out.println("Operator at top of stack: " + operatorStack.peek() + " and has prec value of :" + convertToPrecedenceValue((String)operatorStack.peek()));
			if (!operatorStack.empty())
				System.out.println("Operator being checked: " + operator + " and has prec value of :" + convertToPrecedenceValue(operator));

			if (!valueStack.empty() && !operatorStack.empty())
				System.out.println( " - convertToPrecedenceValue(operator) >= convertToPrecedenceValue((String)operatorStack.peek()) = " + (convertToPrecedenceValue(operator) >= convertToPrecedenceValue((String)operatorStack.peek())));

			System.out.println();
			System.out.println();
		}
		while (!valueStack.empty() && !operatorStack.empty() 
				&& (convertToPrecedenceValue(operator) >= convertToPrecedenceValue((String)operatorStack.peek()))) 
		{
			if (verbose)
				System.out.println("Entered repeatOperations while loop with operator : " + operator); 
			if (operator.equals(")"))
			{
				if (verbose)
					System.out.println("closing parentheses loop"); 
				while (!operatorStack.peek().equals("(")) 
				{
					if (verbose)
						System.out.println(operatorStack);
					doOperation();
				}

				operatorStack.pop(); //To remove the opening bracket operator from the stack
				break; 
			} else if (operator.equals("(")) {
				break; 
			}

			else
			{
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
		if (verbose)
			System.out.println("Performing operation with operator: " + operator); 
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
		case "(": return PRECEDENCE_PARENTHESIS; 
		case ")": return PRECEDENCE_PARENTHESIS; 
		case "!": return PRECEDENCE_FACTORIAL;
		case "^": return PRECEDENCE_POWER;
		case "*":return PRECEDENCE_MULTDIV;
		case "x":return PRECEDENCE_MULTDIV;
		case "/": return PRECEDENCE_MULTDIV;
		case "+":return PRECEDENCE_ADDSUB;
		case "-": return PRECEDENCE_ADDSUB; 
		case ">":return PRECEDENCE_GREATERLESSER;
		case "<":return PRECEDENCE_GREATERLESSER;
		case ">=":return PRECEDENCE_GREATERLESSER;
		case "<=": return PRECEDENCE_GREATERLESSER;
		case "==":return PRECEDENCE_EQUALITY;
		case "!=": return PRECEDENCE_EQUALITY;
		case "$": return 20; 
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

	/*	public static double factorial(int f)
	{

	}*/

	/**
	 * 
	 * @param n
	 * @return
	 */
	public static double factorial(int f) {

		double fact = f;

		if (f<=10)
		{
			for (int i = 1; i < f ; ++i)
			{
				fact = fact*i;
			}
			return fact;
		}

		return (2.506628274631*Math.pow((float)f,(float)f+0.5)*Math.exp(-(double)f));

		/*f = 1.0;
        if(n <= 10) 
        {
            // calculate the actual factorial
            for(j = 1; j <= n; j++) f*=j;
            return f;
        }*/

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
		File inputFile; 
		try {

			inputFile = new File("expressions.txt");

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