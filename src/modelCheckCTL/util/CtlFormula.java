package modelCheckCTL.util;

import java.util.*;

enum SATkind
{
    UNKNOWN,
    ALL_TRUE,
    ALL_FALSE,
    ATOMIC,
    NOT,
    AND,
    OR,
    IMPLIES,
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

    public KripkeStructure kripke;
    public State state;
    public String expression;

    String leftExpr;
    String rightExpr;

    public CtlFormula(String expr, State s, KripkeStructure k)
    {
        kripke = k;
        state = s;
        expression = swapSymbols(expr);
    }

    public String swapSymbols(String expr)
    {
        String[][] symbols = {
                {"and", "&"},
                {"or", "|"},
                {"->", ">"},
                {"not", "!"},
            };

        for (String[] symbol : symbols)
            expr = expr.replace(symbol[0], symbol[1]);

        return expr;
    }

    /**
     * calculate satisfiability for given CTL
     * @return
     */
    public boolean satisfies() throws Exception {
        List<State> states = SAT(expression);
        return states.contains(state);
    }

//    private SATkind getSATkind(String expr, String leftExpr, String rightExpr)
    private SATkind getSATkind(String expr)
    {

        //remove extra brackets
        expression = RemoveExtraBrackets(expression);
        System.out.println("expression after bracket strip: " + expression);

        //look for binary implies
        if (expression.contains(">"))
        {
//            if (IsBinaryOp(expression, ">", ref leftExpression, ref rightExpression))
            if (IsBinaryOp(expression, ">"))
                return SATkind.IMPLIES;
        }
        //look for binary and
        if (expression.contains("&"))
        {
//            if (IsBinaryOp(expression, "&", ref leftExpression, ref rightExpression))
            if (IsBinaryOp(expression, "&"))
                return SATkind.AND;
        }
        //look for binary or
        if (expression.contains("|"))
        {
//            if (IsBinaryOp(expression, "|", ref leftExpression, ref rightExpression);
            if (IsBinaryOp(expression, "|"))
                return SATkind.OR;
        }
        //look for binary AU
        if (expression.startsWith("A("))
        {
            String strippedExpression = expression.substring(2, expression.length() - 3);
//            if (IsBinaryOp(strippedExpression, "U", ref leftExpression, ref rightExpression))
            if (IsBinaryOp(strippedExpression, "U"))
                return SATkind.AU;
        }
        //look for binary EU
        if (expression.startsWith("E("))
        {
            String strippedExpression = expression.substring(2, expression.length() - 3);
//            if (IsBinaryOp(strippedExpression, "U", ref leftExpression, ref rightExpression))
            if (IsBinaryOp(strippedExpression, "U"))
                return SATkind.EU;
        }

        //look for unary T, F, !, AX, EX, AG, EG, AF, EF, atomic
        if (expression.equals("T"))
        {
            leftExpr = expression;
            return SATkind.ALL_TRUE;
        }
        if (expression.equals("F"))
        {
            leftExpr = expression;
            return SATkind.ALL_FALSE;
        }
        if (isAtomic(expression))
        {
            leftExpr = expression;
            return SATkind.ATOMIC;
        }
        if (expression.startsWith("!"))
        {
            leftExpr = expression.substring(1, expression.length() - 1);
            return SATkind.NOT;
        }
        if (expression.startsWith("AX"))
        {
            leftExpr = expression.substring(2, expression.length() - 2);
            return SATkind.AX;
        }
        if (expression.startsWith("EX"))
        {
            leftExpr = expression.substring(2, expression.length() - 2);
            return SATkind.EX;
        }
        if (expression.startsWith("EF"))
        {
            leftExpr = expression.substring(2, expression.length() - 2);
            return SATkind.EF;
        }
        if (expression.startsWith("EG"))
        {
//            leftExpr = expression.substring(2, expression.length() - 2);
            leftExpr = expression.substring(2, expression.length() - 1);
            return SATkind.EG;
        }
        if (expression.startsWith("AF"))
        {
            leftExpr = expression.substring(2, expression.length() - 2);
            return SATkind.AF;
        }
        if (expression.startsWith("AG"))
        {
            leftExpr = expression.substring(2, expression.length() - 2);
            return SATkind.AG;
        }

        return SATkind.UNKNOWN;

    }

    /**
     * determine SAT kind for expression
     * @param expression
     * @return
     * @throws Exception
     */
//    private List<State> SAT(String expression) throws Exception {
    private List<State> SAT(String expression) throws Exception {

        List<State> states = new LinkedList<State>();

        String leftExpr = "";
        String rightExpr = "";

//        SATkind satkind = getSATkind(expression, leftExpr, rightExpr);
        SATkind satkind = getSATkind(expression);

        System.out.println("SAT kind: " + satkind);
        System.out.println("Left expression: " + leftExpr);
        System.out.println("Right expression: " + rightExpr);



        switch (satkind)
        {
            case ALL_TRUE -> {
                states.addAll(kripke.states);
                break;
            }
            case ALL_FALSE -> {
                break;
            }
            case ATOMIC -> {
                for (State state : kripke.states)
                {
                    if (state.atoms.contains(leftExpr))
                        states.add(state);
                }
                break;
            }
            case NOT -> {
                states.addAll(kripke.states);
                List<State> f1States = SAT(leftExpr);

                for (State state : f1States)
                    states.remove(state);

                break;
            }
            case AND -> {
                List<State> andF1States = SAT(leftExpr);
                List<State> andF2States = SAT(rightExpr);

                for (State state : andF1States)
                {
                    if (andF2States.contains(state))
                        states.add(state);
                }
                break;
            }
            case OR -> {
                List<State> orF1States = SAT(leftExpr);
                List<State> orF2States = SAT(rightExpr);

                states = orF1States;
                for (State state : orF2States)
                {
                    if (!states.contains(state))
                        states.add(state);
                }
                break;
            }
            case IMPLIES -> {
                String impliesFormula = "!" + leftExpr + "|" + rightExpr;
                states = SAT(impliesFormula);
                break;
            }
            case AX -> {
                String axFormula = "!EX!" + leftExpr;
                states = SAT(axFormula);

                //check if states actually has link to next state
                List<State> tempStates = new LinkedList<State>();
                for (State sourceState : states)
                {
                    for (Transition transition : kripke.transitions)
                    {
                        if (sourceState.equals(transition.FromState))
                        {
                            tempStates.add(sourceState);
                            break;
                        }
                    }
                }
                states = tempStates;
                break;
            }
            case EX -> {
                //TODO: reevaluate exFormula
                String exFormula = leftExpr;
                states = SAT_EX(exFormula);
                break;
            }
            case AU -> {
                StringBuilder auFormulaBuilder = new StringBuilder();
                auFormulaBuilder.append("!(E(!");
                auFormulaBuilder.append(rightExpr);
                auFormulaBuilder.append("U(!");
                auFormulaBuilder.append(leftExpr);
                auFormulaBuilder.append("&!");
                auFormulaBuilder.append(rightExpr);
                auFormulaBuilder.append("))|(EG!");
                auFormulaBuilder.append(rightExpr);
                auFormulaBuilder.append("))");
                states = SAT(auFormulaBuilder.toString());
                break;
            }
            case EU -> {
                states = SAT_EU(leftExpr, rightExpr);
                break;
            }
            case EF -> {
                String efFormula = "E(TU" + leftExpr + ")";
                states = SAT(efFormula);
                break;
            }
            case EG -> {
                String egFormula = "!AF!" + leftExpr;
                states = SAT(egFormula);
                break;
            }
            case AF -> {
                String afFormula = leftExpr;
                states = SAT_AF(afFormula);
                break;
            }
            case AG -> {
                String agFormula = "!EF!" + leftExpr;
                states = SAT(agFormula);
                break;
            }
            case UNKNOWN -> {
                throw new Exception("Invalid CTL expression");
            }
        }

        return states;
    }

    private List<State> SAT_EX(String expression) throws Exception {
        //X := SAT (φ);
        //Y := pre∃(X);
        //return Y
        List<State> x;
        List<State> y;
        x = SAT(expression);
        y = PreE(x);
        return y;
    }

    private List<State> SAT_EU(String leftExpression, String rightExpression) throws Exception {
        List<State> w = new LinkedList<State>();
        List<State> x = new LinkedList<State>();
        List<State> y = new LinkedList<State>();

        w = SAT(leftExpression);
        x.addAll(kripke.states);
        y = SAT(rightExpression);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> newY = new LinkedList<State>();
            List<State> preEStates = PreE(y);

            newY.addAll(y);
            List<State> wAndPreE = new LinkedList<State>();
            for (State state : w)
            {
                if (preEStates.contains(state))
                    wAndPreE.add(state);
            }

            for (State state : wAndPreE)
            {
                if (!newY.contains(state))
                    newY.add(state);
            }
            y = newY;
        }

        return y;
    }

    private List<State> SAT_AF(String expression) throws Exception {
        List<State> x = new LinkedList<State>();
        x.addAll(kripke.states);
        List<State> y = new LinkedList<State>();
        y = SAT(expression);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> newY = new LinkedList<State>();
            List<State> preAStates = PreA(y);
            newY.addAll(y);

            for (State state : preAStates)
            {
                if (!newY.contains(state))
                    newY.add(state);
            }

            y = newY;
        }

        return y;
    }

    private List<State> PreE(List<State> y)
    {
        //{s ∈ S | exists s, (s → s and s ∈ Y )}
        List<State> states = new LinkedList<State>();

        List<Transition> transitions = new LinkedList<Transition>();
        for (State sourceState : kripke.states)
        {
            for (State destState : y)
            {
                Transition myTransition = new Transition(sourceState, destState);
                if (kripke.transitions.contains(myTransition))
                {
                    if (!states.contains(sourceState))
                        states.add(sourceState);
                }
            }
        }

        return states;
    }

    private List<State> PreA(List<State> y)
    {
        //pre∀(Y ) = pre∃y − pre∃(S − Y)
        List<State> PreEY = PreE(y);

        List<State> S_Minus_Y = new LinkedList<State>(kripke.states);

        for (State state : y)
        {
            if (S_Minus_Y.contains(state))
                S_Minus_Y.remove(state);
        }

        List<State> PreE_S_Minus_Y = PreE(S_Minus_Y);

        //PreEY - PreE(S-Y)
        for (State state : PreE_S_Minus_Y)
        {
            if (PreEY.contains(state))
                PreEY.remove(state);
        }

        return PreEY;
    }

//    private boolean IsBinaryOp(String expression, String symbol, String leftExpression, String rightExpression)
    private boolean IsBinaryOp(String expression, String symbol)
    {
        boolean isBinaryOp = false;
        if (expression.contains(symbol))
        {
            int openParanthesisCount = 0;
            int closeParanthesisCount = 0;

            for (int i = 0; i < expression.length(); i++)
            {
                String currentChar = expression.substring(i, 1);
                if (currentChar.equals(symbol) && openParanthesisCount == closeParanthesisCount)
                {
//                    leftExpression = expression.substring(0, i);
//                    rightExpression = expression.substring(i + 1, expression.length() - i - 1);
                    leftExpr = expression.substring(0, i);
                    rightExpr = expression.substring(i + 1, expression.length() - i - 1);
                    isBinaryOp = true;
                    break;
                }
                else if (currentChar.equals("("))
                {
                    openParanthesisCount++;
                }
                else if (currentChar.equals(")"))
                {
                    closeParanthesisCount++;
                }
            }
        }
        return isBinaryOp;
    }

    private boolean AreListStatesEqual(List<State> list1, List<State> list2)
    {
        if (list1.size() != list2.size())
            return false;

        for (State state : list1)
        {
            if (!list2.contains(state))
                return false;
        }

        return true;
    }

    private boolean isAtomic(String expression) { return kripke.atoms.contains(expression); }

    //    private splitExpression(String )
    private String RemoveExtraBrackets(String expression)
    {
        String newExpression = expression;
        int openParanthesis = 0;
        int closeParanthesis = 0;

        if (expression.startsWith("(") && expression.endsWith(")"))
        {
            for (int i = 0; i < expression.length() - 1; i++)
            {
                String charExpression = expression.substring(i, 1);

                if (charExpression.equals("("))
                    openParanthesis++;
                else if (charExpression.equals(")"))
                    closeParanthesis++;
            }

            if (openParanthesis - 1 == closeParanthesis)
                newExpression = expression.substring(1, expression.length() - 2);
        }
        return newExpression;
    }

}
