package pickle;

import java.util.HashMap;

public class StorageManager {

    public static HashMap VariableTable = new HashMap<String, ResultValue>();

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
