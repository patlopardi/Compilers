package pickle;

public class Expr {
    public Scanner scan;
    
    public Expr() {
      
    }

    public ParseResult evaluateInfix(Token tokenVariable, Scanner scanner){
        this.scan = scanner;
        ParseResult pExpressionHead = new ParseResult("expression");
        try{
        scan.getNext();
        }
        catch(Exception e)
        {
        }
        Token nextToken = scan.currentToken;
        return pExpressionHead;
    }
}