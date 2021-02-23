package pickle;

import java.util.ArrayList;

public class STFunction extends STEntry{
    public SubClassif returnType = SubClassif.EMPTY;
    public SubClassif definedBy = SubClassif.EMPTY;
    String numArgs="";
    ArrayList<String> parmList = new ArrayList<>();
    public STFunction(String s, Classif pClassif, SubClassif rt, SubClassif db, String na) {
        super(s, pClassif);
        this.returnType = rt;
        this.definedBy = db;
        this.numArgs = na;
    }
}
