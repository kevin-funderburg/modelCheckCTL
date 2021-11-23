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
        String[] transitionDefs = items[1].replace("\n", "").split(",");
        String[] atomDefs = items[2].replace("\n", "").split(",");

        for (String stateName : stateNames)
        {
            State state = new State(stateName);
            if (!states.contains(state))
                states.add(state);
            else
                System.out.println("State " + stateName + " already defined.");
        }

        for (String trans : transitionDefs)
        {
            String[] transItems = trans.split(" : ");

            String transName = transItems[0];
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
