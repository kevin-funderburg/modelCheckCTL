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
        String[] transDefs = items[1].replace("\n", "").replace("\r", "").split(",");
        String[] atomDefs = items[2].replace("\n", "").replace("\r", "").replace("\t", "").split(",");

        parseStates(stateNames);
        parseTrans(transDefs);
        parseAtoms(atomDefs);
    }

    public void parseStates(String[] stateNames)
    {
        for (String stateName : stateNames)
        {
//            System.out.println("stateName: " + stateName);
            stateName = stateName.strip();

            //this is here to get around a bug that appeared where s1 kept getting a space before it
            if (stateName.contains("1"))
                stateName = "s1";

            State state = new State(stateName);
//            System.out.println("state.name: " + state.name);
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
                String[] FromTos = transItems[1].split(" - ");

                if (FromTos.length == 2) {
                    String fromStateName = FromTos[0].replace(" ", "");
                    String toStateName = FromTos[1].replace(" ", "");
                    State fromState = findStateByName(fromStateName);
                    State toState = findStateByName(toStateName);

                    Transition t = new Transition(transName, fromState, toState);
                    if (!transitions.contains(t))
                        transitions.add(t);
                }
            }
        }

    /**
     * this should receive a list of atomic definitions with the following format:
     * {state name}: {atom1}, {atom2}, etc
     * @param atomDefs
     */
    public void parseAtoms(String[] atomDefs)
    {
        for (String atomDef : atomDefs)
        {
            String[] atomItems = atomDef.split(" : ");

            String stateName = atomItems[0];

            if (atomItems.length == 2)
            {
                String[] atomNames = atomItems[1].split(" ");

                State s = findStateByName(stateName);
                if (s == null)
                    System.out.println("State " + stateName + " not defined");

                s.atoms.addAll(Arrays.asList(atomNames));

                for (String atomName : atomNames) {
                    if (!atoms.contains(atomName))
                        atoms.add(atomName);
                }
            }
        }
    }

    public State findStateByName(String stateName)
    {
        for (State state : states) {
            System.out.println("state.name = " + state.name);
            if (state.name.equals(stateName))
                return state;
        }
        return null;
    }

    public void out()
    {
        StringBuilder str = new StringBuilder();
        str.append("STATES\n");
        str.append("-----------\n");
        str.append(statesToString() + "\n");
        str.append("\n");
        str.append("TRANSITIONS\n");
        str.append("--------------------\n");
        str.append(transitionsToString());
        str.append("\n");

        System.out.println(str.toString());
    }

    public String statesToString()
    {
        String fullStateString = "";
        int i = 0;
        for (State state : states)
        {
            fullStateString += state.name;
            if (i < states.size()-1)
                fullStateString += ", ";
            i++;
        }
        return fullStateString;
    }

    public String transitionsToString()
    {
        String fulltstring = "";
        String tstring;
        int i = 0;
        for (Transition t : transitions)
        {
            tstring = "(" + t.FromState.name + ", " + t.ToState.name + ")";
            fulltstring += tstring;
            if (i < transitions.size()-1)
                fulltstring += ", ";
            i++;
        }
//        System.out.println("fulltstring: " + fulltstring);
        return fulltstring;
    }
}
