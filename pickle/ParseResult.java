package pickle;

public class ParseResult {
  //parseType
  public ParseType parseType;
  //resultType from Symbol Table
  public SubClassif  resultType = SubClassif.EMPTY;
  //label helps describe what this is, usually from the parent's perspective
  public String label;
  //terminatingStr  
  public String terminatingStr;
  public Token token;
  //children
  public ParseResult targetVar = null;
  public ParseResult operator = null;
  public ParseResult express = null;

  // nonterminal constructor expression
  public ParseResult(String express){
    this.parseType = ParseType.NONTERMINAL;
    this.resultType = SubClassif.EMPTY;
    this.label = express;
  }

  //subtree constructor (targetVar)
  public ParseResult(String targetVar, Token tokenVariable, Classif dclType){
    this.targetVar = targetVar;
    this.token = tokenVariable;
  }

  //child constructor
  public ParseResult(String targetVar, Classif dclType){

  }

  public void appendChild(String label, ParseResult subtree){
    //Appends a node onto the end of the child list for the current ParseResult.  
    //It sets subtree's label to that specified value. If there already is a child 
    //for the current ParseResult, place subtree on the end of that child's sibling list.  
    //Otherwise, it is the first child of the current ParseResult.

  }
  public void appendChild(ParseResult subtree){
  // Appends a node onto the end of the child list for the current ParseResult. 

  }
}