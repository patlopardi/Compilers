package pickle;
import java.util.*;
import java.io.*;

import static pickle.PickleUtil.checkComments;

public class Scanner
{
  //Variables
  //private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";
  private final static String operators = "+-*/<>!=#^";
  private final static String separators = "():;[],";
  private final static ArrayList<String> uniqueOperatorArr = new ArrayList<String>(){
  {
    add("and");
    add("or");
    add("not");
    add("in");
    add("notin");
  }};
  private final static ArrayList<String> uniqueFlowArr = new ArrayList<String>() {
  {
    add("endif");
    add("elseif");
    add("else");
    add("endfor");
    add("endwhile");
    add("while");
    add("if");
    add("for");
  }
  };
  public Token currentToken = new Token();
  public int iColPos;
  public int iSourceLineNr;
  public char textCharM[];
  public List<String> sourceLineM = new ArrayList<>();
  public String fileNm = "";
  public SymbolTable symbol = new SymbolTable();
  public boolean flagString = false;
  public boolean flagNum = false;
  public boolean flagDecimal = false;
  public boolean flagComments = false;

  //Attributes
  //Constructor
  public Scanner(String sourceFileNm, SymbolTable symbolTable)
  {
    fileNm = sourceFileNm;
    symbol = symbolTable;
    //Read file
    try (BufferedReader br = new BufferedReader(new FileReader(sourceFileNm))) {
      while (br.ready()){
        sourceLineM.add(br.readLine());
      }
      br.close();
    } catch (IOException e) {
      return; 
    }
    //Starting line number
    iSourceLineNr = 0;
    iColPos = 0;
    //Split into characters
    textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
  }
  
  public String getNext()
  {
    //Variables
    //Flags
    boolean flagStartCharacter = true;
    Token nextToken = new Token();

    // End of line logic

    //While not end of file
    while(sourceLineM.size() > iSourceLineNr)
    {
      //Check blank line
      if((iColPos > textCharM.length - 1))
      {
        iSourceLineNr += 1;
        if(sourceLineM.size() <= iSourceLineNr)
        {
          return "";
        }
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        iColPos = 0;
        flagComments = false;
      }

      if(iColPos == 0 && sourceLineM.get(iSourceLineNr).trim().length() > 1) {
        System.out.printf("%d %s\n", iSourceLineNr + 1, sourceLineM.get(iSourceLineNr));
      }

      if(flagComments == false){
        textCharM = checkComments(textCharM);
        flagComments = true;
      }
      if(textCharM.length == 0){
        currentToken.tokenStr = "COMMENT";
        return currentToken.tokenStr;
      }

      if(sourceLineM.get(iSourceLineNr).trim().length() < 1)
      {
        //Increment Line, set text char array, reset col position
        iSourceLineNr += 1;
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        iColPos = 0;
        //Print new line if not whitespace
        if(sourceLineM.get(iSourceLineNr).trim().length() > 1)
        {
          System.out.printf("%d %s\n", iSourceLineNr + 1, sourceLineM.get(iSourceLineNr));
        }

        textCharM = checkComments(textCharM);
        if(textCharM.length == 0){
          currentToken.tokenStr = "COMMENT";
          return currentToken.tokenStr;
        }

      }
      //!!!!!!!!!!!!!!!!!!!!!!!!MARKED FIRST CHARACTER!!!!!!!!!!!!!!!!
      else if(flagStartCharacter)
      {
        flagStartCharacter = false;
        //Sort for first character
        try {
          switch(getType(textCharM[iColPos]))
          {
            case "OPERATOR":
              nextToken.primClassif = Classif.OPERATOR;
              nextToken.tokenStr += textCharM[iColPos];
              //Check if 2 character operator
              if(textCharM[iColPos + 1] == '=')
              {
                //Iterate and assign 2nd character
                iColPos++;
                nextToken.tokenStr+= textCharM[iColPos];
              }
              currentToken = nextToken;
              iColPos++;
              return currentToken.tokenStr;
            case "SEPARATOR":
              nextToken.primClassif = Classif.SEPARATOR;
              nextToken.tokenStr += textCharM[iColPos];
              currentToken = nextToken;
              iColPos++;
              return currentToken.tokenStr;
            case "SPACE":
              //Incorrect first character to reset and try again
              flagStartCharacter = true;
              iColPos++;
              break;
            case "STRING":
              flagString = true;
              nextToken.primClassif = Classif.OPERAND;
              nextToken.subClassif = SubClassif.STRING;
              iColPos++;
              break;
            case "NUMBER":
              flagNum = true;
              nextToken.primClassif = Classif.OPERAND;
              if(textCharM[iColPos] == '.'){
                flagDecimal = true;
                nextToken.subClassif = SubClassif.FLOAT;
              }
              else
              {
                nextToken.subClassif = SubClassif.INTEGER;
              }
              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              break;
            case "OPERAND":
              nextToken.primClassif = Classif.OPERAND;
              nextToken.tokenStr += textCharM[iColPos];
              //Change this to be specific
              nextToken.subClassif = SubClassif.IDENTIFIER;
              iColPos++;
              break;
            default:
              System.out.println("No Match");
        }
        } catch(Exception e)
        {
          System.out.printf("\nMissing closed quotation on line %d\n", iSourceLineNr + 1);
          iSourceLineNr += 1;
          iColPos = 0;
          currentToken = nextToken;
          return currentToken.tokenStr;
        }
      }
      //!!!!!!!!!!!!!!!!!!!!!!!!!!NON FIRST CHARACTER!!!!!!!!!!!!!!!!!!!!!!!!
      else
      {
        try
        {
          //Sort for Nth character
          switch(getType(textCharM[iColPos]))
          {
            case "OPERATOR":
              currentToken = nextToken;
              return currentToken.tokenStr;
            case "SEPARATOR":
              currentToken = nextToken;
              return currentToken.tokenStr;
            case "SPACE":
              currentToken = nextToken;
              return currentToken.tokenStr;
            case "STRING":
              //Non-quote accounting
              if(textCharM[iColPos] == '\"')
              {
                flagString = false;
                iColPos++;
                currentToken = nextToken;
                return currentToken.tokenStr; 
              }
              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              break;
            case "NUMBER":
              if(textCharM[iColPos] == '.'){
                if(flagDecimal == true)
                {
                  flagDecimal = false;
                  System.out.printf("\nIncorrect float value on line %d\n", iSourceLineNr + 1);
                  System.out.println("");
                  iSourceLineNr += 1;
                  iColPos = 0;
                  currentToken = nextToken;
                  return currentToken.tokenStr;
                }
                flagDecimal = true;
                nextToken.subClassif = SubClassif.FLOAT;
              }
              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              break;
            case "OPERAND":
              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              //May be able to be shortened down \/
              //Check if and or not in notin
              if(uniqueOperatorArr.contains(nextToken.tokenStr))
              {
                //Ensure it's end of the statement
                if(iColPos > textCharM.length - 1 || !getType(textCharM[iColPos]).equals("OPERAND") && !getType(textCharM[iColPos]).equals("NUMBER"))
                {
                  //Set as Operator
                  nextToken.primClassif = Classif.OPERATOR;
                  nextToken.subClassif = SubClassif.EMPTY;
                  currentToken = nextToken;
                  return currentToken.tokenStr;
                }
              }
              //Check control flow tokens
              if(uniqueFlowArr.contains(nextToken.tokenStr))
              {
                //Ensure nothing is added, ex. "fort" is not "for"
                if(iColPos > textCharM.length - 1 || !getType(textCharM[iColPos]).equals("OPERAND") && !getType(textCharM[iColPos]).equals("NUMBER"))
                {
                  //Set as Control
                  nextToken.primClassif = Classif.CONTROL;
                  //Specify subclass
                  if(uniqueFlowArr.subList(0,5).contains(nextToken.tokenStr))
                  {
                    nextToken.subClassif = SubClassif.END;
                  }
                  else
                  {
                    nextToken.subClassif = SubClassif.FLOW;
                  }
                  currentToken = nextToken;
                  return currentToken.tokenStr;
                }
              }
              
              //Line ends
              if(iColPos > textCharM.length - 1)
              {
                currentToken = nextToken;
                return currentToken.tokenStr;
              }
              break;
            case "COMMENT":
              if(textCharM[iColPos+1] == '/'){
                iColPos = textCharM.length;
              }
            default:
              System.out.println("No Match");
          }
        } catch(Exception e)
        {
          System.out.printf("\nMissing closed quotation on line %d\n", iSourceLineNr + 1);
          System.out.println("");
          //No need to itterate when already at end of line
          //iSourceLineNr += 1;
          //iColPos = 0;
          currentToken = nextToken;
          return currentToken.tokenStr;
        }
      }
    }
    //Return for end of file
    return "";
  }
  

  
  //Returns Identifier
  private String getType(char input)
  {
    //Special cases first
    //String 
    if(flagString || input == '\"')
    {
      return "STRING";
    }
    //Number
    else if(Character.isDigit(input) || input == '.')
    {
      return "NUMBER";
    }
    if(flagDecimal)
    {
      flagDecimal = false;
    }
    //Non-Special
    //Operators
    else if(operators.contains(String.valueOf(input)))
    {
      return "OPERATOR";
    }
    else if(separators.contains(String.valueOf(input)))
    {
      return "SEPARATOR";
    }
    //Spaces
    else if(input == ' ')
    {
      return "SPACE";  
    }
    //comments
    else if(input =='/'){
      return "COMMENT";
    }
    //Operand
    else
    {
      return "OPERAND";
    }
    return "";
  }
}