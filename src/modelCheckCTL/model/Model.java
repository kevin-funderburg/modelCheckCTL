package modelCheckCTL.model;

import java.util.List;

public class Model {

	private String expression;
	private State state;
	private KripkeStructure kripkeStructure;
	
	public Model(String kripkeString) throws Exception {
		kripkeStructure = new KripkeStructure(kripkeString);
	}
	
	public void setState(String stateName) throws Exception {
		state = new State(stateName);
		if(!kripkeStructure.stateList.contains(state))
			throw new Exception("Invalid state selected");
	}
	
	public boolean verifyFormula() throws Exception {
		CtlFormula verifier = new CtlFormula(expression, state, kripkeStructure);
		List<State> states = verifier.sat(verifier.expression);
		return states.contains(state);
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public State getState() {
		return state;
	}
	
	public KripkeStructure getKripkeModel() {
		return kripkeStructure;
	}
	public void setKripkeModel(KripkeStructure kripkeStructure) {
		this.kripkeStructure = kripkeStructure;
	}

	
}