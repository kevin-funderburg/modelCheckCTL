package modelCheckCTL.model;

public class Transition {

	String transitionName;
	State fromState;
	State toState;

	public Transition(State fromState, State toState, String transitionName) {
		this.transitionName = transitionName;
		this.fromState = fromState;
		this.toState = toState;
	}
	
	public Transition(State fromState, State toState) {
		this.transitionName = "";
		this.fromState = fromState;
		this.toState = toState;
	}

	@Override
	public boolean equals(Object o) {
		Transition transition = (Transition)o;
		return this.transitionName.equals(transition.transitionName)
				|| (this.fromState.equals(transition.fromState) && this.toState.equals(transition.toState));
	}
}