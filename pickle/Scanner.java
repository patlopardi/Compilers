package pickle;
import pickle.Exceptions.InvalidFloatException;
import pickle.Exceptions.InvalidLiteralException;
import pickle.Exceptions.InvalidStringException;

import java.util.*;
import java.io.*;

public class Scanner
{
  //Variables
  //private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n";
  private final static String operators = "+-*/<>!=#^";
  private final static String separators = "():;[],";
  public Token currentToken = new Token();
  public int iColPos;
  public int iSourceLineNr;
  public char textCharM[];
  public List<String> sourceLineM = new ArrayList<>();
  public String fileNm = "";
  public SymbolTable symbol = new SymbolTable();
  private char quoteType = 'n';
  public boolean flagString = false;
  public boolean flagNum = false;
  public boolean flagDecimal = false;
  public boolean debugStatement = false;
  public boolean flagComments = false;

  //Attributes
  /**
  * Constructor for the Scanner class which set variables and reads through
  *    file, appending to list.
  * <p>
  * The variables it populates are String fileNm and SymbolTable symbol.
  *    The file fileNm has it's lines appended to an array list sourceLineM.
  *    It also assigns the first line of sourceLineM to char array textCharM.
  *
  * @param sourceFileNm    String which holds the name of file to be read
  * @param symbolTable     SymbolTable object passed into class to set variable
  *
  * @return      N/A
  * @throws      Exception if file can't be read and lines appended to array list
  */
  public Scanner(String sourceFileNm, SymbolTable symbolTable)
  {
    fileNm = sourceFileNm;
    symbol = symbolTable;
    //Read through file using BufferReader to append to array
    try (BufferedReader br = new BufferedReader(new FileReader(sourceFileNm))) {
      while (br.ready()){
        sourceLineM.add(br.readLine());
      }
      br.close();
    } catch (IOException e) {
      return; 
    }
    //Set line number and column position for the arrays
    iSourceLineNr = 0;
    iColPos = 0;
    //Set char array to first line of the string array of lines 
    textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
  }
  /**
  * Sorts through array of lines to populate Tokens with correct identifiers, then
  *    returns either the next Token's String or an empty String if end of array
  * <p>
  * The return value is currentToken.tokenStr. Loops through String Array sourceLineM
  *    then adds line to char array textCharM. Loops through the character array grabbing
  *    characters and appending them to the Token nextToken. When it hits an end to the
  *    token and it has been classified/subclassified it will set currentToken to nextToken
  *    then return nextToken. The sorting of what to do with the characters is done with
  *    two case statements (one for first character and other for non-first) that handle
  *    the character depending on identifier.
  *
  * @return      String currentToken.tokenStr which is the String of the current token
  *                  created or returns a "" when end of search.
  */
  public String getNext() throws InvalidFloatException, InvalidLiteralException, InvalidStringException {
    //Variables
    boolean flagStartCharacter = true;
    Token nextToken = new Token();
//    StorageManager a = new StorageManager();
//    ResultValue res01 = new ResultValue(SubClassif.INTEGER, 4, "", "d");
//    ResultValue res02 = new ResultValue(SubClassif.INTEGER, 2.0, "", "");
//    Numeric nOp1 = new Numeric(this, res01, "-", "1st Operand");
//    Numeric nOp2 = new Numeric(this, res02, "-", "2nd Operand");
//    a.Declare("i", res02);
//    ResultValue test = a.Assign("i", PickleUtil.Square(nOp1,nOp2));
//    System.out.println(PickleUtil.GreaterThan(nOp1,nOp2));


    //Loop through array of lines until at the end
    while(sourceLineM.size() > iSourceLineNr)
    {

      if(flagComments == false){
        textCharM = PickleUtil.checkComments(textCharM); // Deletes all comments from the text array
        flagComments = true;
      }

      // End of line logic
      if((iColPos > textCharM.length - 1)) {
        iSourceLineNr += 1;
        if(sourceLineM.size() <= iSourceLineNr)
        {
          return "";
        }
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        iColPos = 0;
        flagComments = false;
      }

      // Print for when on a new line
      if(iColPos == 0 && sourceLineM.get(iSourceLineNr).trim().length() > 1 && this.debugStatement)
      {
        System.out.printf("%d %s\n", iSourceLineNr + 1, sourceLineM.get(iSourceLineNr));
      }

      //Check blank line
      if(sourceLineM.get(iSourceLineNr).trim().length() < 1)
      { 
        //Increment Line, set text char array, reset col position
        iSourceLineNr += 1;
        if(sourceLineM.size() <= iSourceLineNr)
        {
          return "";
        }     
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        iColPos = 0;
      }
      //!!!!!!!!!!!!!!!!!!!!!!!!MARKED FIRST CHARACTER!!!!!!!!!!!!!!!!
      else if(flagStartCharacter)
      {
        flagStartCharacter = false;
        //Sort for first character
        switch(getType(textCharM[iColPos]))
        {
            case "OPERATOR":
              nextToken.primClassif = Classif.OPERATOR;
              nextToken.tokenStr += textCharM[iColPos];
              //Check if 2 character operator
              if(iColPos < textCharM.length - 1 && textCharM[iColPos + 1] == '=')
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
              quoteType = textCharM[iColPos];
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
              
              //Boolean constant check
              if((textCharM[iColPos] == 'T' || textCharM[iColPos] == 'F') && (iColPos > textCharM.length - 1 || (!getType(textCharM[iColPos + 1]).equals("OPERAND") && !getType(textCharM[iColPos + 1]).equals("NUMBER")))){
                nextToken.subClassif = SubClassif.BOOLEAN;
              }
              else{
                nextToken.subClassif = SubClassif.IDENTIFIER;
              }
              
              iColPos++;
              break;
            default:
        }
      }
      //!!!!!!!!!!!!!!!!!!!!!!!!!!NON FIRST CHARACTER!!!!!!!!!!!!!!!!!!!!!!!!
      else
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
              //Non-quoted accounting
              if(textCharM[iColPos] == quoteType)
              {
                flagString = false;
                quoteType = 'n';
                iColPos++;
                currentToken = nextToken;
                return currentToken.tokenStr; 
              }
              //If end of line and string isn't concluded then error
              if((iColPos + 1 > textCharM.length - 1))
              {
                flagString = false;
                currentToken = nextToken;
                currentToken.tokenStr += textCharM[iColPos];
                //Error Alert
                throw new InvalidStringException( currentToken.tokenStr, iColPos, iSourceLineNr );
              }
              //Handling escaped values
              if(textCharM[iColPos] == '\\')
              {
                iColPos++;
                if("\"\'\\".contains(String.valueOf(textCharM[iColPos])))
                {
                  nextToken.tokenStr += textCharM[iColPos];
                }
                else if(textCharM[iColPos] == 'n')
                {
                  nextToken.tokenStr += Character.toString((char) 10);
                }
                else if(textCharM[iColPos] == 't')
                {
                  nextToken.tokenStr += Character.toString((char) 9);
                }
                else if(textCharM[iColPos] == 'a')
                {
                  nextToken.tokenStr += Character.toString((char) 7);
                }
                iColPos++;
              }
              else 
              {
                nextToken.tokenStr += textCharM[iColPos];
                iColPos++;
              }
              break;
            case "NUMBER":
              if(textCharM[iColPos] == '.'){

                if(flagDecimal == true) {
                  throw new InvalidFloatException(nextToken.tokenStr,textCharM,iColPos,iSourceLineNr);
                }

                flagDecimal = true;
                nextToken.subClassif = SubClassif.FLOAT;
              }
              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              break;
            case "OPERAND":
              // Check for invalid literal
              if(nextToken.subClassif == SubClassif.INTEGER || nextToken.subClassif == SubClassif.FLOAT){
                throw new InvalidLiteralException(nextToken.tokenStr, textCharM, iColPos, iSourceLineNr);
              }

              nextToken.tokenStr += textCharM[iColPos];
              iColPos++;
              //End of Operand Check
              if(iColPos > textCharM.length - 1 || !getType(textCharM[iColPos]).equals("OPERAND") && !getType(textCharM[iColPos]).equals("NUMBER"))
              {
                STEntry htSearchResult = symbol.getSymbol(nextToken.tokenStr);
                if(htSearchResult == null && iColPos > textCharM.length - 1)
                {
                  currentToken = nextToken;
                  return currentToken.tokenStr;
                }
                else if(htSearchResult != null)
                {
                  nextToken.primClassif = htSearchResult.primClassif;
                  if(htSearchResult instanceof STControl)
                  {
                    STControl subHolder = (STControl) htSearchResult;
                    nextToken.subClassif = subHolder.subClassif;
                  }
                  currentToken = nextToken;
                  return currentToken.tokenStr;
                }
              }
              break;
            default:
          }
      }
    }
    //Return for end of file
    return "";
  }
  

  /**
  * Returns a String to represent the identifier of a character.
  * <p>
  * The return is either STRING,NUMBER,OPERATOR,SEPERATOR,SPACE, or OPERAND. It
  *    takes in a char input which is the character to be sorted and checks for match.
  *
  * @param input      Type char to be sorted.
  *
  * @return       String with values of either STRING,NUMBER,OPERATOR,SEPERATOR,SPACE, or OPERAND
  */
  private String getType(char input)
  {
    //Special cases first
    //String 
    if(flagString || input == '\"' || input == '\'')
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
    //Operand
    else
    {
      return "OPERAND";
    }
    return "";
  }
}