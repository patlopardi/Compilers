package pickle;



public class Numeric {
    public Scanner scan;
    // public resultValue res;
    public String operator;
    public String operand;

    /**
     * <p>
     * This class is for future use for classifying numeric values such as,
     *      Ints and Floats, will also handle error checking. All to implemented
     *      later
     *
     * @param scan            Scanner which will be crucial to error handling for items like line #
     * @param operator        String A string format for a operation done on the numeric
     * @param operand         String A string format for the actual item being preformed on
     *
     *
     * @return      N/A
     */

    public Numeric(Scanner scan, String operator, String operand ){
        this.operand = operand;
        this.operator = operator;
        this.scan = scan;
        // this.res = res;
    }

}
