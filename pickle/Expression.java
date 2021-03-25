package pickle;

import java.util.*;
import java.io.*;

public class Expression
{

  //Still need: Differentiate between float and int, correct input, and way to reference stored variable values

  //Variables
  private ArrayList<STEntry> inputExpressionArr = new ArrayList<STEntry>();
  private ArrayList<STEntry> operatorStack = new ArrayList<STEntry>();
  private ArrayList<STEntry> operandStack = new ArrayList<STEntry>();
  private String type = "unknown";
  
  /**
    * Evaluator of an infix expression
    * <p>
    *  Uses stack handling inorder to solve infix expressions.
    *
    * @return      STEntry      Value of the result within a STEntry object
    */
  public STEntry Evaluate()
  {
    //Empty array
    if(inputExpressionArr.size() == 0)
    {
      return null;
    }
    //Loop through the array
    while(inputExpressionArr.size() > 0)
    {
      //Sort by type
      switch(inputExpressionArr.get(0).primClassif)
      {
        case OPERATOR:
          OperatorCondition();
          break;
        case OPERAND:
          OperandCondition();
          break;
        case SEPARATOR:
          SeparatorCondition();
          break;
        default:
          System.out.printf("Oops looks like a mistake?");
      }
    }
    while(operatorStack.size() > 0)
    {
        PopNPlace();
    }
    System.out.printf("OUTPUT: " + operandStack.get(0).symbol + "\n");
    return null;
  }
  
    /**
    * Operator handling
    * <p>
    * Normally appends to the operator stack if stack is empty or new operator has higher priority.
    *    If same priority or less, runs the PopNPlace function for evaluating.
    *
    * @return      N/A
    */
  private void OperatorCondition()
  {
    //Normal appending to stack
    if(operatorStack.size() == 0 || !Pemdas())
    {
      //Transfer the operator to the stack
      operatorStack.add(inputExpressionArr.get(0));
      inputExpressionArr.remove(0);
    }
    //Evaluate
    else
    {
      PopNPlace();
    }
  }
  
  /**
    * Basic operand functionality 
    * <p>
    * Adds the input to the operand stack.
    *
    * @return      N/A
    */
  private void OperandCondition()
  {
    //Need to as well account for variable values
    operandStack.add(inputExpressionArr.get(0));
    inputExpressionArr.remove(0);
  }
  
      /**
    * Function for handing parenthesis 
    * <p>
    * If left parenthesis it adds to the operator stack. If right
    *    parenthesis it will continue to pop and solve through the
    *    stacks until an ( operand.
    *
    * @return      N/A
    */
  private void SeparatorCondition()
  {
    if(inputExpressionArr.get(0).symbol.equals("("))
    {
      operatorStack.add(inputExpressionArr.get(0));
      inputExpressionArr.remove(0);
    }
    else if(inputExpressionArr.get(0).symbol.equals(")"))
    {
      inputExpressionArr.remove(0);
      while(!operatorStack.get(operatorStack.size()-1).symbol.equals("("))
      {
        PopNPlace();
      }
      operatorStack.remove(operatorStack.size()-1);
    }
    else
    {
      System.out.printf("Incorrect separator");
    }
  }
  
  
    /**
    * Function to pop the two operands and operator, then calculate it
    * <p>
    * Pops the top operand and second from the top operand off the operand stack. 
    *    It then pops the top operator off the operator stack. Makes the calculation
    *    based off the operator and appends the result to the top of operand stack.
    *
    * @return      N/A
    */
  private void PopNPlace()
  {
    String secondOperand = operandStack.get(operandStack.size()-1).symbol;
    operandStack.remove(operandStack.size()-1);
    String firstOperand = operandStack.get(operandStack.size()-1).symbol;
    operandStack.remove(operandStack.size()-1);
    String operator = operatorStack.get(operatorStack.size()-1).symbol;
    operatorStack.remove(operatorStack.size()-1);
    float tempFloat;
    String newEntry;
    
    switch(operator)
    {
      case "*":
        tempFloat = Float.parseFloat(firstOperand)*Float.parseFloat(secondOperand);
        break;
      case "+":
        tempFloat = Float.parseFloat(firstOperand)+Float.parseFloat(secondOperand);
        break;
      case "-":
        tempFloat = Float.parseFloat(firstOperand)-Float.parseFloat(secondOperand);
        break;
      case "/":
        tempFloat = Float.parseFloat(firstOperand)/Float.parseFloat(secondOperand);
        break;
      case "^":
        tempFloat = Integer.parseInt(firstOperand)^(Integer.parseInt(secondOperand));
        break;
      default:
        tempFloat = 0.0f;
    }
    //If check for if float or int
    newEntry = String.valueOf(tempFloat);
    newEntry = String.valueOf((int)tempFloat);
    operandStack.add(new STEntry(newEntry, Classif.OPERAND));
  }
  
  /**
    * Boolean priority check for top operand and current input operand
    * <p>
    * Checks if current input operand is equal to or less than top operand
    *    of the stack in priority. It returns true if so meaning a pop must
    *    take place. If not then it returns false meaning only add to stack.
    *
    * @return      true    Top of operator stack is higher or equal priority
    *                          than current input.
    *              false   Top of operator stack is lower priority than current
    *                          input.
    */
  private boolean Pemdas()
  {
    if(operatorStack.get(operatorStack.size()-1).symbol.equals("("))
    {
      return false;
    }
    //Using modulo 3 for sorting, higher number = higher priority
    String order = "+*^-/";
    int operator1Val = order.indexOf(inputExpressionArr.get(0).symbol, 0) % 3;
    int operator2Val = order.indexOf(operatorStack.get(operatorStack.size()-1).symbol, 0) % 3;
    if(operator1Val <= operator2Val)
    {
      return true;
    }
    return false;
  }
  
  
  public void Test()
  {
    inputExpressionArr.add(new STEntry("2", Classif.OPERAND));
    inputExpressionArr.add(new STEntry("+", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("5", Classif.OPERAND));
    inputExpressionArr.add(new STEntry("-", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("3", Classif.OPERAND));
    inputExpressionArr.add(new STEntry("*", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("(", Classif.SEPARATOR));
    inputExpressionArr.add(new STEntry("16", Classif.OPERAND));
    inputExpressionArr.add(new STEntry("+", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("6", Classif.OPERAND));
    inputExpressionArr.add(new STEntry("/", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("2", Classif.OPERAND));
    inputExpressionArr.add(new STEntry(")", Classif.SEPARATOR));
    inputExpressionArr.add(new STEntry("-", Classif.OPERATOR));
    inputExpressionArr.add(new STEntry("(", Classif.SEPARATOR));
    inputExpressionArr.add(new STEntry("(", Classif.SEPARATOR));
    inputExpressionArr.add(new STEntry("32", Classif.OPERAND));
    inputExpressionArr.add(new STEntry(")", Classif.SEPARATOR));
    inputExpressionArr.add(new STEntry(")", Classif.SEPARATOR));
    Evaluate();
  }
}