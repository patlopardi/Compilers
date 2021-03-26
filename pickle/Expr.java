package pickle;

public class Expr {
  public Scanner scan;
  
  public Expr(Scanner scanner){
    this.scan = scanner;
  }
    
  public ResultValue expr(String endSeparator, Scanner scanner) throws Exception {
    // begin on the first token of the expression
    scan.getNext();
    Token operator;
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
    return res;
}

  private ResultValue products() throws Exception {

    Token operator; 
    ResultValue res = operand();                    
    ResultValue temp;

    while (scan.currentToken.tokenStr.equals("*") || scan.currentToken.tokenStr.equals("/")) {
      operator = scan.currentToken;
      scan.getNext();
      if (scan.currentToken.primClassif != Classif.OPERAND)
        System.out.printf("Within expression, expected operand.  Found: '%s'"
                      , scan.currentToken.tokenStr);

      temp = operand();  
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

  private ResultValue operand() throws Exception {
    ResultValue res = null;
    if (scan.currentToken.primClassif == Classif.OPERAND)
    {
      switch (scan.currentToken.subClassif)
      {
        case IDENTIFIER:                  
          //Need reference to the manager
          //res = StorageManager.getVariableValue(scan.currentToken.tokenStr);
          // nextToken is operator or sep
          scan.getNext();                     
          return res;	
        case INTEGER:
        case FLOAT:
        case DATE:
        case STRING:
        case BOOLEAN:
          res = scan.currentToken.toResult();  
          // nextToken is operator or sep
          scan.getNext();                     
          return res;
      }

    }
    System.out.printf("Within operand, found: '%s'", scan.currentToken.tokenStr);
    return null; 
  }
}
