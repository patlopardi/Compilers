package pickle;

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
            //throw error
        }
        else{
            VariableTable.put(variableName, value);
        }
    }

    public void DeclareArr(Scanner scan, String variableName, String index, SubClassif classif) throws InvalidFloatException, InvalidLiteralException, InvalidStringException {

        int begCol = scan.iColPos;
        ArrayList<ResultValue> resultArr = new ArrayList<ResultValue>();

        ResultValue EndOfArr = new ResultValue(classif, "ENDOFARR", "", "" );
        ResultValue res01;

        // for preset arrays int arr[] = 19, 10;
        if (index.equals("]")){
            while(!scan.getNext().equals(";")){
                if(scan.currentToken.subClassif == classif){
                   res01 = new ResultValue(classif, scan.currentToken.tokenStr, "", "" );
                   resultArr.add(res01);
                }
            }
            resultArr.add(EndOfArr);
        }
        else{

            int subscriptValue= Integer.valueOf(index);
            boolean completedAssignmentFlag = false;
            for(int i = 0; i < subscriptValue; i++){
                while(!scan.getNext().equals(";") && !completedAssignmentFlag){
                    if(scan.currentToken.subClassif == classif){
                        res01 = new ResultValue(classif, scan.currentToken.tokenStr, "", "" );
                        resultArr.add(res01);
                        i++;
                    }
                }
                completedAssignmentFlag = true;
                resultArr.add(null);
                if((i + 1) == subscriptValue){
                    resultArr.add(EndOfArr);
                }


            }
        }

        VariableTableArr.put(variableName, resultArr);
    }

    public void AssignArr(String variableName, String index, ResultValue value){
        //Check array exists
        int indexInt = 0;
        ArrayList<ResultValue> res = VariableTableArr.get(variableName);

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
     * This function grabs the value of a variable in the HashMap
     */
    public ResultValue getVariableValue(String varName){
        return (ResultValue) VariableTable.get(varName);
    }

}
