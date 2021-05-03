package pickle.Exceptions;

public class ExprException extends Exception {
    String text;
    public ExprException() {
        super("Error");
    }
    public ExprException(int LineNr, String issue){
        super("Error, Line " +  LineNr + issue);
        this.text = "\nError, on line " +  (LineNr + 1) + " " + issue;
    }

    @Override
    public String getMessage(){
        return text;
    }
    
}
