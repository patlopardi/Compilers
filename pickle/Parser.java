package pickle;

import java.util.Hashtable;

public class Parser {
    public Scanner scan;
    public SymbolTable symbolT;
    public StorageManager storage = new StorageManager();
    public Parser(Scanner scanner, SymbolTable symbolTable){
        this.scan = scanner;
        this.symbolT = symbolTable;
        statement();

    }
    public void statement(){
        Token holder;
        try{
            while(! scan.getNext().isEmpty() ){
                //scan.currentToken.printToken();
                //System.out.println(scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("print")){
                    print();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
                    ifStmt();
                    continue;
                }
                else if(scan.currentToken.tokenStr.equals("while")){
                    whileStmt();
                    continue;
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    skipTo(";"); //why isnt this skipping to ;
                    continue;
                }
                else{
                    assignment();
                    
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void print() {
        try{
            scan.getNext();
            scan.getNext();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(scan.currentToken.tokenStr);
        skipTo(";");
    }

    private void ifStmt() {
        System.out.println("in if");
        skipTo(";");
    }

    private void whileStmt() {
        System.out.println("in while");
        skipTo(";");
    }

    public void assignment(){
        //Temporary things for testing the assignment
        Expr exp = new Expr(scan, storage);
        try{
                //Sets the variable
                /*String var = scan.currentToken.tokenStr;
                //Assuming just normal = for testing
                String equalVar = scan.currentToken.tokenStr;
                scan.getNext();
                ResultValue result = exp.expr(";");
                storage.Declare(var, result);
                */
                //The handle for different types of =
                /*if(equalVar.equals("="))
                {
                  storage.Declare(var, result);
                }
                else if(equalVar.equals("+="))
                {
                  storage.Declare(var, PickleUtil.Addition())
                }*/
                
            }
            catch (Exception e){
                e.printStackTrace();
            }
        skipTo(";");

    }
    public void skipTo(String stopToken){
        while(!scan.currentToken.tokenStr.equals(stopToken)){
            try{
                System.out.println("in here");
                scan.getNext();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            scan.getNext();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
