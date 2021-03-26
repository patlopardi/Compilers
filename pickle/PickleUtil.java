package pickle;

import java.util.Arrays;

public final class PickleUtil {

    /**
     * <p>
     * This function checks for comments on each line of code and then deletes
     *      them from the character array to be processed later in the scanner
     *
     * @param textArray  a character array that contains the character of a line in the code
     *
     * @return      char[]
     */
    public static char[] checkComments(char[] textArray){
        // Loop through text array looking for double slash
        boolean stringFlag = false;
        for(int i = 0; i < textArray.length - 1; i++){
            if(textArray[i] == '/' && textArray[i + 1] == '/' && !stringFlag){
                // if the // is at the beginning send an empty array back
                if((i - 1) > 0){
                    // Cut the comments out of the token array
                    textArray = Arrays.copyOfRange(textArray, 0, i);
                    //System.out.println("New Arr :" + String.valueOf(textArray));
                    break;
                }
                else{
                    textArray = new char[]{};
                    break;
                }
            }
            else if (textArray[i] == '\"' || textArray[i] == '\'') {
                // Swaps on when in string swaps off when meeting end quote
                stringFlag = !stringFlag;
            }
        }
        return textArray;
    }

    public static boolean checkNumericExpr(ResultValue res01, ResultValue res02){
        if ((res01.dataType == SubClassif.FLOAT || res01.dataType == SubClassif.INTEGER) &&
                (res02.dataType == SubClassif.FLOAT || res02.dataType == SubClassif.INTEGER)){
            return true;
        }
        else   {
            return false;
        }
    }

    /**
     * <p>
     * This function will return a ResultValue based on the resulting subtraction from
     *      the two operands and the type will be based off the left operand
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Subtract(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
           // Throw error
        }

        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int value = nOp1.valueToInt() - nOp2.valueToInt();
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value = nOp1.valueToDouble() - nOp2.valueToDouble();
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }

        return result;

    }

    /**
     * <p>
     * This function will return a ResultValue based on the resulting addition from
     *      the two operands and the type will be based off the left operand
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Addition(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int value = nOp1.valueToInt() + nOp2.valueToInt();
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value =  nOp1.valueToDouble() + nOp2.valueToDouble();
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }

        return result;

    }

    /**
     * <p>
     * This function will return a ResultValue based on the resulting multiplcation from
     *      the two operands and the type will be based off the left operand
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Multiply(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }

        // check left hand operand type
        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int value = nOp1.valueToInt() * nOp2.valueToInt();
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value = nOp1.valueToDouble() * nOp2.valueToDouble();
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }

        return result;

    }

    /**
     * <p>
     * This function will return a ResultValue based on the resulting division from
     *      the two operands and the type will be based off the left operand
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Divide(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }

        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int value = nOp1.valueToInt() / nOp2.valueToInt();
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value = nOp1.valueToDouble() / nOp2.valueToDouble();
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }

        return result;

    }

    /**
     * <p>
     * This function will return a ResultValue based on the resulting square from
     *      the two operands and the type will be based off the left operand
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Square(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }

        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int op1 = nOp1.valueToInt();
            int op2 = nOp2.valueToInt();
            int value = (int) Math.pow(op1, op2);
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value = Math.pow((double) nOp1.resultValue.value, nOp2.valueToDouble());
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }

        return result;

    }

    /**
     * <p>
     * This function will return true if the left operand is equivalent to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean Equivalent(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble()  == nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * This function will return true if the left operand is not equivalent to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean NotEquivalent(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble() != nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * <p>
     * This function will return true if the left operand is less than or equal to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean LessThanOrEqual(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble() <= nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * This function will return true if the left operand is greater than or equal to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean GreaterThanOrEqual(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble() >= nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * This function will return true if the left operand is less than
     *      the right operand and false if else
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean LessThan(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble() < nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * This function will return true if the left operand is greater than to
     *      the right operand and false if else
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      Boolean
     */
    public static Boolean GreaterThan(Numeric nOp1, Numeric nOp2){
        if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
            // Throw error
        }
        if (nOp1.valueToDouble() > nOp2.valueToDouble()){
            return true;
        }
        else {
            return false;
        }
    }

}
