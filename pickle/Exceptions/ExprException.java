package pickle.Exceptions;

public class ExprException extends Exception {
    String text;
    public ExprException() {
        super("Error");
    }
    public ExprException(int LineNr, String issue){
        this.text = "Error, on line " +  (LineNr + 1) + " " + issue;
        System.out.printf(text);
        System.exit(1);
    }

    @Override
    public String getMessage(){
        return text;
    }
    
}
