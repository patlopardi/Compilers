package pickle;

public class STEntry {
    public String symbol = "";
    public Classif primClassif = Classif.EMPTY;
    /**
    * Constructor for the STEntry class.
    * <p>
    * Sets the pClassif variable to the primClassif and sets s to symbol
    *
    * @param s             String of token entry to be set
    * @param pClassif      Classif object to set
    *
    * @return      N/A
    */
    public STEntry(String s, Classif pClassif)
    {
       this.primClassif =  pClassif;
       this.symbol = s;
    }
}
