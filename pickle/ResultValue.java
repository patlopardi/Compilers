package pickle;

public class ResultValue {

    public Object value;
    public SubClassif dataType;
    public String terminatingString;

    public ResultValue(SubClassif dataType, Object value, String structure, String terminating){
        this.dataType = dataType;
        this.value = value;
        this.terminatingString = terminating;
    }



}
