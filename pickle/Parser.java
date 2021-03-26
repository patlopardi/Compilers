package pickle;

import java.util.Hashtable;

public class Parser {
    public Scanner scan;
    public SymbolTable symbolT;
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
        
        //General Case
        Expr exp = new Expr(scan);
        try{
                //Skips past the variable and assignment (ex: foo =)
                scan.getNext();
                scan.getNext();
                exp.expr(";");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        //skipTo(";");

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
