package pickle;

import pickle.Exceptions.ParserException;

import javax.print.DocFlavor;
import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Parser {
    public boolean debugExpr = false;
    public boolean debugAssign = false;
    public Scanner scan;
    public SymbolTable symbolT;
    public StorageManager storage = new StorageManager();
    boolean bExec = true;
    boolean forCheck = false;
    List<Token> tokens = new ArrayList<>();



    //constructor
    public Parser(Scanner scanner, SymbolTable symbolTable){
        this.scan = scanner;
        this.symbolT = symbolTable;
        statement();

    }

    /**
     * statement loops through each token and executes them
     *
     */
    public void statement(){
        ResultValue res;
        Expr exp = new Expr(scan, storage);

        try{
            while(! scan.getNext().isEmpty() ){
                //scan.currentToken.printToken();

                //scan.currentToken.printToken();
                if(scan.currentToken.tokenStr.equals("print")){
                    print();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
                    ifStmt(bExec);
                }
//                else if(scan.currentToken.tokenStr.equals("=") || scan.currentToken.tokenStr.equals("]")){
//                    //counter in the while for index
//                    int count = 0;
//                    System.out.println("in this case");
//                    while(!scan.getNext().equals(";")){
//                        String index = String.valueOf(count);
//                        //res = exp.expr(",)", debugExpr);
//                        count++;
//                    }
//
//
//
//                    skipTo(";");
//                    //storage.AssignArr(index);
//                }
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
                    scan.getNext();
                    scan.getNext();
                    boolean check = false;
                    if(!scan.currentToken.tokenStr.equals(";")){
                        check=true;
                    }
                    scan.iSourceLineNr -= 1;
                    scan.iColPos = 10000;
                    scan.getNext();
                    //System.out.println(scan.currentToken.tokenStr);
                    scan.getNext();
                    if(check){
                        res = assignment();
                    }
                    else{
                        skipTo(";");
                    }
                }
                else if(scan.currentToken.tokenStr.equals("for")){
                    forStmt();
                }
                else{
                    res = assignment();
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

        /**
         * adds debugging aids to the output
         */
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
            case "Expr":
                if (debugSwitch.equals("off")){
                    debugExpr = false;
                }
                else {
                    debugExpr = true;
                }
                break;
        }

        skipTo(";");

    }
    /**
     * error throws an exception
     *
     * @param fmt    a string to print out
     * @param varArgs     the object where the error was incountered
     *
     * @return      N/A
     */
    public void error(String fmt, Object varArgs) throws Exception
    {
        String diagnosticTxt = String.format(fmt, varArgs);
        throw new ParserException(scan.iSourceLineNr
                , diagnosticTxt
                , scan.fileNm);
    }

    /**
     * prints out the string literal and any variables
     *
     * @return      N/A
     */
    private void print() {
        //TODO: work on print
        Expr exp = new Expr(scan, storage);
        try{
            scan.getNext();
            while(!scan.getNext().equals(";")){
                System.out.printf("%s", exp.expr(",)", debugExpr).value);
            }
            skipTo(";");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("");

    }
    /**
     * assigns a value to a variable
     *
     * @return      ResultValue which is the result of the assignment
     * @throws  Exception
     */
    public ResultValue assignment() throws Exception {
//        System.out.println("in assignemnt");
//        System.out.println("current token: " + scan.currentToken.tokenStr);
//        scan.getNext();
//        System.out.println("current token: " + scan.currentToken.tokenStr);
        Expr exp = new Expr(scan, storage);
        ResultValue res = null;

        if(!bExec)
            skipTo(";");
        else {
//            System.out.println("in else");
//        ResultValue res;
            if (scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                error("expected a variable for the target of an assignment ", scan.currentToken);

            String variableStr = scan.currentToken.tokenStr;
            scan.getNext();
            //tokens.add(scan.currentToken);
            //scan.currentToken.printToken();
            if (scan.currentToken.primClassif != Classif.OPERATOR && !scan.currentToken.tokenStr.equals("["))
                error("expected assignment operator", scan.currentToken.tokenStr);
            String operatorStr = scan.currentToken.tokenStr;
            ResultValue res02;
            ResultValue res01;
            Numeric n0p2;  // numeric value of second operand
            Numeric n0p1;  // numeric value of first operand

            switch (operatorStr) {
                case "=":
//                    System.out.println("in this case");
                    if(forCheck) {
//                        System.out.println("in here");
                        res02 = exp.expr("to", debugExpr);
                    }
                    else{
                        res02 = exp.expr(";", debugExpr);
                    }

                    res = storage.Assign(variableStr, res02);   // assign to target

                    break;
                case "-=":
                    res02 = exp.expr(operatorStr, debugExpr);
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
                    res02 = exp.expr(operatorStr, debugExpr);
                    // expression must be numeric, raise exception if not
                    n0p2 = new Numeric(scan, res02, " +=", " nd Operand");
                    // Since it is numeric, we need value of target variable
                    res01 = storage.getVariableValue(variableStr);
                    // target variable must be numeric
                    n0p1 = new Numeric(scan, res01, " +=", " st operand");
                    // subtract 2nd operand from first and assign it
                    res = storage.Assign(variableStr, PickleUtil.Addition(n0p1, n0p2));
                    break;
                case "[":
                    if(storage.getArrayValue(variableStr) == null){
                        System.out.println("this is not declared");
                        String index = scan.getNext();
                        storage.DeclareArr(scan, variableStr, index, SubClassif.INTEGER);
                        scan.iSourceLineNr-=1;
                        scan.iColPos=1000;
                        break;
                    }
                    else if(storage.getArrayValue(variableStr) != null){
                        //counter in the while for index
                        int count = 0;
                        System.out.println("in this case");
                        skipTo("=");
                        boolean check = false;
                        while(!scan.currentToken.tokenStr.equals(";")){
//                            scan.currentToken.printToken();
//                            if(!check){
//                                check =true;
//                            }
//                            else{
//                                scan.getNext();
//                            }

                            String index = String.valueOf(count);
                            res = exp.expr(";", debugExpr);
                            storage.AssignArr(variableStr, index, res);
                            System.out.println(storage.getArrayValue(variableStr).get(count).value);
//                            System.out.println("current token: " + res.value);
                            if(scan.currentToken.tokenStr.equals(";")){
                                break;
                            }
                            scan.getNext();


//                            count++;
                        }
                        System.out.println("out here");
//                        res = exp.expr(";", debugExpr);
//                        scan.getNext();
//                        System.out.println("current token: " + res.value);
//                        res = exp.expr(";", debugExpr);
//                        scan.getNext();
//                        System.out.println("current token: " + res.value);
//                        res = exp.expr(";", debugExpr);
//                        System.out.println("current token: " + res.value);
//                        scan.currentToken.printToken();
//                        System.out.printf("%s\n", exp.expr(",", debugExpr).value);
//                        scan.getNext();
//                        System.out.printf("%s", exp.expr(",)", debugExpr).value);
//                        scan.getNext();
//                        System.out.printf("%s", exp.expr(",)", debugExpr).value);
//                        System.out.printf("%s", exp.expr(",", debugExpr).value);
//                        System.out.printf("%s", exp.expr(",", debugExpr).value);
//                        scan.currentToken.printToken();
//                        while(!scan.getNext().equals(";")){
//                            System.out.printf("%s", exp.expr(",)", debugExpr).value);
////                            String index = String.valueOf(count);
////                            res = exp.expr(",)", debugExpr);
////                            storage.AssignArr(variableStr, index, res);
////                            System.out.println(storage.getArrayValue(variableStr).get(count).value);
////                            count++;
//                        }
                        //scan.currentToken.printToken();
                        //storage.AssignArr(index);
                    }


                    break;
                default:
                    error("expected assignment operator received instead: ", operatorStr);
                    break;
            }

//            System.out.println("after switch");
        }

        return res;
    }
    /**
     * handles the if statements within pickle and executes them
     *
     *
     * @return      N/A
     */
    private void ifStmt(boolean bExec) {
        try {
            int saveLineNr = scan.currentToken.iSourceLineNr;
            if (bExec) {
                boolean resCond = evalCond();
                if (resCond) {
                    executeStatements(true);
                    if(scan.currentToken.tokenStr.equals(";")){
                        scan.getNext();
                    }
                    if (scan.currentToken.tokenStr.equals("else")) {
                        if (!scan.getNext().equals(":"))
                            error("expected colon after else but received: ", scan.currentToken.tokenStr);
                        executeStatements(false);
                    }
                    else if(!scan.currentToken.tokenStr.equals("endif")){
                        error("expected an endif for if found: ", scan.currentToken.tokenStr);
                    }
                    if(!scan.getNext().equals(";"))
                        error("expected ; after endif but received: ", scan.currentToken.tokenStr);
                }
                else {
                    executeStatements(false);
                    if(scan.currentToken.tokenStr.equals("else")){
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
    /**
     * handles while statements and executes them
     *
     * @return      N/A
     */
    public void whileStmt() {
        int saveLineNr = scan.iSourceLineNr;

        int saveLineAfter=0;

        boolean flag = false;
        try{

            boolean resCond = evalCond();
            while (resCond) {
                if(flag){
                    resCond = evalCond();
                    if(!resCond){

                        break;
                    }
                }
                flag = true;
                executeStatements(true);
                scan.iSourceLineNr = saveLineNr - 1;
                scan.iColPos=1000;
                scan.getNext();
            }
            skipStatements();
            if(!scan.currentToken.tokenStr.equals("endwhile"))
                error("expected endwhile for while received: ", scan.currentToken.tokenStr);
            if(!scan.getNext().equals(";"))
                error("expected ; after endwhile received: ", scan.currentToken.tokenStr);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void forStmt(){
        //TODO: add for each element in an array
        try{
            ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
            int saveLineNr = scan.iSourceLineNr;
            String saveToken;
            String saveToken2;
            tokens.clear();
            Expr exp = new Expr(scan, storage);
            forCheck=true;
            boolean forType1 = false;
            boolean forType2 = false;
            ResultValue res02;
            ResultValue res01;
            ResultValue res03;
            ResultValue res04;
            ResultValue res05;
            boolean checkCond = false;
            Numeric n0p2;  // numeric value of second operand
            Numeric n0p1;  // numeric value of first operand
            Numeric n0p4;
            Numeric n0p3;
            ResultValue res;
            boolean forType3 = false;
            scan.getNext();
            if(scan.getNext().equals("=")){
                //System.out.println("in forType1 or 2");
                scan.getNext();
                scan.getNext();
                scan.getNext();
                if(scan.getNext().equals("by")){
                    forType1=true;
                }
                else{
                    forType2=true;
                }
            }
            else if(scan.currentToken.tokenStr.equals("in")){
                //System.out.println("in forType3");
                forType3=true;
            }
            else{
                error("incorrect for form: ", scan.currentToken.tokenStr);
            }
            scan.iSourceLineNr=saveLineNr - 1;
            scan.iColPos=10000;
            scan.getNext();
            //System.out.println(scan.currentToken.tokenStr);
            if(forType1){
                scan.getNext();
                saveToken = scan.currentToken.tokenStr;

                //System.out.println("right after exp: " + scan.currentToken.tokenStr);

                scan.iSourceLineNr-=1;
                scan.iColPos=1000;
                scan.getNext();
                scan.getNext();
                assignment();
                scan.getNext();
                res04 = exp.expr("by", debugExpr);
                n0p4 = new Numeric(scan, res04, " <", "2nd Operand");
                res03 = storage.getVariableValue(saveToken);
                n0p3 = new Numeric(scan, res03, " <", " st operand");
                checkCond = PickleUtil.LessThan(n0p3, n0p4);
                //System.out.println(checkCond);
                //System.out.println(scan.currentToken.tokenStr);
                //System.out.println(scan.currentToken.tokenStr);
                scan.getNext();
                //System.out.println(scan.currentToken.tokenStr);
                res02 = exp.expr(":", debugExpr);
                n0p2 = new Numeric(scan, res02, " +=", " nd Operand");
                res01 = storage.getVariableValue(saveToken);
                n0p1 = new Numeric(scan, res01, " +=", " st operand");
                int saveLineNr2 = scan.iSourceLineNr;
                while(checkCond){
                    scan.iSourceLineNr = saveLineNr2;
                    scan.iColPos=1000;
                    scan.getNext();
                    //System.out.println("before executing token: " + scan.currentToken.tokenStr);
                    executeForStmt();
                    //increment
                    res01 = storage.getVariableValue(saveToken);
                    n0p1 = new Numeric(scan, res01, " +=", " st operand");
                    res = storage.Assign(saveToken, PickleUtil.Addition(n0p1, n0p2));
                    //compare
                    res03 = storage.getVariableValue(saveToken);
                    n0p3 = new Numeric(scan, res03, " <", " st operand");
                    checkCond = PickleUtil.LessThan(n0p3, n0p4);
                }
                if(!scan.currentToken.tokenStr.equals("endfor"))
                    error("expected endfor for for received: ", scan.currentToken.tokenStr);
                if(!scan.getNext().equals(";"))
                    error("expected ; after endfor received: ", scan.currentToken.tokenStr);
            }
            else if(forType2){
                scan.getNext();
                saveToken = scan.currentToken.tokenStr;

                //System.out.println("right after exp: " + scan.currentToken.tokenStr);

                scan.iSourceLineNr-=1;
                scan.iColPos=1000;
                scan.getNext();
                scan.getNext();
                assignment();
//                tokens.add(scan.currentToken);
                System.out.println(scan.currentToken.tokenStr);
                scan.getNext();
                res04 = exp.expr(":", debugExpr);
                n0p4 = new Numeric(scan, res04, " <", "2nd Operand");
                res03 = storage.getVariableValue(saveToken);
                n0p3 = new Numeric(scan, res03, " <", " st operand");
                checkCond = PickleUtil.LessThan(n0p3, n0p4);
                n0p2 = new Numeric(scan, result, " +=", " nd Operand");
                res01 = storage.getVariableValue(saveToken);
                n0p1 = new Numeric(scan, res01, " +=", " st operand");
                int saveLineNr2 = scan.iSourceLineNr;
                while(checkCond){
                    scan.iSourceLineNr = saveLineNr2;
                    scan.iColPos=1000;
                    scan.getNext();
                    //System.out.println("before executing token: " + scan.currentToken.tokenStr);
                    executeForStmt();
                    //increment
                    res01 = storage.getVariableValue(saveToken);
                    n0p1 = new Numeric(scan, res01, " +=", " st operand");
                    res = storage.Assign(saveToken, PickleUtil.Addition(n0p1, n0p2));
                    //compare
                    res03 = storage.getVariableValue(saveToken);
                    n0p3 = new Numeric(scan, res03, " <", " st operand");
                    checkCond = PickleUtil.LessThan(n0p3, n0p4);
                }
                if(!scan.currentToken.tokenStr.equals("endfor"))
                    error("expected endfor for for received: ", scan.currentToken.tokenStr);
                if(!scan.getNext().equals(";"))
                    error("expected ; after endfor received: ", scan.currentToken.tokenStr);
            }
            else if(forType3){
                scan.getNext();
                saveToken = scan.currentToken.tokenStr;
                scan.getNext();
                res01 = exp.expr(":", debugExpr);
//                System.out.println(res01.value.toString());
                char [] temp = new char[1000];
                temp = res01.value.toString().toCharArray();
                ResultValue rv = new ResultValue(SubClassif.STRING, "", "", "");
                //System.out.println(temp[0]);
                scan.getNext();
                int saveLineNr2 = scan.iSourceLineNr;
                for(char i: temp){
                    scan.iSourceLineNr = saveLineNr2-1;
                    scan.iColPos=1000;
                    scan.getNext();

                    rv.value = i;
                    res = storage.Assign(saveToken, rv);
                    executeForStmt();
                }
                if(!scan.currentToken.tokenStr.equals("endfor"))
                    error("expected endfor for 'for' received: ", scan.currentToken.tokenStr);
                if(!scan.getNext().equals(";"))
                    error("expected ; after endfor received: ", scan.currentToken.tokenStr);
                //System.out.println(scan.currentToken.tokenStr);
            }
            //System.out.println(scan.currentToken.tokenStr);
            //if(sc
            //an.currentToken.tokenStr.equals())
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void executeForStmt(){
        try{
            ResultValue res;
            while(true){
//                System.out.println("token in executeFor " + scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("print")){
//                    System.out.println("in print");
                    print();
                    scan.getNext();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
//                    System.out.println("in if ");
                    ifStmt(bExec);
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    scan.getNext();
                    scan.getNext();
                    boolean check = false;
                    if(!scan.currentToken.tokenStr.equals(";")){
                        check=true;
                    }
                    scan.iSourceLineNr -= 1;
                    scan.iColPos = 10000;
                    scan.getNext();
                    //System.out.println(scan.currentToken.tokenStr);
                    scan.getNext();
                    if(check){
                        res = assignment();
                    }
                    else{
                        skipTo(";");
                    }
                }
                else if(scan.currentToken.tokenStr.equals("endfor")){
                    return;
                }
                else if(scan.currentToken.tokenStr.equals("while")){
                    whileStmt();
                }
                else if(scan.currentToken.tokenStr.equals("for")){
                    forStmt();
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
    /**
     * skips statements until it gets to endwhile
     *
     * @return      N/A
     */
    public void skipStatements(){
        try{
//            System.out.println("in ignoreStatements");
            boolean check = true;
            boolean checkForWhile = false;
            scan.getNext();
            while(check){
//                System.out.println("printing: " + scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("while")){
                    checkForWhile = true;
                }
                else if( scan.currentToken.tokenStr.equals("endwhile") && !checkForWhile){
                    //System.out.println("middle else if");


                    return;
                }
                else if(scan.currentToken.tokenStr.equals("endwhile") && checkForWhile){
                    //System.out.println("last else if");
                    checkForWhile = false;
                }
                scan.getNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * ignores statements until it gets to and endif
     *
     * @return      N/A
     */
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
                else if((scan.currentToken.tokenStr.equals("else") || scan.currentToken.tokenStr.equals("endif")
                        || scan.currentToken.tokenStr.equals("endwhile"))&& !checkForIf){
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
    /**
     * executes statements until it gets to endif or endwhile
     *
     * @return      N/A
     */
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
                    scan.getNext();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
//                    System.out.println("in if ");
                    ifStmt(bExec);
                }
                else if(scan.currentToken.tokenStr.equals("for")){
                    forStmt();
                }
                else if(scan.currentToken.tokenStr.equals("Int") || scan.currentToken.tokenStr.equals("Bool")
                        || scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                    scan.getNext();
                    scan.getNext();
                    boolean check2 = false;
                    if(!scan.currentToken.tokenStr.equals(";")){
                        check2=true;
                    }
                    scan.iSourceLineNr -= 1;
                    scan.iColPos = 10000;
                    scan.getNext();
                    //System.out.println(scan.currentToken.tokenStr);
                    scan.getNext();
                    if(check2){
                        res = assignment();
                    }
                    else{
                        skipTo(";");
                    }
                }
                else if(scan.currentToken.tokenStr.equals("endif") || scan.currentToken.tokenStr.equals("else") || scan.currentToken.tokenStr.equals("endwhile")){
                    return;
                }
                else if(scan.currentToken.tokenStr.equals("while")){
                    whileStmt();
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
    /**
     * executeStatements decides whether to ignore statements or execute them through the param
     *
     *
     * @return      N/A
     */
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
//      System.out.println("in eval");
        boolean check = false;
        Expr exp = new Expr(scan, storage);
        ResultValue res01;
        ResultValue res02;

        try{
            res01 = exp.expr(":", debugExpr);
            check = (boolean) res01.value;
            return check;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }



    /**
     * skips to a certain token
     *
     * @param stopToken token to skip to
     * @return      N/A
     */
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