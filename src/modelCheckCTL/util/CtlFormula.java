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

    public KripkeStructure _kripke;
    public State _state;
    public String _expression;

    String _leftExpression;
    String _rightExpression;

    public CtlFormula(String expression, State state, KripkeStructure kripke)
    {
        this._kripke = kripke;
        this._state = state;
        this._expression = swapSymbols(expression);
        this._leftExpression = "";
        this._rightExpression = "";
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
        List<State> states = SAT(_expression);
        return states.contains(_state);
    }

    private SATkind getSATkind(String expression)
    {

        //remove extra brackets
        expression = RemoveExtraBrackets(expression);
//        System.out.println("expression after bracket strip: " + expression);

        //look for binary implies
        if (expression.contains(">"))
        {
            if (IsBinaryOp(expression, ">"))
                return SATkind.IMPLIES;
        }
        //look for binary and
        if (expression.contains("&"))
        {
            if (IsBinaryOp(expression, "&"))
                return SATkind.AND;
        }
        //look for binary or
        if (expression.contains("|"))
        {
            if (IsBinaryOp(expression, "|"))
                return SATkind.OR;
        }
        //look for binary AU
        if (expression.startsWith("A("))
        {
            String strippedExpression = expression.substring(2, expression.length() - 1);
            if (IsBinaryOp(strippedExpression, "U"))
                return SATkind.AU;
        }
        //look for binary EU
        if (expression.startsWith("E("))
        {
            String strippedExpression = expression.substring(2, expression.length() - 1);
            if (IsBinaryOp(strippedExpression, "U"))
                return SATkind.EU;
        }

        //look for unary T, F, !, AX, EX, AG, EG, AF, EF, atomic
        if (expression.equals("T"))
        {
            _leftExpression = expression;
            return SATkind.ALL_TRUE;
        }
        if (expression.equals("F"))
        {
            _leftExpression = expression;
            return SATkind.ALL_FALSE;
        }
        if (isAtomic(expression))
        {
            _leftExpression = expression;
            return SATkind.ATOMIC;
        }
        if (expression.startsWith("!"))
        {
            _leftExpression = expression.substring(1);
            return SATkind.NOT;
        }
        if (expression.startsWith("AX"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.AX;
        }
        if (expression.startsWith("EX"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.EX;
        }
        if (expression.startsWith("EF"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.EF;
        }
        if (expression.startsWith("EG"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.EG;
        }
        if (expression.startsWith("AF"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.AF;
        }
        if (expression.startsWith("AG"))
        {
            _leftExpression = expression.substring(2);
            return SATkind.AG;
        }
        if (expression.equals("")) {
            _leftExpression = expression;
            return SATkind.ALL_TRUE;
        }

        return SATkind.UNKNOWN;

    }

    /**
     * determine SAT kind for expression
     * @param expression
     * @return
     * @throws Exception
     */
    private List<State> SAT(String expression) throws Exception {
//        System.out.println("Original expression: " + expression);
        expression = expression.trim();

        List<State> states = new LinkedList<State>();

        SATkind satkind = getSATkind(expression);

//        System.out.println("SAT kind: " + satkind);
//        System.out.println("Left expression: " + _leftExpression);
//        System.out.println("Right expression: " + _rightExpression);
//        System.out.println("------------------------------");

        switch (satkind)
        {
            case ALL_TRUE -> {
                states.addAll(_kripke.states);
                break;
            }
            case ALL_FALSE -> {
                break;
            }
            case ATOMIC -> {
                for (State state : _kripke.states)
                {
                    if (state.atoms.contains(_leftExpression))
                        states.add(state);
                }
                break;
            }
            case NOT -> {
                states.addAll(_kripke.states);
                List<State> f1States = SAT(_leftExpression);

                for (State state : f1States)
                    states.remove(state);

                break;
            }
            case AND -> {
                List<State> andF1States = SAT(_leftExpression);
                List<State> andF2States = SAT(_rightExpression);

                for (State state : andF1States)
                {
                    if (andF2States.contains(state))
                        states.add(state);
                }
                break;
            }
            case OR -> {
                List<State> orF1States = SAT(_leftExpression);
                List<State> orF2States = SAT(_rightExpression);

                states = orF1States;
                for (State state : orF2States)
                {
                    if (!states.contains(state))
                        states.add(state);
                }
                break;
            }
            case IMPLIES -> {
                String impliesFormula = "!" + _leftExpression + "|" + _rightExpression;
                states = SAT(impliesFormula);
                break;
            }
            case AX -> {
                String axFormula = "!EX!" + _leftExpression;
                states = SAT(axFormula);

                //check if states actually has link to next state
                List<State> tempStates = new LinkedList<State>();
                for (State sourceState : states)
                {
                    for (Transition transition : _kripke.transitions)
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
                String exFormula = _leftExpression;
                states = SAT_EX(exFormula);
                break;
            }
            case AU -> {
                StringBuilder auFormulaBuilder = new StringBuilder();
                auFormulaBuilder.append("!(E(!");
                auFormulaBuilder.append(_rightExpression);
                auFormulaBuilder.append("U(!");
                auFormulaBuilder.append(_leftExpression);
                auFormulaBuilder.append("&!");
                auFormulaBuilder.append(_rightExpression);
                auFormulaBuilder.append("))|(EG!");
                auFormulaBuilder.append(_rightExpression);
                auFormulaBuilder.append("))");
                states = SAT(auFormulaBuilder.toString());
                break;
            }
            case EU -> {
                states = SAT_EU(_leftExpression, _rightExpression);
                break;
            }
            case EF -> {
                String efFormula = "E(TU" + _leftExpression + ")";
                states = SAT(efFormula);
                break;
            }
            case EG -> {
                String egFormula = "!AF!" + _leftExpression;
                states = SAT(egFormula);
                break;
            }
            case AF -> {
                String afFormula = _leftExpression;
                states = SAT_AF(afFormula);
                break;
            }
            case AG -> {
                String agFormula = "!EF!" + _leftExpression;
                states = SAT(agFormula);
                break;
            }
            case UNKNOWN -> {
                throw new IllegalArgumentException("Invalid CTL expression");
            }
        }

        return states;
    }

    private List<State> SAT_EX(String expression) throws Exception {
        //X := SAT (φ);
        //Y := pre∃(X);
        //return Y
        List<State> x = SAT(expression);
        List<State> y = PreE(x);
        return y;
    }

    private List<State> SAT_EU(String leftExpression, String rightExpression) throws Exception {

        List<State> w = SAT(leftExpression);
        List<State> x = new LinkedList<State>(_kripke.states);
        List<State> y = SAT(rightExpression);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> newY = new LinkedList<State>(y);
            List<State> preEStates = PreE(y);

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

    private List<State> SAT_AF(String expr) throws Exception {
        List<State> x = new LinkedList<>(_kripke.states);
        List<State> y = SAT(expr);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> preAStates = PreA(y);
            List<State> newY = new LinkedList<>(y);

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
        List<State> states = new LinkedList<>();
        List<Transition> transitions = new LinkedList<Transition>();
        for (State sourceState : _kripke.states)
        {
            for (State destState : y)
            {
                Transition myTransition = new Transition(sourceState, destState);
                if (_kripke.transitions.contains(myTransition))
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
        List<State> S_Minus_Y = new LinkedList<State>(_kripke.states);

        for (State state : y)
            S_Minus_Y.remove(state);

        List<State> PreE_S_Minus_Y = PreE(S_Minus_Y);

        //PreEY - PreE(S-Y)
        for (State state : PreE_S_Minus_Y)
            PreEY.remove(state);

        return PreEY;
    }

    private boolean IsBinaryOp(String expression, String symbol)
    {
        boolean isBinaryOp = false;
        if (expression.contains(symbol))
        {
            int openParanthesisCount = 0;
            int closeParanthesisCount = 0;

            int i=0;
            while (i < expression.length() - 1)
            {
                String currentChar = String.valueOf(expression.charAt(i));
                if (currentChar.equals(symbol) && openParanthesisCount == closeParanthesisCount)
                {
                    _leftExpression = expression.substring(0, i);
                    _rightExpression = expression.substring(i + 1);
                    isBinaryOp = true;
                    break;
                } else if (currentChar.equals("(")) {
                    openParanthesisCount++;
                } else if (currentChar.equals(")")) {
                    closeParanthesisCount++;
                }
                i++;
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

    private boolean isAtomic(String expression) { return _kripke.atoms.contains(expression); }

    private String RemoveExtraBrackets(String expression)
    {
        String newExpression = expression;
        int openParanthesis = 0;
        int closeParanthesis = 0;

        if (expression.startsWith("(") && expression.endsWith(")"))
        {
            for (int i = 0; i < expression.length() - 1; i++)
            {
                String charExpression = expression.substring(i, i + 1);

                if (charExpression.equals("("))
                    openParanthesis++;
                else if (charExpression.equals(")"))
                    closeParanthesis++;
            }

            if (openParanthesis - 1 == closeParanthesis)
                newExpression = expression.substring(1, expression.length() - 1);
        }
        return newExpression;

    }
}
