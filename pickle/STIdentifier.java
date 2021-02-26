package pickle;

public class STIdentifier extends STEntry{
    public Classif dclType = Classif.EMPTY;
    public String structure = "";
    public String parm = "";
    public int nonLocal;
    /**
    * Constructor for the STIdentifier class.
    * <p>
    * Sets the String of token, primClassif, dclType, nonLocal, parm, and structure.
    *
    * @param s             String of token entry to be set from extension
    * @param pClassif      Classif object to set from extension
    * @param dclt          Classif object to be set
    * @param struct        String of structure to be set
    * @param p             String of parm to be set
    * @param nl            Int value of nonLocal to be set
    *
    * @return      N/A
    */
    public STIdentifier(String s, Classif pClassif, Classif dclt, String struct, String p, int nl) {
        super(s, pClassif);
        this.dclType = dclt;
        this.nonLocal = nl;
        this.parm = p;
        this.structure = struct;
    }
}
