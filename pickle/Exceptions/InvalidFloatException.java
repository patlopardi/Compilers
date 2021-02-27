package pickle.Exceptions;
public class InvalidFloatException extends Exception{
    public char[] textArr;
    public int colIndex;
    public int lineNumber;
    public String invalidFloat;

    /**
     *
     * @param invalidFloat    String The invalid literal beginning to be fully populated in getMessage()
     * @param textArr         char[] A char array that stores the whole line
     * @param colIndex        Int Needed to print the whole value of invalidLiteral
     * @param lineNumber      Int Needed to return the line number where the error occured
     *
     *
     * @return      N/A
     */
    public InvalidFloatException(String invalidFloat, char[] textArr, int colIndex, int lineNumber){
        this.invalidFloat = invalidFloat;
        this.textArr = textArr;
        this.colIndex = colIndex;
        this.lineNumber = lineNumber;
    }

    /**
     * <p>
     * This function overrides the getMessage() function for the Exception class
     *      and loops through the invalid literal until a non digit character
     *      is found to return the full invalid float to the user
     *
     * @Override    Used to override the getMessage function in the Exception class
     * @return      String that will be the full error message
     */
    @Override
    public String getMessage(){
        String out;
        for(int i = colIndex; i < textArr.length; i++) {
            if (!Character.isDigit(textArr[i]) && textArr[i] != '.' ) {
                break;
            }
            invalidFloat += textArr[i];
        }
        out = "Invalid Float \'" + invalidFloat + "\' on line " + lineNumber;
        return out;
    }
}
