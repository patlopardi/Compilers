package pickle;

import pickle.Exceptions.ParserException;

import javax.print.DocFlavor;
import java.util.Hashtable;

public class Parser {
    public boolean debugExpr = false;
    public boolean debugAssign = false;
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

//                System.out.println(scan.currentToken.tokenStr);
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
                else if(scan.currentToken.tokenStr.equals("debug")){
                    String debugType = scan.getNext();
                    String debugSwitch = scan.getNext();
                    debug(debugType, debugSwitch);
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    skipTo(";");
//                    System.out.println(scan.currentToken.tokenStr);
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
    public void debug(String debugType, String debugSwitch){

        switch (debugType) {
            case "Stmt":
                if (debugSwitch.equals("off")){
                    this.scan.debugStatement = false;
                }
                else {
                    this.scan.debugStatement = true;
                }
                break;
            case "Assign":
                if (debugSwitch.equals("off")){
                    debugAssign = false;
                }
                else {
                    debugAssign = true;
                }
                break;
        }

        skipTo(";");

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
            scan.getNext();
            while(!scan.getNext().equals(";")){
                if(scan.currentToken.subClassif == SubClassif.IDENTIFIER){
                    System.out.println(" " + storage.getVariableValue(scan.currentToken.tokenStr).value);
                }
                else if(scan.currentToken.primClassif==Classif.OPERAND){
                    System.out.print(scan.currentToken.tokenStr);
                }
                else if(scan.currentToken.tokenStr.equals(",")){
                }
            }
//            System.out.println();
            skipTo(";");
//            System.out.println("still in print " + scan.currentToken.tokenStr);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        System.out.println();

//        System.out.println("still in print " + scan.currentToken.tokenStr);
    }

    public ResultValue assignment() throws Exception {
//        System.out.println("in assignemnt");
        Expr exp = new Expr(scan, storage);
        ResultValue res = null;

        if(!bExec)
            skipTo(";");
        else {
//        ResultValue res;
            if (scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                error("expected a variable for the target of an assignment ", scan.currentToken);

            String variableStr = scan.currentToken.tokenStr;
//
//// get the assignment operator and check it
//            System.out.println("befoer get next");
            scan.getNext();
//            System.out.println("afet get next");
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
//                        System.out.println("before expr");
                        res02 = exp.expr(";");

//                        System.out.println(res02.value);
                        res = storage.Assign(variableStr, res02);   // assign to target
//                        System.out.println(res.value);
                        break;
                    case "-=":
//                        System.out.println("in second case");
                        res02 = exp.expr(operatorStr);
                        // expression must be numeric, raise exception if not
                        n0p2 = new Numeric(scan, res02, " -=", "2nd Operand");
                        // Since it is numeric, we need value of target variable
                        res01 = storage.getVariableValue(variableStr);
                        // target variable must be numeric
                        n0p1 = new Numeric(scan, res01, " -=", "1st operand");
                        // subtract 2nd operand from first and assign it
//                        System.out.println(PickleUtil.Subtract(n0p1, n0p2).value);
                        //ResultValue temp = PickleUtil.Subtract(n0p1, n0p2);
                        res = storage.Assign(variableStr, PickleUtil.Subtract(n0p1, n0p2));
//                        System.out.println(res.value);
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
                        // System.out.println("terminating string of res01 " + res01.terminatingString);
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
        try {
//            System.out.println("in if");
            int saveLineNr = scan.currentToken.iSourceLineNr;
            if (bExec) {
                //        // we are executing (not ignoring)
                boolean resCond = evalCond();
                //        // Did the condition return True?
                if (resCond) {
//                    System.out.println("this is a true statement");
                    //        // Cond returned True, continue executing
                    executeStatements(true);
//                    System.out.println("back here");
//                    System.out.println(scan.currentToken.tokenStr);
                    if(scan.currentToken.tokenStr.equals(";")){
                        scan.getNext();
                    }

//                    System.out.println("out");
                    //        // what ended the statements after the true part?  Else of endif
                    if (scan.currentToken.tokenStr.equals("else")) {
                        if (!scan.getNext().equals(":"))
                            error("expected colon after else but received: ", scan.currentToken.tokenStr);
                        executeStatements(false);
//                        System.out.println("back here");
//                        System.out.println(scan.currentToken.tokenStr);
                    }
                    else if(!scan.currentToken.tokenStr.equals("endif")){
                        error("expected an endif for if found: ", scan.currentToken.tokenStr);
                    }
//                    System.out.println("in here");
                    if(!scan.getNext().equals(";"))
                        error("expected ; after endif but received: ", scan.currentToken.tokenStr);
                }
                else {
//                    System.out.println("in big else");
                    executeStatements(false);
//                    System.out.println("back here in big else");
//                    System.out.println(scan.currentToken.tokenStr);
                    if(scan.currentToken.tokenStr.equals("else")){
//                        System.out.println("executing else part");
                        if(!scan.getNext().equals(":"))
                            error("expected a : after else received: ", scan.currentToken.tokenStr);

                        executeStatements(true);
                    }
                    if(!scan.currentToken.tokenStr.equals("endif"))
                        error("expected endif for if received: ", scan.currentToken.tokenStr);
                    if(!scan.getNext().equals(";"))
                        error("expected ; after endif received: ", scan.currentToken.tokenStr);


                }
            }
            else{
                skipTo(":");
                executeStatements(false);
                if(scan.currentToken.tokenStr.equals("else")){
                    if(!scan.getNext().equals(":"))
                        error("expected a : after else received: ", scan.currentToken.tokenStr);
                    executeStatements(false);
                }
                if(!scan.currentToken.tokenStr.equals("endif"))
                    error("expected endif for if received: ", scan.currentToken.tokenStr);
                if(!scan.getNext().equals(";"))
                    error("expected ; after endif received: ", scan.currentToken.tokenStr);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void ignoreStatements(){
        try{
//            System.out.println("in ignoreStatements");
            boolean check = true;
            boolean checkForIf = false;
            scan.getNext();
            while(check){
//                System.out.println("printing: " + scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("if"))
                    checkForIf = true;
                else if((scan.currentToken.tokenStr.equals("else") || scan.currentToken.tokenStr.equals("endif"))&& !checkForIf){
//                    System.out.println("middle else if");

                    return;
                }
                else if(scan.currentToken.tokenStr.equals("endif") && checkForIf){
//                    System.out.println("last else if");
                    checkForIf = false;
                }
                scan.getNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void execute(){
        ResultValue res;
        boolean check = true;

        try{
            scan.getNext();
            while(check){
//                System.out.println("in execute while loop");
                //scan.currentToken.printToken();
//                System.out.println(scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("print")){
//                    System.out.println("in print");
                    print();
                }
                if(scan.currentToken.tokenStr.equals("if")){
//                    System.out.println("in if ");
                    ifStmt(bExec);
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    skipTo(";");
                }
                else if(scan.currentToken.tokenStr.equals("endif") || scan.currentToken.tokenStr.equals("else")){
//                    System.out.println("in last else if");
                    return;
                }
                else{
                    res = assignment();
                    //System.out.println(scan.currentToken.tokenStr);
                }
                scan.getNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void executeStatements(boolean check){
        if(!check) {
            ignoreStatements();
        }
        else{
            skipTo(":");
            execute();
        }
    }
    public boolean evalCond(){
//        System.out.println("in eval");
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
//            System.out.println(k);
//            System.out.println(res01.value);
            if(k.equals(res01.value)) {
//                System.out.println("it is true");
                //ignoreStatements();
                return true;
            }
            return false;
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
    }
}
