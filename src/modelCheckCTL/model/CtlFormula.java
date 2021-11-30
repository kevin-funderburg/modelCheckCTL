package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelCheckCTL.util.Constants;
import modelCheckCTL.util.ExpressionUtils;

public class CtlFormula {

	private KripkeStructure kripkeStructure;
	public String expression;
	private Map<String, String> conversionMap;

	public CtlFormula(String expression, State state, KripkeStructure kripkeStructure) {

		conversionMap = new HashMap<>();
		loadConversionMap(conversionMap);
		this.kripkeStructure = kripkeStructure;
		this.expression = convertToCTLFormula(expression, conversionMap);
	}

//	public boolean satisfies() throws Exception {
////		ModelVerifier verifier = new ModelVerifier(expression, state, kripkeModel);
//		List<ModelState> states = sat(expression);
//		return states.contains(state);
//	}
	public List<State> sat(String expression) throws Exception {

		List<State> statesList = new ArrayList<>();
		ExpressionUtils ex = new ExpressionUtils(expression);
		String satType = getFormulaType(ex, this.kripkeStructure);
		expression = ex.expression;
		String leftExpr = ex.leftExpr;
		String rightExpr = ex.rightExpr;

		switch (satType) {
		case Constants.ALLTRUE:
			statesList.addAll(kripkeStructure.stateList);
			break;
		case Constants.ALLFALSE:
			break;
		case Constants.ATOMIC:
			if (!kripkeStructure.atomsList.contains(leftExpr))
				throw new Exception("Ivalid atom present in the formula");
			for (State state : kripkeStructure.stateList) {
				if (state.atomsList.contains(leftExpr))
					statesList.add(state);
			}
			break;
		case Constants.NOT:
			statesList.addAll(kripkeStructure.stateList);
			for (State state : sat(leftExpr)) {
				if (statesList.contains(state))
					statesList.remove(state);
			}
			break;
		case Constants.AND:
			List<State> andf1List = sat(leftExpr);
			List<State> andf2List = sat(rightExpr);
			for (State state : andf1List) {
				if (andf2List.contains(state))
					statesList.add(state);
			}
			break;
		case Constants.OR:
			List<State> orf1List = sat(leftExpr);
			List<State> orf2List = sat(rightExpr);
			statesList = orf1List;
			for (State state : orf2List) {
				if (!statesList.contains(state))
					statesList.add(state);
			}
			break;
		case Constants.IMPLIES:
			String impliesFormula = "!" + leftExpr + "|" + rightExpr;
			statesList = sat(impliesFormula);
			break;
		case Constants.AX:
			String axFormula = "!" + "EX" + "!" + leftExpr;
			statesList = sat(axFormula);
			List<State> tempList = new ArrayList<>();
			for (State state : statesList) {
				for (Transition trans : kripkeStructure.transList) {
					if (state.equals(trans.fromState)) {
						tempList.add(state);
						break;
					}
				}
			}
			statesList = tempList;
			break;

		case Constants.EX:
			statesList = satEX(leftExpr);
			break;
		case Constants.AU:
			StringBuilder sb = new StringBuilder();
			String auFormula = sb.append("!(E(!").append(rightExpr).append("U(!").append(leftExpr).append("&!")
					.append(rightExpr).append("))|(EG!").append(rightExpr).append("))").toString();
			statesList = sat(auFormula);
			break;
		case Constants.EU:
			statesList = satEU(leftExpr, rightExpr);
			break;
		case Constants.EF:
			statesList = sat("E(TU" + leftExpr + ")");
			break;
		case Constants.EG:
			statesList = sat("!AF!" + leftExpr);
			break;
		case Constants.AF:
			statesList = satAF(leftExpr);
			break;
		case Constants.AG:
			statesList = sat("!EF!" + leftExpr);
			break;
		default:
			throw new IllegalArgumentException("Invalid formula ");
		}
		return statesList;
	}

	private List<State> satAF(String expression) throws Exception {
		List<State> tempList = new ArrayList<>();
		List<State> result = new ArrayList<>();
		tempList.addAll(kripkeStructure.stateList);
		result = sat(expression);
		while (!(tempList.size() == result.size() && tempList.containsAll(result))) {
			tempList = result;
			List<State> newTemp = new ArrayList<>();
			List<State> preAStates = preA(result);
			newTemp.addAll(result);
			for (State state : preAStates) {
				if (!newTemp.contains(state))
					newTemp.add(state);
			}
			result = newTemp;
		}

		return result;
	}

	private List<State> preA(List<State> result) {

		List<State> preEYStates = preE(result);
		List<State> diffList = new ArrayList<>();
		diffList.addAll(kripkeStructure.stateList);
		diffList.removeAll(result);
		List<State> preEDiffList = preE(diffList);
		preEYStates.removeAll(preEDiffList);
		return preEYStates;
	}

	private List<State> preE(List<State> result) {

		List<State> states = new ArrayList<>();
		for (State fromState : kripkeStructure.stateList) {
			for (State toState : result) {
				Transition trans = new Transition(fromState, toState);
				if (kripkeStructure.transList.contains(trans)) {
					if (!states.contains(fromState))
						states.add(fromState);
				}
			}
		}
		return states;
	}

	private List<State> satEX(String expression) throws Exception {

		List<State> x = new ArrayList<>();
		List<State> y = new ArrayList<>();
		x = sat(expression);
		y = preE(x);
		return y;
	}

	private List<State> satEU(String leftExpr, String rightExpr) throws Exception {

		List<State> w = new ArrayList<>();
		List<State> x = new ArrayList<>();
		List<State> y = new ArrayList<>();

		w = sat(leftExpr);
		x.addAll(kripkeStructure.stateList);
		y = sat(rightExpr);

		while (!(x.size() == y.size() && x.containsAll(y))) {
			x = y;
			List<State> newY = new ArrayList<>();
			List<State> preEStates = preE(y);
			newY.addAll(y);
			List<State> wAndPreE = new ArrayList<>();
			for (State state : w) {
				if (preEStates.contains(state))
					wAndPreE.add(state);
			}
			for (State state : wAndPreE) {
				if (!newY.contains(state))
					newY.add(state);
			}
			y = newY;
		}
		return y;
	}

	public static String getFormulaType(ExpressionUtils ex, KripkeStructure kripkeStructure) {

		String expression = ex.expression;

		expression = formatBrackets(expression);
		ex.expression = expression;

		if (expression.contains(">") && isBinaryOperater(ex, ">"))
			return Constants.IMPLIES;
		if (expression.contains("&") && isBinaryOperater(ex, "&"))
			return Constants.AND;
		if (expression.contains("|") && isBinaryOperater(ex, "|"))
			return Constants.OR;
		if (expression.startsWith("A(")) {
			ex.expression = ex.expression.substring(2, ex.expression.length() - 1);
			if (isBinaryOperater(ex, "U"))
				return Constants.AU;
		}
		if (expression.startsWith("E(")) {

			ex.expression = ex.expression.substring(2, ex.expression.length() - 1);
			if (isBinaryOperater(ex, "U"))
				return Constants.EU;

		}
		if (expression.equals("T")) {
			ex.leftExpr = expression;
			return Constants.ALLTRUE;
		}
		if (expression.equals("F")) {
			ex.leftExpr = expression;
			return Constants.ALLFALSE;
		}
		if (isAtomicFormula(expression, kripkeStructure)) {
			ex.leftExpr = expression;
			return Constants.ATOMIC;
		}
		if (expression.startsWith("!")) {
			ex.leftExpr = expression.substring(1, expression.length());
			return Constants.NOT;
		}
		if (expression.startsWith("AX")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AX;
		}
		if (expression.startsWith("EX")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EX;
		}
		if (expression.startsWith("EF")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EF;
		}
		if (expression.startsWith("EG")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.EG;
		}
		if (expression.startsWith("AF")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AF;
		}
		if (expression.startsWith("AG")) {
			ex.leftExpr = expression.substring(2, expression.length());
			return Constants.AG;
		}
		return "";
	}

	private static boolean isBinaryOperater(ExpressionUtils ex, String symbol) {

		boolean isBinary = false;
		if (ex.expression.contains(symbol)) {
			int openParen = 0;
			int closeParen = 0;

			for (int i = 0; i < ex.expression.length(); i++) {
				String currChar = ex.expression.substring(i, i + 1);
				if (currChar.equals(symbol) && openParen == closeParen) {
					ex.leftExpr = ex.expression.substring(0, i);
					ex.rightExpr = ex.expression.substring(i + 1, ex.expression.length());
					isBinary = true;
					break;
				} else if (currChar.equals("("))
					openParen++;
				else if (currChar.equals(")"))
					closeParen++;
			}
		}

		return isBinary;
	}

	private static boolean isAtomicFormula(String expression, KripkeStructure kripkeStructure) {
		if (kripkeStructure.atomsList.contains(expression))
			return true;
		return false;
	}

	private static String formatBrackets(String expression) {
		String resultExpr = expression;
		int openParen = 0;
		int closedParen = 0;
		if (expression.startsWith("(") && expression.endsWith(")")) {
			for (int i = 0; i < expression.length() - 1; i++) {
				char charExp = expression.charAt(i);
				if (charExp == '(')
					openParen++;
				if (charExp == ')')
					closedParen++;
			}

			if (openParen - 1 == closedParen)
				resultExpr = expression.substring(1, expression.length() - 1);
		}
		return resultExpr;
	}

	public static void loadConversionMap(Map<String, String> converstionMap) {
		converstionMap.put("and", "&");
		converstionMap.put("or", "|");
		converstionMap.put("->", ">");
		converstionMap.put("not", "!");
		converstionMap.put(" ", "");
	}

	public static String convertToCTLFormula(String expression, Map<String, String> converstionMap) {

		for (Map.Entry<String, String> entry : converstionMap.entrySet()) {
			expression = expression.replace(entry.getKey(), entry.getValue());
		}

		return expression;
	}
}
