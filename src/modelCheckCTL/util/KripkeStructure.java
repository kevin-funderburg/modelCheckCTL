package modelCheckCTL.util;

import java.util.*;

public class KripkeStructure {

    List<Transition> transitions = new LinkedList<Transition>();
    List<State> states = new LinkedList<State>();
    List<String> atoms = new LinkedList<String>();

    public KripkeStructure(String kripkeDef)
    {
        String[] items=kripkeDef.split(";");
        for (String item : items )
        {
            System.out.println(item);
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
