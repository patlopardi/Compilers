package pickle;

public class STControl extends STEntry{
    public SubClassif subClassif = SubClassif.EMPTY;
    public STControl(String s, Classif pClassif, SubClassif sClassif) {
        super(s, pClassif);
        subClassif = sClassif;
    }
}
