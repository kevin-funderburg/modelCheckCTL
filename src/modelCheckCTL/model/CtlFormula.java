package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelCheckCTL.util.Constants;
import modelCheckCTL.util.ExpressionUtils;
import modelCheckCTL.util.CtlFormulaUtils;

public class CtlFormula {

	private KripkeStructure kripkeStructure;
	public String expression;
	private Map<String, String> converstionMap;

	public CtlFormula(String expression, State state, KripkeStructure kripkeStructure) {

		converstionMap = new HashMap<>();
		CtlFormulaUtils.loadConversionMap(converstionMap);
		this.kripkeStructure = kripkeStructure;
		this.expression = CtlFormulaUtils.convertToCTLFormula(expression, converstionMap);
	}

//	public boolean satisfies() throws Exception {
////		ModelVerifier verifier = new ModelVerifier(expression, state, kripkeModel);
//		List<ModelState> states = sat(expression);
//		return states.contains(state);
//	}
	public List<State> sat(String expression) throws Exception {

		List<State> statesList = new ArrayList<>();
		ExpressionUtils ex = new ExpressionUtils(expression);
		String satType = CtlFormulaUtils.getFormulaType(ex, this.kripkeStructure);
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


}
