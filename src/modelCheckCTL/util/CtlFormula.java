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

    KripkeStructure _kripke;
    State _state;
    String _expression;
    Hashtable<String, String> _toString;

    public CtlFormula(String expression, State state, KripkeStructure kripke)
    {
        _toString = new Hashtable<String, String>();
    }
}
