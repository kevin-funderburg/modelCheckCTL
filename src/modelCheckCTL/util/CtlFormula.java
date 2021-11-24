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
    Hashtable<String, String> symbolDict;

    public CtlFormula(String expr, State s, KripkeStructure k)
    {
        symbolDict = new Hashtable<String, String>();
        symbolDict.put("and", "&");
        symbolDict.put("or", "|");
        symbolDict.put("->", ">");
        symbolDict.put("not", "!");

        kripke = k;
        state = s;
        expression = expr;
    }

    public String swapSymbols(String expr)
    {
        symbolDict.forEach((key, value) -> expr.replace(key, value));
        return expr;
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

    private SATkind getSATkind(String expr, String leftExpr, String rightExpr)
    {
        return SATkind.AF;
    }
}
