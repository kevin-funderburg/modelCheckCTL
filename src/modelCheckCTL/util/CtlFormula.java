package modelCheckCTL.util;

import java.util.Dictionary;
import java.util.Hashtable;

enum TypeSAT
{
    Unknown,
    AllTrue,
    AllFalse,
    Atomic,
    Not,
    And,
    Or,
    Implies,
    AX,
    EX,
    AU,
    EU,
    EF,
    EG,
    AF,
    AG
}

public class CtlFormula {

    KripkeStructure kripke;
    State state;
    String expression;
    Hashtable<String, String> toString;

    public CtlFormula(String expr, State s, KripkeStructure krip)
    {
        toString = new Hashtable<String, String>();
        toString.put("and", "&");
        toString.put("or", "|");
        toString.put("→", ">");
        toString.put("not", "!");

        kripke = k;
        state = s;
        expression = expr;
    }
}
