package pickle;

  /**
  * Constructor for the Expr class which set variables
  * <p>
  * The variables it populates is the scanner object for reading in tokens as 
  *     as the storage object which holds all the initialized variable values
  *
  * @param scanner      Scanner object for iterating through and getting the tokens
  * @param storage      StorageManager object for access to the initialized variables
  *
  * @return      N/A
  */
public class Expr {
  public Scanner scan;
  public String endSeparator;
  public StorageManager storage;
  
  public Expr(Scanner scanner, StorageManager storage){
    
    this.scan = scanner;
    this.storage = storage;
  }

    /**
  * Expr function which starts the movement down the grammar
  * <p>
  *   Starting handle for any token that is + or -, takes the left and right operand
  *      and runs either a addition or subtract using the util functionality
  *
  * @param endSeparator      The end Separator for populating the ResultValue with
  *
  * @return       ResultValue which holds the value of the calculation of the expression
  * @throws       Exception
  */
  public ResultValue expr(String endSeparator) throws Exception {
    // begin on the first token of the expression
    this.endSeparator = endSeparator;
    
    if(scan.currentToken.primClassif != Classif.OPERAND)
    {
      scan.getNext();
    }
    
    Token operator;
    //System.out.printf("This is the current token %s \n", scan.currentToken.tokenStr);
    ResultValue res = products();                    
    ResultValue temp;
    
    while (scan.currentToken.tokenStr.equals("+") || scan.currentToken.tokenStr.equals("-")){
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND)
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
    }
    System.out.printf("\n My final return is %s \n", res.value);
    return res;
}

  /**
  * Products which is the 2nd down from the top level and handles the * and / expressions
  * <p>
  *   Handle for any two tokens that multiply or divide. Then return as the result.
  *
  * @return       ResultValue which holds the value of the calculation of the expression
  * @throws       Exception
  */
  private ResultValue products() throws Exception {

    Token operator; 
    //ResultValue res = operand();  
    ResultValue res = expon();                  
    ResultValue temp;

    while (scan.currentToken.tokenStr.equals("*") || scan.currentToken.tokenStr.equals("/")) {
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND)
        System.out.printf("Within expression, expected operand.  Found: '%s'"
                      , scan.currentToken.tokenStr);

      //temp = operand();
      temp = expon();
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
  * Handles exponents of the expression which is the 3rd down from the top level
  * <p>
  *   Handles exponent of left and right expression, returns the ResultValue fo the expression
  *
  * @return       ResultValue which holds the value of the calculation of the expression
  * @throws       Exception
  */
  private ResultValue expon() throws Exception {

    Token operator; 
    ResultValue res = operand();                    
    ResultValue temp;

    while (scan.currentToken.tokenStr.equals("^")) {
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND)
        System.out.printf("Within expression, expected operand.  Found: '%s'"
                      , scan.currentToken.tokenStr);

      temp = operand();  
      if(operator.tokenStr.equals("^"))
      {
        res = PickleUtil.Square(new Numeric(this.scan, res, null, null), new Numeric(this.scan, temp, null, null));
      }
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
    
    ResultValue res = null;
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
          if(negative)
          {
            res = PickleUtil.UnitaryMinus(new Numeric(this.scan, res, null, null));
          }
        case DATE:
        case STRING:
        case BOOLEAN:
          res = scan.currentToken.toResult(endSeparator);  
          // nextToken is operator or sep
          scan.getNext();                     
          return res;
      }

    }

    
    System.out.printf("Within operand, found: '%s'", scan.currentToken.tokenStr);
    return null; 
  }
}
