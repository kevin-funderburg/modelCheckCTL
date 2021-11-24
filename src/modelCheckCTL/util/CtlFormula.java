package modelCheckCTL.util;

import java.util.*;

enum SATkind
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


    private List<State> SAT(String expression) throws Exception {
        List<State> states = new LinkedList<State>();
        SATkind satkind = SATkind.AX;   //placeholder, remove later
        switch (satkind)
        {
            case AllTrue -> {

                break;
            }
            case AllFalse -> {

                break;
            }
            case Atomic -> {

                break;
            }
            case Not -> {

                break;
            }
            case And -> {

                break;
            }
            case Or -> {

                break;
            }
            case Implies -> {

                break;
            }
            case AX -> {

                break;
            }
            case EX -> {

                break;
            }
            case AU -> {

                break;
            }
            case EU -> {

                break;
            }
            case EF -> {

                break;
            }
            case EG -> {

                break;
            }
            case AF -> {

                break;
            }
            case AG -> {

                break;
            }
            case Unknown -> {
                throw new Exception("Invalid CTL expression");
            }
        }

        return states;
    }

    public SATkind getSATtype
}
