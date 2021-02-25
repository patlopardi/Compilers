package pickle;
import java.util.*;
import java.io.*;

import static pickle.PickleUtil.checkComments;

public class Scanner
{
  //Variables
  //private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";
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
          iSourceLineNr += 1;
          iColPos = 0;
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
    else if(input == '+' || input == '-' || input == '*' || input == '/' || input == '<' ||
          input == '>' || input == '!' || input == '=' || input == '#' || input == '^')
    {
      return "OPERATOR";
    }
    //Seperators //String.valueOf(input).matches("(|)|:|;|[|]|,"))
    else if(input == '(' || input == ')' || input == ':' || input == ';' || input == '[' || input == ']' ||input == ',')
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