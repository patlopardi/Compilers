package pickle.Exceptions;

public class InvalidDeclarationException extends Exception{


    public char[] textArr;
    public int colIndex;
    public int lineNumber;
    public String invalidDeclaration;

    /**
     *
     * @param invalidDeclaration String The invalid literal beginning to be fully populated in getMessage()
     * @param textArr         char[] A char array that stores the whole line
     * @param colIndex        Int Needed to print the whole value of invalidDeclaration
     * @param lineNumber      Int Needed to return the line number where the error occured
     *
     *
     * @return      N/A
     */
    public InvalidDeclarationException(String invalidDeclaration, char[] textArr, int colIndex, int lineNumber){
        this.invalidDeclaration = invalidDeclaration;
        this.textArr = textArr;
        this.colIndex = colIndex;
        this.lineNumber = lineNumber;
    }

    /**
     * <p>
     * This function overrides the getMessage() function for the Exception class
     *      and loops through the invalid literal until a non alphabetic character
     *      is found to return the full invalid literal to the user
     *
     * @Override    Used to override the getMessage class in the Exception class
     * @return      String that will be the full error message
     */
    @Override
    public String getMessage(){
        String out;
        for(int i = colIndex; i < textArr.length; i++) {
            if (!Character.isAlphabetic(textArr[i])) {
                break;
            }
            invalidDeclaration += textArr[i];
        }
        out = "Invalid Declartaion \'" + invalidDeclaration+ "\' on line " + lineNumber;
        return out;
    }

}
