package pickle;

public class STEntry {
    public String symbol = "";
    public Classif primClassif = Classif.EMPTY;
    public STEntry(String s, Classif pClassif)
    {
       this.primClassif =  pClassif;
       this.symbol = s;
    }
}
