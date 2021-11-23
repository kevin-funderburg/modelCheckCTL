package modelCheckCTL.util;

import java.util.*;

public class KripkeStructure {

    List<Transition> transitions = new LinkedList<Transition>();
    List<State> states = new LinkedList<State>();
    List<String> atoms = new LinkedList<String>();

    public KripkeStructure(String kripkeDef)
    {
        String[] items=kripkeDef.split(";");

        String[] stateNames = items[0].split(",");
        String[] transDefs = items[1].replace("\n", "").split(",");
        String[] atomDefs = items[2].replace("\n", "").split(",");

        parseStates(stateNames);
        parseTrans(transDefs);
        parseAtoms(atomDefs);
    }

    public void parseStates(String[] stateNames)
    {
        for (String stateName : stateNames)
        {
            State state = new State(stateName);
            if (!states.contains(state))
                states.add(state);
            else
                System.out.println("State " + stateName + " already defined.");
        }
    }

    /**
     * this should receive a list of transition definitions with the following format:
     * {transition name}: {from state} - {to state}
     * @param transDefs
     */
    public void parseTrans(String[] transDefs)
    {
        for (String trans : transDefs)
        {
            String[] transItems = trans.split(" : ");

            String transName = transItems[0];
            String[] FromTos = trans.split(" - ");

            String fromStateName = FromTos[0];
            String toStateName = FromTos[1];
            State fromState = findStateByName(fromStateName);
            State toState = findStateByName(toStateName);

            Transition t = new Transition(transName, fromState, toState);
            if (!transitions.contains(t))
                transitions.add(t);
        }
    }

    public void parseAtoms(String[] atomDefs)
    {
        for (String atomDef : atomDefs)
        {
            String[] atomItems = atomDef.split(" : ");

            String stateName = atomItems[0];
        }
    }

    public State findStateByName(String stateName)
    {

        return null;
    }

    public String statesToString()
    {
        return "null";
    }

    public String transitionsToString ()
    {
        return "null";
    }
}
