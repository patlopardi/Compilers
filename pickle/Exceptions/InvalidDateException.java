package pickle.Exceptions;

public class InvalidDateException extends Exception
{
    public InvalidDateException() {
        super("Invalid Date");
    }
    public InvalidDateException(String error, String date){
        super("ERROR Incorrect Date Format. \""+ date +"\" has " + error);
    }
}
