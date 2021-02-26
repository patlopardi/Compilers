package pickle;

public class STControl extends STEntry{
    public SubClassif subClassif = SubClassif.EMPTY;
    /**
    * Constructor for the STControl class.
    * <p>
    * Sets the subClassif variable and pClassif variable
    *
    * @param s             String of token entry to set from extension
    * @param pClassif      Classif object to set from extension
    * @param SubClassif    SubClassif object to set
    *
    * @return      N/A
    */
    public STControl(String s, Classif pClassif, SubClassif sClassif) {
        super(s, pClassif);
        subClassif = sClassif;
    }
}
