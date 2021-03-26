package pickle;

import java.util.HashMap;

public class StorageManager {

    public static HashMap VariableTable = new HashMap<String, ResultValue>();

    public StorageManager(){
    }

    public void Declare(String variableName, ResultValue value){
        if(VariableTable.containsKey(variableName)){
            //throw error
        }
        else{
            VariableTable.put(variableName, value);
        }
    }

    public ResultValue Assign(String variableName, ResultValue value){
            VariableTable.put(variableName, value);
            return value;
    }

    public ResultValue getVariableValue(String varName){
        return (ResultValue) VariableTable.get(varName);
    }

}
