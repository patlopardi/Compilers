package pickle;

  /**
  * Constructor for the Expr class which set variables
  * <p>
  * The variables it populates is the scanner object for reading in tokens as 
  *     as the storage object which holds all the initialized variable values
  *
  *  scanner      Scanner object for iterating through and getting the tokens
  *  storage      StorageManager object for access to the initialized variables
  *
  * @return      N/A
  */
public class Expr {
  public Scanner scan;
  public String endSeparator;
  public StorageManager storage;
  public boolean debugExpr;
  
  public Expr(Scanner scanner, StorageManager storage){
    
    this.scan = scanner;
    this.storage = storage;
  }



    /**
  * Expr function which starts the movement down the grammar
  * <p>
  *   Starting handle for any token that is comparing two values, then returning a result value
  *    holding the boolean of the statement.
  *
  * @param endSeparator      The end Separator for populating the ResultValue with
  * @param debugExpr         Boolean value to toggle debug mode
  *
  * @return       ResultValue which holds the boolean values of the calculated expression
  * @throws       Exception
  */
  public ResultValue expr(String endSeparator, boolean debugExpr) throws Exception {
    
    // begin on the first token of the expression
    this.endSeparator = endSeparator;
    this.debugExpr = debugExpr;
    
    if(scan.currentToken.primClassif != Classif.OPERAND)
    {
      if(debugExpr && (scan.currentToken.tokenStr.equals("if") || scan.currentToken.tokenStr.equals("while"))){
        System.out.println("> " + scan.currentToken.tokenStr + "Stmt: " + scan.printCurrLine().trim());
      }
      if(!scan.currentToken.tokenStr.equals("-"))
      {
        scan.getNext(); 
      }
    }
    
    Token operator;
    String opString = "";
    String res02 = "";
    ResultValue res = summation();
    String resClone = res.value.toString();
    ResultValue temp;
    boolean result;
      //Loop for the actual check of the comparison
      while (scan.currentToken.tokenStr.equals("<") || scan.currentToken.tokenStr.equals(">") || scan.currentToken.tokenStr.equals("<=") || scan.currentToken.tokenStr.equals(">=") ||
      scan.currentToken.tokenStr.equals("==") || scan.currentToken.tokenStr.equals("!=")){
        operator = scan.currentToken;
        opString = operator.tokenStr;
        scan.getNext();
        if (scan.currentToken.primClassif != Classif.OPERAND && (!scan.currentToken.tokenStr.equals("(") && !scan.currentToken.tokenStr.equals(")")))
          System.out.printf("Within expression, expected operand.  Found %s", scan.currentToken.tokenStr);

        temp = summation();
        res02 = temp.value.toString();
        if(operator.tokenStr.equals("<"))   
        {
          result = PickleUtil.LessThan(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }              
        else if(operator.tokenStr.equals(">"))
        {
          result = PickleUtil.GreaterThan(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }
        else if(operator.tokenStr.equals("<="))
        {
          result = PickleUtil.LessThanOrEqual(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }
        else if(operator.tokenStr.equals(">="))
        {
          result = PickleUtil.GreaterThanOrEqual(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }
        else if(operator.tokenStr.equals("=="))
        {
          result = PickleUtil.Equivalent(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }
        else if(operator.tokenStr.equals("!="))
        {
          result = PickleUtil.NotEquivalent(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
          res = new ResultValue(SubClassif.BOOLEAN, result, null, endSeparator);
        }
      }
    if(debugExpr && opString.length() > 1){

      System.out.println("..." + resClone + " " + opString + " " + res02 + " is " + res.value.toString() );
    }
    // System.out.printf("Final result is: %s \n", res.value);
    return res;
  }


    /**
  * Summation function which is the 2nd down from the branching top level. It handles addition
  *    as well as subtraction expressions
  * <p>
  *   Handle for any two tokens that add or subtract from one another. Then returns as
  *    a resultvalue of the expression's sum
  *
  * @return       ResultValue which holds the value of the calculated expression
  * @throws       Exception
  */
  private ResultValue summation() throws Exception {
    
    Token operator;
    //System.out.printf("This is the current token %s \n", scan.currentToken.tokenStr);
    ResultValue res = products();                    
    ResultValue temp;
    
    //Loop for the actual check of the summation
    while (scan.currentToken.tokenStr.equals("+") || scan.currentToken.tokenStr.equals("-") || scan.currentToken.tokenStr.equals("#")){
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND && (!scan.currentToken.tokenStr.equals("(") && !scan.currentToken.tokenStr.equals(")")))
        System.out.printf("Within expression, expected operand.  Found %s", scan.currentToken.tokenStr);

      temp = products(); 
      if(operator.tokenStr.equals("+"))   
      {
        res = PickleUtil.Addition(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }              
      else if(operator.tokenStr.equals("-"))
      {
        res = PickleUtil.Subtract(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
      else if(operator.tokenStr.equals("#"))
      {
        res = PickleUtil.Concatenation(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
    }
    return res;
}

  /**
  * Products which is the 3rd down from the top level and handles the * and / expressions
  * <p>
  *   Handle for any two tokens that multiply or divide. Then return as the result.
  *
  * @return       ResultValue which holds the value of the calculation of the expression
  * @throws       Exception
  */
  private ResultValue products() throws Exception {

    Token operator; 
    ResultValue res = expon(null);                  
    ResultValue temp;

    while (scan.currentToken.tokenStr.equals("*") || scan.currentToken.tokenStr.equals("/")) {
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND && (!scan.currentToken.tokenStr.equals("(") && !scan.currentToken.tokenStr.equals(")")))
        System.out.printf("Within expression, expected operand.  Found: '%s'"
                      , scan.currentToken.tokenStr);

      temp = expon(null);
      if(operator.tokenStr.equals("*"))
      {
        res = PickleUtil.Multiply(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
      else if(operator.tokenStr.equals("/"))                         
      {
        res = PickleUtil.Divide(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
    }
    return res;
  }

    /**
  * Handles exponents of the expression which is the 4th down from the top level
  * <p>
  *   Handles exponent of left and right expression, returns the ResultValue fo the expression
  *
  * @param recursed         ResultValue of the previous high level recursive temp used in chain exponents
  *
  * @return       ResultValue which holds the value of the calculation of the expression
  * @throws       Exception
  */
  private ResultValue expon(ResultValue recursed) throws Exception {

    Token operator;
    ResultValue res;
    ResultValue temp;
    //Non recursive
    if(recursed == null)
    {
      res = function();  
    }
    //Recursive
    else
    {
      res = recursed;
    }

    while (scan.currentToken.tokenStr.equals("^")) {
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND && (!scan.currentToken.tokenStr.equals("(") && !scan.currentToken.tokenStr.equals(")")))
        System.out.printf("Within expression, expected operand.  Found: '%s'"
                      , scan.currentToken.tokenStr);

      temp = function(); 
      if(scan.currentToken.tokenStr.equals("^"))
      {
        temp = expon(temp);
      }
      if(operator.tokenStr.equals("^"))
      {
        res = PickleUtil.Square(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
    }
    return res;
  }

      /**
  * Handles function calls of the expression which is the 5th down from the top level
  * <p>
  *   Handles the call to function, returns the ResultValue for the expression
  *
  * @return       ResultValue   Holds the returned value of the function
  * @throws       Exception
  */
  private ResultValue function() throws Exception {

    Token operator;
    ResultValue res = null;
    operator = scan.currentToken;

    if(scan.currentToken.tokenStr.equals("LENGTH") || scan.currentToken.tokenStr.equals("MAXELEM") || scan.currentToken.tokenStr.equals("ELEM") 
    || scan.currentToken.tokenStr.equals("SPACES")) { 
      //Iterate to the left parentheses and call to operand for recursion with parentheses.
      scan.getNext();
      res = operand();
      if(operator.tokenStr.equals("LENGTH"))
      {
        //res = PickleUtil.Length(res);
      }
      else if(operator.tokenStr.equals("MAXELEM"))
      {
        //res = PickleUtil.MAXELEM(res);
      }
      else if(operator.tokenStr.equals("ELEM"))
      {
        //res = PickleUtil.ELEM(res);
      }
      else if(operator.tokenStr.equals("SPACES"))
      {
        //res = PickleUtil.SPACES(res);
      }
    }
    else
    {
      //Default call to operand
      res = operand();
    }
    return res;
  }

    /**
  * Handles the bottom layer of the grammar specifying indepth value of token
  * <p>
  *   Returns the value of the variable, through either searching the storage or through the
  *     literal current value. They're returned as a ResultValue
  *
  * @return       ResultValue which holds the value of the token (either from stored variable or just value)
  * @throws       Exception
  */
  private ResultValue operand() throws Exception {
    boolean negative = false;
    if(scan.currentToken.tokenStr.equals("-"))
    {
      negative = true;
      scan.getNext();
    }
    
    ResultValue res = new ResultValue(null, null, null, null);
    if (scan.currentToken.primClassif == Classif.OPERAND)
    {
      switch (scan.currentToken.subClassif)
      {
        case IDENTIFIER:                
          //Need reference to the manager
          res = storage.getVariableValue(scan.currentToken.tokenStr);
          // nextToken is operator or sep
          scan.getNext();                     
          if(negative)
          {
            res = PickleUtil.UnitaryMinus(new Numeric(this.scan, res, null, null));
          }
          return res;	
        case INTEGER:
        case FLOAT: 
          res = scan.currentToken.toResult(endSeparator);
          if(negative)
          {
            res = PickleUtil.UnitaryMinus(new Numeric(this.scan, res, null, null));
          }
          // nextToken is operator or sep
          scan.getNext();                     
          return res;
        case DATE:
        case STRING:
        case BOOLEAN:
          res = scan.currentToken.toResult(endSeparator);
          // nextToken is operator or sep
          scan.getNext();                     
          return res;
      }
    }
    //Handling Parenth
    if(scan.currentToken.tokenStr.equals("("))
    {
      //System.out.printf("Entered Left Parenth\n");
      res = expr(endSeparator, debugExpr);
      //Ensure there is a right parentheses
      if(!scan.currentToken.tokenStr.equals(")"))
      {
        System.out.printf("Expected right parentheses, found: '%s'\n", scan.currentToken.tokenStr);
      }
      scan.getNext();  
      return res;
    }
    
    System.out.printf("Within operand, found: '%s'\n", scan.currentToken.tokenStr);
    return null; 
  }
}
