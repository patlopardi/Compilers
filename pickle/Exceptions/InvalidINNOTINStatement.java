package pickle.Exceptions;

public class InvalidINNOTINStatement extends Exception{

    public char[] textArr;
    public int colIndex;
    public int lineNumber;
    public String invalidLiteral;
    /**
     *
     * @param invalidLiteral  String The invalid literal beginning to be fully populated in getMessage()
     * @param textArr         char[] A char array that stores the whole line
     * @param colIndex        Int Needed to print the whole value of invalidLiteral
     * @param lineNumber      Int Needed to return the line number where the error occured
     *
     *
     * @return      N/A
     */
    public InvalidINNOTINStatement(String invalidLiteral, char[] textArr, int colIndex, int lineNumber){
        this.invalidLiteral = invalidLiteral;
        this.textArr = textArr;
        this.colIndex = colIndex;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage(){
        String out;
        for(int i = colIndex; i < textArr.length; i++) {
            if (!Character.isAlphabetic(textArr[i])) {
                break;
            }
            invalidLiteral += textArr[i];
        }
        out = "Invalid IN/NOTIN \'" + invalidLiteral + "\' on line " + lineNumber;
        return out;
    }

}
