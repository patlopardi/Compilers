package pickle;

public class Expr {
  public Scanner scan;
  public String endSeparator;
  public StorageManager storage;
  
  public Expr(Scanner scanner, StorageManager storage){
    
    this.scan = scanner;
    this.storage = storage;
  }
    
  public ResultValue expr(String endSeparator) throws Exception {
    // begin on the first token of the expression
    this.endSeparator = endSeparator;
    
    if(scan.currentToken.primClassif != Classif.OPERAND)
    {
      scan.getNext();
    }
    
    Token operator;
    System.out.printf("This is the current token %s \n", scan.currentToken.tokenStr);
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

  private ResultValue operand() throws Exception {
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
          return res;	
        case INTEGER:
        case FLOAT:
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
