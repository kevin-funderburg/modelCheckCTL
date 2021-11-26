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
                return SATkind.Implies;
        }
        //look for binary and
        if (expression.contains("&"))
        {
//            if (IsBinaryOp(expression, "&", ref leftExpression, ref rightExpression))
            if (IsBinaryOp(expression, "&"))
                return SATkind.And;
        }
        //look for binary or
        if (expression.contains("|"))
        {
//            if (IsBinaryOp(expression, "|", ref leftExpression, ref rightExpression);
            if (IsBinaryOp(expression, "|"))
                return SATkind.Or;
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
            return SATkind.AllTrue;
        }
        if (expression.equals("F"))
        {
            leftExpr = expression;
            return SATkind.AllFalse;
        }
//        if (IsAtomic(expression))
//        {
//            leftExpr = expression;
//            return SATkind.Atomic;
//        }
        if (expression.startsWith("!"))
        {
            leftExpr = expression.substring(1, expression.length() - 1);
            return SATkind.Not;
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

        return SATkind.Unknown;

    }

    /**
     * determine SAT kind for expression
     * @param expression
     * @return
     * @throws Exception
     */
//    private List<State> SAT(String expression) throws Exception {
    public List<State> SAT(String expression) throws Exception {

        List<State> states = new LinkedList<State>();

        String leftExpr = "";
        String rightExpr = "";

//        SATkind satkind = getSATkind(expression, leftExpr, rightExpr);
        SATkind satkind = getSATkind(expression);

        System.out.println("SAT kind: " + satkind);
        System.out.println("Left expression: " + leftExpr);
        System.out.println("Right expression: " + rightExpr);



//        switch (satkind)
//        {
//            case AllTrue -> {
//
//                break;
//            }
//            case AllFalse -> {
//
//                break;
//            }
//            case Atomic -> {
//
//                break;
//            }
//            case Not -> {
//
//                break;
//            }
//            case And -> {
//
//                break;
//            }
//            case Or -> {
//
//                break;
//            }
//            case Implies -> {
//
//                break;
//            }
//            case AX -> {
//
//                break;
//            }
//            case EX -> {
//
//                break;
//            }
//            case AU -> {
//
//                break;
//            }
//            case EU -> {
//
//                break;
//            }
//            case EF -> {
//
//                break;
//            }
//            case EG -> {
//
//                break;
//            }
//            case AF -> {
//
//                break;
//            }
//            case AG -> {
//
//                break;
//            }
//            case Unknown -> {
//                throw new Exception("Invalid CTL expression");
//            }
//        }

        return states;
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
