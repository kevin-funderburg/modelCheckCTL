package modelCheckCTL.util;

public class Transition {

    public String name;
    public State FromState;
    public State ToState;

    public Transition(State fromState, State toState)
    {
        name = "";
        FromState = fromState;
        ToState = toState;
    }


    public Transition(String TransitionName, State fromState, State toState)
    {
        name = TransitionName;
        FromState = fromState;
        ToState = toState;
    }


    public boolean isEqual(Transition other)
    {
        return FromState.isEqual(other.FromState) && ToState.isEqual(other.ToState);
    }
}
