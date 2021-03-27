package pickle.Exceptions;

public class ParserException extends Exception{
    public int lineNr;
    public String text;
    public String fileNm;


    /**
     *
     *
     *
     *
     * @return      N/A
     */
    public ParserException(int lnNr, String txt, String flNm){
        this.lineNr = lnNr;
        this.text = txt;
        this.fileNm = flNm;
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
        out = text + lineNr;
        return out;
    }

}
