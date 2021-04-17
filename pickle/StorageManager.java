package pickle;

import pickle.Exceptions.InvalidDeclarationException;
import pickle.Exceptions.InvalidFloatException;
import pickle.Exceptions.InvalidLiteralException;
import pickle.Exceptions.InvalidStringException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class StorageManager {

    public static HashMap<String, ResultValue> VariableTable = new HashMap<String, ResultValue>();
    public static HashMap<String, ArrayList<ResultValue>> VariableTableArr = new HashMap<String, ArrayList<ResultValue>>();

    public StorageManager(){
    }

    /**
     * <p>
     * This function adds a new value to the HashMap and if the value
     *      is already in the HashMap it will throw an error
     */
    public void Declare(String variableName, ResultValue value){
        if(VariableTable.containsKey(variableName)){
            // throw error
        }
        else{
            VariableTable.put(variableName, value);
        }
    }


    /**
     * <p>
     * This function assigns a new value to a variable and checks to see if
     *      if the variable it's assigned to has the same data type
     *
     */
    public ResultValue Assign(String variableName, ResultValue value){
            VariableTable.put(variableName, value);
            return value;
    }

    /**
     * <p>
     * This function initializes an empty array of null objects and adds a EndOfArr value to the end
     * so that the program knows the data type of the array
     *
     * @param scan Scanner to count the number of elements in a preset array
     * @param variableName Name of array
     * @param classif SubClassif of the array
     * @param index Index in the array
     *
     */
    public void DeclareArr(Scanner scan, String variableName, String index, SubClassif classif) throws InvalidFloatException, InvalidLiteralException, InvalidStringException {

        int begCol = 0;
        ArrayList<ResultValue> resultArr = new ArrayList<ResultValue>();

        ResultValue EndOfArr = new ResultValue(classif, "ENDOFARR", "", "" );

        // for preset arrays
        if (index.equals("]")){
            while(!scan.getNext().equals(";")){
                if(scan.currentToken.tokenStr.equals(",") && scan.currentToken.primClassif.name().equals("SEPARATOR") ){
                    resultArr.add(null);
                }
            }
            resultArr.add(null);
            resultArr.add(EndOfArr);
        }
        else{
            int subscriptValue= Integer.valueOf(index);
            for(int i = 0; i < subscriptValue; i++){
                resultArr.add(null);
                if((i + 1) == subscriptValue){
                    resultArr.add(EndOfArr);
                }
            }
        }

        scan.iColPos = begCol;
        VariableTableArr.put(variableName, resultArr);

    }

    /**
     * <p>
     * Assigns a value to an array and coerces the assigned value into
     * the value of the array
     *
     * @param variableName Name of array
     * @param index Index in the array
     * @param value resultValue to be assigned in the array
     *
     */

    public void AssignArr(String variableName, String index, ResultValue value){
        //Check array exists
        int indexInt = 0;
        ArrayList<ResultValue> res = VariableTableArr.get(variableName);
        SubClassif arrayDataType = res.get(res.size()).dataType;

        if( value.dataType !=  arrayDataType){
            if(arrayDataType == SubClassif.INTEGER){
                switch (value.dataType.name()){
                    case "FLOAT":
                        value.dataType = SubClassif.FLOAT;
                        break;
                    case "STRING":
                        value.dataType = SubClassif.STRING;
                        break;
                }
            }
            if(arrayDataType == SubClassif.FLOAT){
                switch (value.dataType.name()){
                    case "INTEGER":
                        value.dataType = SubClassif.INTEGER;
                        break;
                    case "STRING":
                        value.dataType = SubClassif.STRING;
                        break;
                }
            }

            if(arrayDataType == SubClassif.STRING){
                switch (value.dataType.name()){
                    case "FLOAT":
                        value.dataType = SubClassif.FLOAT;
                        break;
                    case "INTEGER":
                        value.dataType = SubClassif.INTEGER;
                        break;
                }

            }
        }

        if(index == null){
            for(int i = 0; i < res.size() - 1; i++){
                if(res.get(i) == null){
                    res.set(i, value);
                }
            }
        }
        else{
            indexInt = Integer.parseInt(index);
        }

        if(indexInt < 0){
            res.set((res.size() - 1) - indexInt, value);
        }
        else{
            res.set(indexInt, value);
        }
    }


    /**
     * <p>
     * This function grabs the value of a variable in the HashMap
     * @param varName variable name
     */
    public ResultValue getVariableValue(String varName){
        return (ResultValue) VariableTable.get(varName);
    }

    /**
     * <p>
     * This function grabs the array value of a variable in the HashMap
     *
     * @param varName variable name
     */
    public ArrayList<ResultValue> getArrayValue(String varName){
        return (ArrayList<ResultValue>) VariableTableArr.get(varName);
    }


}
