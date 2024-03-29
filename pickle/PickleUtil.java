package pickle;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import pickle.Exceptions.InvalidDateException;
import pickle.Exceptions.ExprException;

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
     * @throws      ExpreException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue Subtract(Numeric nOp1, Numeric nOp2) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                int value = nOp1.valueToInt() - nOp2.valueToInt();
                result = new ResultValue(SubClassif.INTEGER, value, "","");
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                double value = nOp1.valueToDouble() - nOp2.valueToDouble();
                result = new ResultValue(SubClassif.FLOAT, value, "","");
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '-' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
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
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue Addition(Numeric nOp1, Numeric nOp2) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        try
        {
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
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '+' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
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
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue Multiply(Numeric nOp1, Numeric nOp2) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        try
        {
            // check left hand operand type
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                int value = nOp1.valueToInt() * nOp2.valueToInt();
                result = new ResultValue(SubClassif.INTEGER, value, "","");
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                double value = nOp1.valueToDouble() * nOp2.valueToDouble();
                result = new ResultValue(SubClassif.FLOAT, value, "","");
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '*' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }

        return result;

    }

    /**
     * <p>
     * This function will return a result value of an input value's unary minus
     *
     * @param nOp1  Left hand operand, the result type will match this
     * @param nOp2  Right hand operand
     *
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue UnitaryMinus(Numeric nOp1) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");

        // check left hand operand type
        if (nOp1.resultValue.dataType == SubClassif.INTEGER){
            int value = nOp1.valueToInt() * -1;
            result = new ResultValue(SubClassif.INTEGER, value, "","");
        }
        else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
            double value = nOp1.valueToDouble() * -1;
            result = new ResultValue(SubClassif.FLOAT, value, "","");
        }
        else{
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid attachment of unitary minus to non-numeric data, value is "
                    + nOp1.resultValue.value.toString());
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
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue Divide(Numeric nOp1, Numeric nOp2) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                int value = nOp1.valueToInt() / nOp2.valueToInt();
                result = new ResultValue(SubClassif.INTEGER, value, "","");
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                double value = nOp1.valueToDouble() / nOp2.valueToDouble();
                result = new ResultValue(SubClassif.FLOAT, value, "","");
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '/' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
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
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      ResultValue
     */
    public static ResultValue Square(Numeric nOp1, Numeric nOp2) throws ExprException{
        ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                int op1 = nOp1.valueToInt();
                int op2 = nOp2.valueToInt();
                int value = (int) Math.pow(op1, op2);
                result = new ResultValue(SubClassif.INTEGER, value, "","");
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                double value = Math.pow((nOp1.valueToDouble()), nOp2.valueToDouble());
                result = new ResultValue(SubClassif.FLOAT, value, "","");
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '^' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }

        return result;

    }
    /**
     * <p>
     * This function will return ResultValue of two concatenated strings
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @return      ResultValue
     */
    public static ResultValue Concatenation(Numeric nOp1, Numeric nOp2){
        ResultValue result = new ResultValue(SubClassif.STRING, 1, "", "");
        result.value = nOp1.resultValue.value.toString() + nOp2.resultValue.value.toString();
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
     * @throws      ExprException when numeric format conversion is incorrect     
     * @return      Boolean
     */
    public static Boolean Equivalent(Numeric nOp1, Numeric nOp2) throws ExprException{
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() == nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() == Double.valueOf(nOp2.resultValue.value.toString()))){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().equals(nOp2.valueToString())){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '==' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }
        return false;
    }

    /**
     * <p>
     * This function will return true if the left operand is not equivalent to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      Boolean
     */
    public static Boolean NotEquivalent(Numeric nOp1, Numeric nOp2) throws ExprException
    {
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() != nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() != Double.valueOf(nOp2.resultValue.value.toString()))){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().compareTo(nOp2.valueToString()) == 0){
                    return false;
                }
                else{
                    return true;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '!=' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }

        return false;
    }


    /**
     * <p>
     * This function will return true if the left operand is less than or equal to
     *      the right operand
     *
     * @param nOp1  Left hand operand
     * @param nOp2  Right hand operand
     *
     * @throws      ExprException when numeric format conversion is incorrect
     * @return      Boolean
     */
    public static Boolean LessThanOrEqual(Numeric nOp1, Numeric nOp2) throws ExprException{
    try {
            if(!checkNumericExpr(nOp1.resultValue, nOp2.resultValue)){
                // Throw error
            }

            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() <= nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() <= nOp2.valueToDouble())){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().compareTo(nOp2.valueToString()) <= 0){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '<=' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }

        return false;
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
    public static Boolean GreaterThanOrEqual(Numeric nOp1, Numeric nOp2) throws ExprException{
        try {
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() >= nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() >= nOp2.valueToDouble())){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().compareTo(nOp2.valueToString()) >= 0){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '>=' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }
        return false;
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
    public static Boolean GreaterThan(Numeric nOp1, Numeric nOp2) throws ExprException{
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() > nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }

            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() > nOp2.valueToDouble())){
                    return true;
                }
                else{
                    return false;
                }
            }
        
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().compareTo(nOp2.valueToString()) > 0){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '>' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }

        return false;
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
    public static Boolean LessThan(Numeric nOp1, Numeric nOp2) throws ExprException{
        try{
            if (nOp1.resultValue.dataType == SubClassif.INTEGER){
                if (nOp1.valueToInt() < nOp2.valueToInt()){
                    return true;
                }
                else {
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.FLOAT){
                if((nOp1.valueToDouble() < nOp2.valueToDouble())){
                    return true;
                }
                else{
                    return false;
                }
            }
            else if(nOp1.resultValue.dataType == SubClassif.STRING){
                if(nOp1.valueToString().compareTo(nOp2.valueToString()) < 0){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch(NumberFormatException e)
        {   
            throw new ExprException(nOp1.scan.iSourceLineNr, " invalid non-numeric data in '<' operation, regarding values "
                + nOp1.resultValue.value.toString() + " with " + nOp2.resultValue.value.toString());
        }
        return false;
    }
    /**
     * <p>
     *     This function returns an int based on the number of
     *     non-null elements in the array
     *
     * @param storage storage manager to return the stored array
     * @param variableName name of the array
     *
     * @return     int
     */

    public static int ELEM(String variableName, StorageManager storage){

        int elements = 0;
        ArrayList<ResultValue> res = storage.getArrayValue(variableName);

        for(int i = 0; i < res.size() - 1; i++ ){
            if(res.get(i) != null){
                elements++;
            }
        }

        return elements;
    }

    /**
     * <p>
     *     This function returns an int based on the number of
     *     elements in the array
     *
     * @param storage storage manager to return the stored array
     * @param variableName name of the array
     *
     * @return     int
     */
    public static int MAXELEM(String variableName, StorageManager storage){
        ArrayList<ResultValue> res = storage.getArrayValue(variableName);
        return res.size()-1;
    }

    /**
     * <p>
     *     This function returns true if the string has spaces and
     *     false if the string has none
     *
     * @param spaces evaluated string
     *
     * @return     boolean
     */
    public static boolean SPACES(String spaces){
        int count = 0;

        //Counts each character except space
        for(int i = 0; i < spaces.length(); i++) {
            if(spaces.charAt(i) == ' ')
                count++;
        }

        if(count == 0){
            return false;
        }
        else{
            return true;
        }
    }


    /**
     * <p>
     *     This function returns the length of a string
     *
     * @param len String value of variable
     *
     * @return     int
     */
    public static int LENGTH(String len){
        return len.length();
    }

    /**
    * Returns boolean value of whether string is valid as a date
    * <p>
    * Returns true if it follows the correct date format "YYYY-MM-DD"
    *     Qualities of a date include:
    *         - String must be total of 10 characters
    *         - Y,M,D must be numbers with 4 Y's, 2 M's, and 2 D's
    *         - Must be separated by hyphen
    *         - 00 < M < 13 and 00 <= D <= M's max day
    *
    * @param date  String value to be evaluated
    *
    * @return       True if follows format, False if it doesn't
    */
    public static boolean validateDate(ResultValue inputDate)
    {
        //Get the value of the date
        String date = inputDate.value.toString();
        boolean returnVal = true;
        int[] parsedDate;
        int[] monthMaxDay = new int[]{0, 31, 29, 31, 30, 31, 30, 31 ,31 ,30, 31 ,30, 31};
        try {
            //Check if Year is not 10 characters
            if(date.length() != 10)
            {
                throw new InvalidDateException("invalid size of " + date.length() + " instead of 10", date);
            }
            //Check for incorrect separators
            if(date.charAt(4) != '-' || date.charAt(7) != '-')
            {
                throw new InvalidDateException("invalid or misplaced separator.", date);
            }
            //Separate the date into year, month, day
            parsedDate = parseDate(date);
            if(parsedDate != null)
            {
                int year = parsedDate[0], month = parsedDate[1], day = parsedDate[2];
                //Check if incorrect month
                if(month > 12 || month < 1)
                {
                    throw new InvalidDateException("invalid month value, must be between 01-12.", date);
                }
                //Check if incorrect day
                if(day > monthMaxDay[month] || day < 1
                || ((month == 2 && day == 29)) && ((year%4 != 0) || (year%100 == 0 && year%400 != 0))) //If Feb 29 the year must be divisible by 4 and not divisible by 100 unless also divisible by 400
                {
                    throw new InvalidDateException("invalid day value, contains either over max or under min days for the month.", date);
                }
            }
            else
            {
                throw new InvalidDateException("invalid format, contains non-numeric value in either year, month, or day index.", date);
            }
        } catch (InvalidDateException e) {
            System.out.printf(e.getMessage() + "\n");
        }
        return returnVal;
    }

    /**
    * Adjust the date by the adjustment value
    * <p>
    *   Adds the adjustment amount to the date and return the result.
    *
    * @param date  ResultValue holding date to be adjusted
    * @param adjustment Numeric that holds the object value of type int or float coerced to int to adjust the date by days
    *
    * @return       date value or null if invalid date
    */
    public static ResultValue dateAdj(ResultValue date, Numeric days)
    {
        ResultValue res = new ResultValue(null, null, null, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int offset;
        int[] dateValues;
        //Coersion to int
        offset = days.valueToInt();

        //Validate date
        if(validateDate(date))
        {
            //res.dataType = SubClassif.DATE;
            res.dataType = SubClassif.STRING;
            dateValues = parseDate(date.value.toString());
            //Date Adjustment
            Calendar calendar = new GregorianCalendar(dateValues[0], dateValues[1]-1, dateValues[2]);
            calendar.add(Calendar.DAY_OF_MONTH, offset);
            res.value = sdf.format(calendar.getTime());

        }
        return res;
    }

    /**
    * Get the difference between two dates in days
    * <p>
    *   Get the result in days of date1-date2
    *
    * @param date1  ResultValue holding first date
    * @param date2  ResultValue holding second date
    *
    * @return       res ResulveValue holding Integer to represent of days between date2 and date1 or returns null if invalid date
    */
    public static ResultValue dateDiff(ResultValue date1, ResultValue date2)
    {
        ResultValue res = new ResultValue(null, null, null, null);
        if(validateDate(date1) && validateDate(date2))
        {
            res.dataType = SubClassif.INTEGER;
            res.value = toJulianConversion(date1.value.toString()) - toJulianConversion(date2.value.toString());
        }
        return res;
    }
    /**
    * Calculate the age in years between two dates
    * <p>
    *   Get the difference in years, truncating depending on the month and day difference
    *
    * @param date1  ResultValue holding first date
    * @param date2  ResultValue holding second date
    *
    * @return      res ResultValue holding a int to represent the age between the two dates in years or returns null if invalid date
    */
    public static ResultValue dateAge(ResultValue date1, ResultValue date2)
    {
        ResultValue res = new ResultValue(null, null, null, null);
        if(validateDate(date1) && validateDate(date2))
        {
            int difference = (int)dateDiff(date1, date2).value;
            res.dataType = SubClassif.INTEGER;
            if(Math.abs(difference) == 365) //Solve for exact division
            {
                res.value = Integer.signum(difference);
            }
            else
            {
                res.value = (int)(difference/365.2425);
            }
        }
        return res;
    }

    /**
    * Parse the year, month, and day into integer values and return array of them
    * <p>
    *   Return a valid date's year, month, and day within an array of integers
    *
    * @param date  String value holding date to be parsed
    *
    * @return      returns either null if unable to parse or returns an array of integers representing year[0], month[1], and day[2]
    */
    public static int[] parseDate(String date)
    {
        int[] dateArr;
        try {
            dateArr = new int[]{Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5,7)), Integer.parseInt(date.substring(8, 10))};
        }
        catch(Exception e)
        {
            return null;
        }
        return dateArr;
    }
    /**
    * Converts a date into it's julian value
    * <p>
    *   Returns int value of the date converted to julian
    *
    * @param date  String value holding date to be converted
    *
    * @return      returns the conversion of date to julian
    */
    public static int toJulianConversion(String date)
    {
        int[] dateArr = parseDate(date); //[0]Year [1]Month [2]Day
        int iCountDays;
        if(dateArr[1] > 2)
        {
            dateArr[1] -= 3;
        }
        else
        {
            dateArr[1] += 9;
            dateArr[0]--;
        }
        iCountDays = 365 * dateArr[0]
            + dateArr[0]/4 - dateArr[0] / 100 + dateArr[0] / 400
            + (dateArr[1] * 306 + 5) / 10
            + (dateArr[2]);
        return iCountDays;
    }


}

