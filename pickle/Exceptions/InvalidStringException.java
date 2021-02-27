package pickle.Exceptions;

public class InvalidStringException extends Exception{
    public int colIndex;
    public int lineNumber;
    public String invalidString;

    /**
     *
     * @param invalidString   String The invalid literal beginning to be fully populated in getMessage()
     * @param colIndex        Int Needed to print the whole value of invalidLiteral
     * @param lineNumber      Int Needed to return the line number where the error occured
     *
     *
     * @return      N/A
     */
    public InvalidStringException(String invalidString, int colIndex, int lineNumber){
        this.invalidString = invalidString;
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
        String out = "Missing closing quote \"" + invalidString + "... on line " + lineNumber;
        return out;
    }

}
