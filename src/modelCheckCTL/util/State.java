package modelCheckCTL.util;

import java.util.LinkedList;
import java.util.List;

public class State {

    String name;
    List<String> atoms;

    public State(String stateName)
    {
        name = stateName;
        atoms = new LinkedList<String>();
    }

    boolean isEqual(State other) { return name.equals(other.name); }
}
