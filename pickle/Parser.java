package pickle;

import pickle.Exceptions.ParserException;

import javax.print.DocFlavor;
import java.util.Hashtable;

public class Parser {
    public Scanner scan;
    public SymbolTable symbolT;
    public StorageManager storage = new StorageManager();
    boolean bExec = true;

    public Parser(Scanner scanner, SymbolTable symbolTable){
        this.scan = scanner;
        this.symbolT = symbolTable;
        statement();

    }

    public void statement(){
        ResultValue res;

        try{
            while(! scan.getNext().isEmpty() ){
                //scan.currentToken.printToken();
                System.out.println(scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("print")){
                    print();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
                    ifStmt(bExec);
                }
                else if(scan.currentToken.tokenStr.equals("while")){
                    whileStmt();
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    skipTo(";");
                }
                else{
                    res = assignment();
                    //System.out.println(scan.currentToken.tokenStr);
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void error(String fmt, Object varArgs) throws Exception
    {
        String diagnosticTxt = String.format(fmt, varArgs);
        throw new ParserException(scan.iSourceLineNr
                , diagnosticTxt
                , scan.fileNm);
    }

    private void print() {
        try{
            scan.getNext(); //moves to left parenth
            while(! scan.getNext().equals(")")){
                if (scan.currentToken.subClassif==SubClassif.IDENTIFIER){
                System.out.printf("%s", storage.getVariableValue(scan.currentToken.tokenStr).value);
                }else if (scan.currentToken.primClassif==Classif.OPERAND){
                    System.out.printf(scan.currentToken.tokenStr);
                }else if (scan.currentToken.tokenStr.equals(",")){
                }
            }
            System.out.printf("\n");
            skipTo(";");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
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

    public ResultValue assignment() throws Exception {
        System.out.println("in assignemnt");
        Expr exp = new Expr(scan, storage);
        ResultValue res = null;

        if(!bExec)
            skipTo(";");
        else {
            System.out.println("in else");
//        ResultValue res;
            if (scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                error("expected a variable for the target of an assignment", scan.currentToken.tokenStr);

            String variableStr = scan.currentToken.tokenStr;
//
//// get the assignment operator and check it
            System.out.println("befoer get next");
            scan.getNext();
            System.out.println("afet get next");
            if (scan.currentToken.primClassif != Classif.OPERATOR)
                error("expected assignment operator", scan.currentToken.tokenStr);
//
            String operatorStr = scan.currentToken.tokenStr;
            ResultValue res02;
            ResultValue res01;
            Numeric n0p2;  // numeric value of second operand
            Numeric n0p1;  // numeric value of first operand
            //System.out.println("in assignment");
                switch (operatorStr) {
                    case "=":
                        System.out.println("before expr");
                        res02 = exp.expr(";");

                        System.out.println(res02.value);
                        res = storage.Assign(variableStr, res02);   // assign to target
                        System.out.println(res.value);
                        break;
                    case "-=":
                        System.out.println("in second case");
                        res02 = exp.expr(operatorStr);
                        // expression must be numeric, raise exception if not
                        n0p2 = new Numeric(scan, res02, " -=", "2nd Operand");
                        // Since it is numeric, we need value of target variable
                        res01 = storage.getVariableValue(variableStr);
                        // target variable must be numeric
                        n0p1 = new Numeric(scan, res01, " -=", "1st operand");
                        // subtract 2nd operand from first and assign it
                        System.out.println(PickleUtil.Subtract(n0p1, n0p2).value);
                        //ResultValue temp = PickleUtil.Subtract(n0p1, n0p2);
                        res = storage.Assign(variableStr, PickleUtil.Subtract(n0p1, n0p2));
                        System.out.println(res.value);
                        break;
                    case "+=":
                        res02 = exp.expr(operatorStr);
                        // expression must be numeric, raise exception if not
                        n0p2 = new Numeric(scan, res02, " +=", " nd Operand");
                        // Since it is numeric, we need value of target variable
                        res01 = storage.getVariableValue(variableStr);
                        // target variable must be numeric
                        n0p1 = new Numeric(scan, res01, " +=", " st operand");
                        // subtract 2nd operand from first and assign it
                        res = storage.Assign(variableStr, PickleUtil.Addition(n0p1, n0p2));
//                        System.out.println("terminating string of res01 " + res01.terminatingString);
                        break;
                    default:
                        error("expected assignment operator", operatorStr);
                        break;
                }
//            System.out.println("after switch");
        }

        return res;
    }

    private void ifStmt(boolean bExec) {
        System.out.println("in if");
        int saveLineNr = scan.currentToken.iSourceLineNr;
        if(bExec) {
//        // we are executing (not ignoring)
            boolean resCond = evalCond();
//        // Did the condition return True?
            if(resCond) {
//        // Cond returned True, continue executing
                executeStatements(true);
//        // what ended the statements after the true part?  Else of endif
//        if resTemp.terminatingStr == “else”:
//        //toss the colon
//        If scan.getNext() != ‘:’:
//        errorWithCurrent(“expected ‘:’ after ‘else’”);
//        endif;

//        resTemp = executeStatements(F); // since the condition is false
//        endif;
//        if resTemp.terminatingStr != “endif”:
//        error("expected an ‘endif’ for ‘if’ beginning at line %d, found ‘%s’",                                                               saveLineNr, resTemp.terminatingStr);
//        endif;
//        // endif should be followed by a semicolon
//        If scan.getNext() != ‘;’:
//        errorWithCurrent(“expected ‘;’ after ‘endif’”);
//         else:
//        // Cond returned False, ignore execution
//        ResaultValue resTemp = executeStatements(F); //since the condition was false
//        // the true statements were terminated by an else or endif
//        if resTemp.terminatingStr == “else”:
//        //toss the colon
//        If scan.getNext() != ‘:’:
//        errorWithCurrent(“expected ‘:’ after ‘else’”);
//        endif;
//        // Since the IF’s condition was false, we want to execute the else part
//        // (when present)
//        resTemp = executeStatements(T);
//        endif;
//        if resTemp.terminatingStr != “endif”:
//        error("expected an ‘endif’ for ‘if’ beginning at line %d, found ‘%s’",                                                               saveLineNr, resTemp.terminatingStr);
//        endif;
//        // endif should be followed by a semicolon
//        If scan.getNext() != ‘;’:
//        errorWithCurrent(“expected ‘;’ after ‘endif’”);
//        endif;
            }
//    else
//        // we are ignoring execution
//        // we want to ignore the conditional, true part, and false part
//        // Should we execute evalCond ?
//        SkipTo(“:”);
//        resTemp = executeStatements(F);
//        // should be terminated by else or endif
//        if resTemp.terminatingStr == “else”:
//        //toss the colon
//        If scan.getNext() != ‘:’:
//        errorWithCurrent(“expected ‘:’ after ‘else’”);
//        endif
//                // we are ignoring the if
//                resTemp = executeStatements(F);
//        endif;
//        if resTemp.terminatingStr != “endif”:
//        error("expected an ‘endif’ for ‘if’ beginning at line %d, found ‘%s’",                                                               saveLineNr, resTemp.terminatingStr);
//        endif;
//        // endif should be followed by a semicolon
//        If scan.getNext() != ‘;’:
//        errorWithCurrent(“expected ‘;’ after ‘endif’”);
//        endif;
        }
    }
    public void ignoreStatements(){
        try{
            boolean check = true;
            boolean checkForIf = false;
            while(check){
                scan.getNext();
                if(scan.currentToken.tokenStr.equals("if"))
                    checkForIf = true;
                else if((scan.currentToken.tokenStr.equals("else") || scan.currentToken.tokenStr.equals("endif"))&& !checkForIf){
                    check = false;
                }
                else if(scan.currentToken.tokenStr.equals("endif") && checkForIf){
                    checkForIf = false;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void executeStatements(boolean check){
        if(!check){
            ignoreStatements();
        }
    }
    public boolean evalCond(){
        System.out.println("in eval");
        boolean check = false;
        Expr exp = new Expr(scan, storage);
        ResultValue res01;
        ResultValue res02;
        String t;

        try{
            scan.getNext();
            res01 = storage.getVariableValue(scan.currentToken.tokenStr);
            scan.getNext();
            scan.getNext();
            Object k = scan.currentToken.tokenStr;
            if(k != res01.value)
                return false;
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void whileStmt() {
        //System.out.println("in while");
        skipTo(";");
    }


    public void skipTo(String stopToken){
        while(!scan.currentToken.tokenStr.equals(stopToken)){
            try{
                //System.out.println("in here");
                scan.getNext();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
//        try {
//            scan.getNext();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
