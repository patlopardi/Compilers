package pickle;

public class ResultValue {

    public  Object value;
    public SubClassif dataType;

    public ResultValue(SubClassif dataType, Object value, String structure, String terminating){
        this.dataType = dataType;
        this.value = value;
    }



}
