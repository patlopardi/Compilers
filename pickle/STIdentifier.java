package pickle;

public class STIdentifier extends STEntry{
    public Classif dclType = Classif.EMPTY;
    public String structure = "";
    public String parm = "";
    public int nonLocal;
    public STIdentifier(String s, Classif pClassif, Classif dclt, String struct, String p, int nl) {
        super(s, pClassif);
        this.dclType = dclt;
        this.nonLocal = nl;
        this.parm = p;
        this.structure = struct;
    }
}
