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
                else if(scan.currentToken.tokenStr.equals("/")){
                    scan.iColPos=100000;
                    scan.getNext();
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
            ResultValue res;
            scan.getNext();
            while(!scan.getNext().equals(";")){
                res = exp.expr(",)", debugExpr);
                if(res.dataType == SubClassif.ARRAY){
                    int i;
                    for(i=0;i<PickleUtil.MAXELEM(res.value.toString(), storage);i++){
                        if(storage.getArrayValue(res.value.toString()).get(i) != null){
                            //System.out.println("in here");
//                            System.out.println(i);
                            System.out.printf("%s ", storage.getArrayValue(res.value.toString()).get(i).value);
                        }
                    }

                }
                else{
//                    System.out.println("in here");
                    System.out.printf("%s", res.value);
                }


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
        Expr exp = new Expr(scan, storage);
        ResultValue res = null;

        if(!bExec)
            skipTo(";");
        else {
            if (scan.currentToken.subClassif != SubClassif.IDENTIFIER)
                error("expected a variable for the target of an assignment ", scan.currentToken);

            String variableStr = scan.currentToken.tokenStr;
            Token saveToken = scan.currentToken;
            scan.getNext();
            if (scan.currentToken.primClassif != Classif.OPERATOR && !scan.currentToken.tokenStr.equals("["))
                error("expected assignment operator", scan.currentToken.tokenStr);
            String operatorStr = scan.currentToken.tokenStr;
            ResultValue res02;
            ResultValue res01;
            String index=null;
            Numeric n0p2;  // numeric value of second operand
            Numeric n0p1;  // numeric value of first operand

            switch (operatorStr) {
                case "=":
                    if(forCheck) {
                        res02 = exp.expr("to", debugExpr);
                    }
                    else{
                        res02 = exp.expr(";", debugExpr);
                    }
                    if(storage.getArrayValue(variableStr) == null){
                        res = storage.Assign(variableStr, res02);
                    }
                    else{
                        scan.iSourceLineNr-=1;
                        scan.iColPos=10000;
                        scan.getNext();
                        skipTo("=");
                        int count2=0;
                        int i;
                        scan.getNext();
                        ResultValue res05;
                        res = exp.expr(";", debugExpr);
                        int max = PickleUtil.MAXELEM(variableStr, storage);
                        if(res.dataType == SubClassif.ARRAY){
                            max = PickleUtil.ELEM(res.value.toString(), storage);
                            for(i=0;i<max;i++){
                                if(i>=PickleUtil.MAXELEM(variableStr, storage)){
                                    break;
                                }
                                res05 = storage.getArrayValue(res.value.toString()).get(i);
                                index=String.valueOf(i);
                                storage.AssignArr(variableStr, index, res05);
                            }
                        }
                        else{
                            for(i=0;i<max;i++){
                                index = String.valueOf(i);
                                storage.AssignArr(variableStr, index, res);
                            }
                        }
                    }
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
                    if(storage.getVariableValue(saveToken.tokenStr) != null){
                        scan.getNext();
                        res = exp.expr("]", debugExpr);
                        index = res.toString();
                        scan.getNext();
                        res02 = exp.expr(";", debugExpr);
                        int temp=Integer.parseInt(res.value.toString());
                        int g = temp;
                        int length = res02.value.toString().length();
                        String temp5 = res02.value.toString();
                        ArrayList<Character> chars = new ArrayList<Character>();
                        for (char c : storage.getVariableValue(saveToken.tokenStr).value.toString().toCharArray()) {
                            chars.add(c);
                        }
                        chars.remove(g);
                        for(char k: temp5.toCharArray()){
                            chars.add(g, k);
                            g++;
                        }
                        StringBuilder builder = new StringBuilder(chars.size());
                        for(Character ch: chars)
                        {
                            builder.append(ch);
                        }

                        String temp2 = builder.toString();
                        ResultValue temp3 = new ResultValue(SubClassif.STRING, temp2, "", "");
                        storage.Assign(saveToken.tokenStr, temp3);
                        break;
                    }
//                    System.out.println("in here");
                    if(storage.getArrayValue(variableStr) == null){
//                        System.out.println("this is not declared");
                        scan.getNext();
                        if(!scan.currentToken.tokenStr.equals("]")){

                            index = exp.expr("]", debugExpr).value.toString();
                        }else{
                            index = scan.currentToken.tokenStr;
                        }

                        storage.DeclareArr(scan, variableStr, index, SubClassif.INTEGER);
                        scan.iSourceLineNr-=1;
                        scan.iColPos=1000;
                        break;
                    }
                    else if(storage.getArrayValue(variableStr) != null){
                        boolean check = false;
                        boolean checkForDeclaration=false;
                        boolean check2 = true;
                        int count = 0;
                        index=null;
                        scan.iSourceLineNr-=1;
                        scan.iColPos=1000;
                        scan.getNext();
                        if(scan.currentToken.tokenStr.equals("Int") ||scan.currentToken.tokenStr.equals("String") || scan.currentToken.tokenStr.equals("Float")){
                            while(!scan.getNext().equals(";")){
                                if(scan.currentToken.tokenStr.equals("=")){
                                    check2 = false;
                                }
                            }
                            if(check2){
                                break;
                            }
                            checkForDeclaration=true;
                        }
                        while(!scan.currentToken.tokenStr.equals("=") && !checkForDeclaration){
                            if(scan.currentToken.tokenStr.equals("[")){
                                scan.getNext();
                                if(scan.currentToken.tokenStr.equals("]")){
                                    check=false;
                                    break;
                                }
                                check=true;
                                res = exp.expr("]", debugExpr);
                                index = res.value.toString();
                            }
                            scan.getNext();
                        }
                        scan.iSourceLineNr-=1;
                        scan.iColPos=1000;
                        scan.getNext();
                        skipTo("=");
                        if(check){
                            res = exp.expr(";", debugExpr);
                            storage.AssignArr(variableStr, index, res);
                        }
                        else {
                            count=0;
                            while (!scan.currentToken.tokenStr.equals(";")) {
                                index = String.valueOf(count);
                                res = exp.expr(";", debugExpr);
                                storage.AssignArr(variableStr, index, res);
                                if (scan.currentToken.tokenStr.equals(";")) {
                                    break;
                                }
                                scan.getNext();
                                count++;
                            }
                        }
                    }


                    break;
                default:
                    error("expected assignment operator received instead: ", operatorStr);
                    break;
            }
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
    /**
     * forStmt() handles all the for loops and executes them in a loop
     *
     *
     *
     *
     * @return      N/A
     */
    public void forStmt(){
        //TODO: add for each element in an array
        try{
            ResultValue result = new ResultValue(SubClassif.INTEGER, 1, "", "");
            int saveLineNr = scan.iSourceLineNr;
            String saveToken;
            String saveToken2;
            tokens.clear();
            Expr exp = new Expr(scan, storage);

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
                scan.getNext();
                scan.getNext();
                scan.getNext();
//                System.out.println(scan.currentToken.tokenStr);
                if(scan.currentToken.tokenStr.equals("ELEM")){
//                    System.out.println("in here");
                    skipTo(")");
                }
//                System.out.println(scan.currentToken.tokenStr);
                if(scan.getNext().equals("by")){
                    forType1=true;
                }
                else{
                    forType2=true;
                }
            }
            else if(scan.currentToken.tokenStr.equals("in")){
                forType3=true;
            }
            else{
                error("incorrect for form: ", scan.currentToken.tokenStr);
            }
            scan.iSourceLineNr=saveLineNr - 1;
            scan.iColPos=10000;
            scan.getNext();
            if(forType1){
                scan.getNext();
                forCheck=true;
                saveToken = scan.currentToken.tokenStr;
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
                scan.getNext();
                res02 = exp.expr(":", debugExpr);
                n0p2 = new Numeric(scan, res02, " +=", " nd Operand");
                res01 = storage.getVariableValue(saveToken);
                n0p1 = new Numeric(scan, res01, " +=", " st operand");
                int saveLineNr2 = scan.iSourceLineNr;
                while(checkCond){
                    scan.iSourceLineNr = saveLineNr2;
                    scan.iColPos=1000;
                    scan.getNext();
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
                scan.iSourceLineNr-=1;
                scan.iColPos=1000;
                scan.getNext();
                scan.getNext();
                assignment();
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
                scan.getNext();
                scan.getNext();
                if(storage.getArrayValue(scan.currentToken.tokenStr)==null){
                    scan.iSourceLineNr-=1;
                    scan.iColPos=10000;
                    scan.getNext();
                    scan.getNext();
                    saveToken = scan.currentToken.tokenStr;
                    scan.getNext();
                    scan.getNext();

                    res01 = exp.expr(":", debugExpr);
                    char [] temp = new char[1000];
                    temp = res01.value.toString().toCharArray();
                    ResultValue rv = new ResultValue(SubClassif.STRING, "", "", "");
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
                }
                else{
                    scan.iSourceLineNr-=1;
                    scan.iColPos=10000;
                    int i=0;
                    scan.getNext();
                    scan.getNext();
                    saveToken=scan.currentToken.tokenStr;
                    scan.getNext();
                    scan.getNext();
                    saveToken2=scan.currentToken.tokenStr;
                    int max = PickleUtil.ELEM(scan.currentToken.tokenStr, storage);
                    int saveLineNr2 = scan.iSourceLineNr+1;

                    for(i=0;i<max;i++){
                        scan.iSourceLineNr = saveLineNr2-1;
                        scan.iColPos=1000;
                        scan.getNext();
                        ResultValue rv = new ResultValue(SubClassif.INTEGER, "", "", "");
                        rv.value = storage.getArrayValue(saveToken2).get(i).value;
                        res = storage.Assign(saveToken, rv);
                        executeForStmt();
                    }
                }

                if(!scan.currentToken.tokenStr.equals("endfor"))
                    error("expected endfor for 'for' received: ", scan.currentToken.tokenStr);
                if(!scan.getNext().equals(";"))
                    error("expected ; after endfor received: ", scan.currentToken.tokenStr);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * executeForStmt() responsible for executing the statements within a for loop
     *
     *
     *
     * @return N/A
     *
     */
    public void executeForStmt(){
        try{
            ResultValue res;
            while(true){
                if(scan.currentToken.tokenStr.equals("print")){
                    print();
                    scan.getNext();
                    continue;
                }
                if(scan.currentToken.tokenStr.equals("if")){
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
            boolean check = true;
            boolean checkForWhile = false;
            scan.getNext();
            while(check){
                if(scan.currentToken.tokenStr.equals("while")){
                    checkForWhile = true;
                }
                else if( scan.currentToken.tokenStr.equals("endwhile") && !checkForWhile){
                    return;
                }
                else if(scan.currentToken.tokenStr.equals("endwhile") && checkForWhile){
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
            boolean check = true;
            boolean checkForIf = false;
            scan.getNext();
            while(check){
                if(scan.currentToken.tokenStr.equals("if"))
                    checkForIf = true;
                else if((scan.currentToken.tokenStr.equals("else") || scan.currentToken.tokenStr.equals("endif")
                        || scan.currentToken.tokenStr.equals("endwhile"))&& !checkForIf){
                    return;
                }
                else if(scan.currentToken.tokenStr.equals("endif") && checkForIf){
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
                if(scan.currentToken.tokenStr.equals("print")){
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
                scan.getNext();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}