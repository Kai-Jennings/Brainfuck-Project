import java.util.Scanner;

public class Main {
    private static final int MULTIPLICATION_THRESHOLD = 12;
    
    public static void main(String[] args) {
        // Use try-with-resources to automatically close the scanner.
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter string: ");
            String input = scanner.nextLine();
            int[] unicodeValues = getUnicodeValues(input);
            int[] diffs = computeUnicodeDifferences(unicodeValues);
            
            StringBuilder bfCode = new StringBuilder();
            for (int diff : diffs) {
                bfCode.append(generateBrainfuckForValue(Math.abs(diff), 1, diff < 0));
            }
            
            String optimizedCode = removeRedundantShifts(bfCode.toString());
            System.out.println("Generated Brainfuck code:\n" + optimizedCode);
        }
    }
    
    /**
     * Converts the input string to an array of Unicode values,
     * appending a newline (Unicode value 10) at the end.
     *
     * @param input The input string.
     * @return An array of Unicode values.
     */
    private static int[] getUnicodeValues(String input) {
        int[] unicodeValues = new int[input.length() + 1];
        for (int i = 0; i < input.length(); i++) {
            unicodeValues[i] = input.charAt(i);
        }
        unicodeValues[input.length()] = 10; // Append trailing newline.
        return unicodeValues;
    }
    
    /**
     * Computes the differences between consecutive Unicode values.
     * This creates relative adjustments to be applied in the Brainfuck code.
     *
     * @param unicodeValues The array of Unicode values.
     * @return An array of differences.
     */
    private static int[] computeUnicodeDifferences(int[] unicodeValues) {
        int[] diffs = new int[unicodeValues.length];
        int previous = 0;
        for (int i = 0; i < unicodeValues.length; i++) {
            diffs[i] = unicodeValues[i] - previous;
            previous = unicodeValues[i];
        }
        return diffs;
    }
    
    /**
     * Generates Brainfuck code to adjust the current cell by n (absolute value),
     * using multiplication loops when the adjustment is large.
     *
     * @param n              The absolute adjustment needed.
     * @param registerOffset The register offset for pointer movement.
     * @param decrement      If true, the operation is a decrement; otherwise, an increment.
     * @return A String containing the generated Brainfuck code.
     */
    private static String generateBrainfuckForValue(int n, int registerOffset, boolean decrement) {
        StringBuilder output = new StringBuilder();
        String regForward = ">".repeat(registerOffset);
        String regBackward = "<".repeat(registerOffset);
        String symbol = decrement ? "-" : "+";
        
        if (n > MULTIPLICATION_THRESHOLD) {
            // Use multiplication loop for larger adjustments.
            int[] factorData = searchFactors(n);
            int outerCount = factorData[0];
            int innerCount = factorData[1];
            int adjustment = (decrement ? -1 : 1) * factorData[2];
            
            output.append("+".repeat(outerCount))  // Set up loop counter.
                  .append("[")
                  .append(regForward)
                  .append(symbol.repeat(innerCount))
                  .append(regBackward)
                  .append("-]")
                  .append(regForward)
                  .append(adjustment < 0 ? "-".repeat(-adjustment) : "+".repeat(adjustment));
        } else {
            // For small adjustments, use direct increment/decrement.
            output.append(regForward)
                  .append(symbol.repeat(n));
        }
        
        output.append(".").append(regBackward); // Print cell and reset pointer.
        return output.toString();
    }
    
    /**
     * Searches for a good factor pair (with a minimal difference) for n or nearby numbers.
     * The method returns an array {factor1, factor2, adjustment} such that:
     * n = factor1 * factor2 + adjustment.
     *
     * @param n The target number.
     * @return An array with the optimal factors and the required adjustment.
     */
    private static int[] searchFactors(int n) {
        int[] bestFactorPair = {0, Integer.MAX_VALUE};
        int bestAdjustment = 0;
        int smallestDiff = Integer.MAX_VALUE;
        
        // Search in a neighbourhood of -2 to +2 around n.
        for (int adjustment = -2; adjustment <= 2; adjustment++) {
            int candidate = n + adjustment;
            if (candidate > 0) {
                int[] factors = getFactorPair(candidate);
                int diff = Math.abs(factors[0] - factors[1]);
                // Tie-breaker: prefer pairs with the smallest difference or smallest adjustment.
                if (diff < smallestDiff || (diff == smallestDiff && Math.abs(adjustment) < Math.abs(bestAdjustment))) {
                    bestFactorPair = factors;
                    bestAdjustment = -adjustment;
                    smallestDiff = diff;
                }
            }
        }
        return new int[] { bestFactorPair[0], bestFactorPair[1], bestAdjustment };
    }
    
    /**
     * Finds a factor pair (a, b) for the given positive integer n,
     * choosing the pair where a is the largest factor not exceeding sqrt(n).
     *
     * @param n The number to factor.
     * @return An array {a, b} such that a * b = n.
     */
    private static int[] getFactorPair(int n) {
        int a = 1;
        int b = n;
        int sqrtN = (int) Math.sqrt(n);
        for (int i = 1; i <= sqrtN; i++) {
            if (n % i == 0) {
                a = i;
                b = n / i;
            }
        }
        return new int[] { a, b };
    }
    
    /**
     * Optimizes the generated Brainfuck code by removing redundant pointer shifts.
     * Currently, it removes simple patterns like "<>" or "><".
     *
     * @param code The raw Brainfuck code.
     * @return The optimized Brainfuck code.
     */
    private static String removeRedundantShifts(String code) {
        return code.replaceAll("(<>|><)", "");
    }
}

// Previous Code
/*
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String input = sc.nextLine(); // input a string to get converted into a brainfuck program that will print it
        int[] values = getUnicodeDiff(unicode(input));
        String output = "";

        for (int i = 0; i < values.length; i++) {
            output += brainfuckIncrement(Math.abs(values[i]), 1, (values[i] < 0 ? true : false));
        }

        output = removeRedundantShifts(output);
        System.out.print(output + "\n");
    }

    private static int[] factors(int n) {
        int a = -1;
        int b = -1;
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                a = i;
                b = n / i;
            }
        }
        return new int[] {a, b}; // return the median pair of factors for n
    } 

    private static int[] unicode(String input) {
        int[] unicode = new int[input.length() + 1];
        for (int i = 0; i < input.length(); i++) {
            unicode[i] = input.charAt(i); // convert each character to unicode value
        }
        unicode[input.length()] = 10; // append trailing newline
        return unicode;
    }

    private static int[] factorSearch(int n) {
        int[] factor = {0, Integer.MAX_VALUE};
        int adjustment = -1;
        for (int i = -2; i <= 2; i++) { // search for factors in a neighbourhood of +- 2 around n
            if (n + i > 0) {
                int[] factors = factors(n + i);
                if (Math.abs(factors[0] - factors[1]) < Math.abs(factor[0] - factor[1])) { // search for factors with smallest distance apart,
                    factor = factors;                                                      // should implement secondary tie break prioritising smallest adjustment factor to save a character or two in edge cases
                    adjustment = -i;
                }
            }
        }
        return new int[] {factor[0], factor[1], adjustment}; // return in the form n = factor[0] * factor[1] + adjustment
    }

    private static String brainfuckIncrement(int n, int reg, boolean decrement) {

            String output;
            String regForward = ">".repeat(reg);
            String regBackward = "<".repeat(reg);
            String symbol = decrement ? "-" : "+";

        if (n > 12) { // for larger values of n use multiplication to increment/decrement to value
            int[] values = factorSearch(n);
            int outerCount = values[0];
            int innerCount = values[1];
            int adjustment = (decrement ? -1 : 1) * values[2];

            output = "+".repeat(outerCount) // set up loop to multiply by the two factors
                + "["
                + regForward
                + symbol.repeat(innerCount)
                + regBackward
                + "-]"
                + regForward
                + (adjustment < 0 // add/subtract adjustment 
                    ? "-".repeat(-adjustment)
                    : "+".repeat(adjustment));
        } else { // for smaller values of n just use addition/subtraction since overhead of setting up a loop for multiplication uses more symbols in these cases
            output = regForward + symbol.repeat(n);
        }

        return output + "." + regBackward; // print value in register and return pointer to 0
    }

    private static int[] getUnicodeDiff(int[] unicode) {
        int[] diff = new int[unicode.length];
        int a = 0;
        for (int i = 0; i < unicode.length; i++) {
            diff[i] = unicode[i] - a;
            a = unicode[i];
        }
        return diff; // return an array of the difference between consecutive unicode values
    }

    private static String removeRedundantShifts(String brainfuck) {
        return brainfuck.replaceAll("(<>|><)", ""); // remove redundant shifts to save characters, currently only checks for single shifts which should be sufficient for current implementation
    }
}   
*/
/* old implementation before refactoring from feedback

    private static String brainfuckIncrement(int n, int reg) {
        int[] values = factorSearch(n);
        char sign = (values[2] < 0) ? '-' : '+';
        String output = "";
        
        output = stringAppend(output, "+", values[0]);
        output = stringAppend(output, "[", 1);
        output = stringAppend(output, ">", reg);
        output = stringAppend(output, "+", values[1]);
        output = stringAppend(output, "<", reg);
        output = stringAppend(output, "-]", 1);
        output = stringAppend(output, ">", reg);
        output = stringAppend(output, String.valueOf(sign), Math.abs(values[2]));
        output = stringAppend(output, "<", reg);

        return output;
    }

    private static String stringAppend(String u, String v, int n) {
        for (int i = 0; i < n; i++) {
            u += v;
        }
        return u;
    }
*/

