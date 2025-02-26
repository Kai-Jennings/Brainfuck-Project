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

