package pickle;

import java.util.HashMap;

public class SymbolTable
{

  HashMap<String, STEntry> ht = new HashMap<>();
  public SymbolTable(){
    initGlobal();
  }
  public void initGlobal(){
    //initiating the global symbol table
    ht.put("if", new STControl("if",Classif.CONTROL, SubClassif.FLOW));
    ht.put("print", new STFunction("print",Classif.FUNCTION,SubClassif.VOID
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("and", new STEntry("and", Classif.OPERATOR));
    ht.put("or", new STEntry("or", Classif.OPERATOR));
    ht.put("Float", new STControl("Float",Classif.CONTROL,SubClassif.DECLARE));
    ht.put("Int", new STControl("Int",Classif.CONTROL,SubClassif.DECLARE));
    ht.put("endif", new STControl("endif",Classif.CONTROL,SubClassif.END));
    ht.put("for", new STControl("for",Classif.CONTROL,SubClassif.FLOW));
    ht.put("def", new STControl("def",Classif.CONTROL,SubClassif.FLOW));
    ht.put("enddef", new STControl("enddef",Classif.CONTROL,SubClassif.END));
    ht.put("else", new STControl("else",Classif.CONTROL,SubClassif.END));
    ht.put("endfor", new STControl("endfor",Classif.CONTROL,SubClassif.END));
    ht.put("while", new STControl("while",Classif.CONTROL,SubClassif.FLOW));
    ht.put("endwhile", new STControl("endwhile",Classif.CONTROL,SubClassif.END));
    ht.put("String", new STControl("String",Classif.CONTROL,SubClassif.DECLARE));
    ht.put("Bool", new STControl("Bool",Classif.CONTROL,SubClassif.DECLARE));
    ht.put("Date", new STControl("Date",Classif.CONTROL,SubClassif.DECLARE));
    ht.put("LENGTH", new STFunction("LENGTH",Classif.FUNCTION,SubClassif.INTEGER
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("MAXLENGTH", new STFunction("MAXLENGTH",Classif.FUNCTION,SubClassif.INTEGER
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("SPACES", new STFunction("SPACES",Classif.FUNCTION,SubClassif.INTEGER
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("ELEM", new STFunction("ELEM",Classif.FUNCTION,SubClassif.INTEGER
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("MAXELEM", new STFunction("MAXELEM",Classif.FUNCTION,SubClassif.INTEGER
            , SubClassif.BUILTIN, "VAR_ARGS"));
    ht.put("not", new STEntry("and", Classif.OPERATOR));
    ht.put("in", new STEntry("and", Classif.OPERATOR));
    ht.put("notin", new STEntry("and", Classif.OPERATOR));
  }

  /**
   * putSymbol() puts a symbol into the symbol table
   * @param symbol the symbol to add
   * @param entry the entry for that symbol
   */
  public void putSymbol(String symbol, STEntry entry){
    ht.put(symbol, entry);
  }

  /**
   * gets an entry that corresponds with an entry of a symbol table
   * @param symbol the symbol to look for
   * @return return the entry that corresponds to the symbol
   */
  public STEntry getSymbol(String symbol){
    STEntry ret = ht.get(symbol);
    if(ret != null){
      return ret;
    }
    //System.out.println("Error: No such value in the symbol table");
    return null;
  }
}
