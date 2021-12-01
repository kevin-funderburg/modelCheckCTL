package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

public class KripkeStructure {

	public ArrayList<Transition> transList = new ArrayList<>();
	public ArrayList<State> stateList = new ArrayList<>();
	public ArrayList<String> atomsList = new ArrayList<>();

	public KripkeStructure(String definition) throws Exception {

		try {
			String[] parsedDefs = definition.replaceAll("\n", "")
					.replaceAll("\t", "").split(";");
			if (parsedDefs.length < 3)
				throw new Exception("Invalid model description.");
			String[] states = parsedDefs[0].replaceAll(" ", "").split(",");
			String[] transitions = parsedDefs[1].replaceAll(" ", "").split(",");
			String[] atoms = parsedDefs[2].split(",");

			parseStates(states);
			parseTransitions(transitions);
			parseAtoms(atoms);

		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

	}

	private void parseStates(String[] states) throws Exception {

		for (String state : states)
		{
			State st = new State();
			st.stateName = state.replaceAll("[^a-zA-Z0-9]", "");

			if (!stateList.contains(st)) {
				stateList.add(st);
			} else
				throw new Exception("State " + state + " already defined");
		}

	}

	private void parseTransitions(String[] transitions) throws Exception {

		for (String transition : transitions)
		{
			String[] parsedTrans = transition.split(":");
			if (parsedTrans == null || parsedTrans.length != 2)
				throw new Exception("Invalid transition definition");

			String transitionName = parsedTrans[0];
			String[] fromToStates = parsedTrans[1].split("-");
			if (fromToStates.length != 2)
				throw new Exception("Invalid from state and to state description for transition : " + transitionName);
			if(!stateList.contains(new State(fromToStates[0])))
				throw new Exception("Invalid from state : "+ fromToStates[0] + " defined for transition : " + transitionName);
			if(!stateList.contains(new State(fromToStates[1])))
				throw new Exception("Invalid to state : "+ fromToStates[1] + " defined for transition : " + transitionName);

			State fromState = stateList.stream().filter(x -> x.stateName.equals(fromToStates[0])).findFirst().get();
			State toState = stateList.stream().filter(x -> x.stateName.equals(fromToStates[1])).findFirst().get();

			if (fromState == null || toState == null)
				throw new Exception("Invalid transition definition for : " + transitionName);

			Transition trans = new Transition(fromState, toState, transitionName);
			if (!transList.contains(trans))
				transList.add(trans);
			else
				throw new Exception("Transition " + transitionName + " already defined");

		}

	}

	private void parseAtoms(String[] atoms) throws Exception {

		for (String atom : atoms)
		{
			String[] parsedAtom = atom.split(":");
			if (parsedAtom == null || parsedAtom.length != 2)
				throw new Exception("Invalid atoms definition");

			String stateName = parsedAtom[0].trim();
			String[] atomList = parsedAtom[1].trim().split(" ");

			List<String> stateAtoms = new ArrayList<>();

			for (String a : atomList)
			{
				if (!a.isEmpty()) {
					if (!stateAtoms.contains(a))
						stateAtoms.add(a);
					else
						throw new Exception("Atoms repeated for state : " + stateName);
					if (!atomsList.contains(a))
						atomsList.add(a);
				}
				if(!stateList.contains(new State(stateName)))
					throw new Exception("Invalid state : " + stateName + " in atom labels");

				State state = stateList.stream().filter(x -> x.stateName.equals(stateName)).findFirst().get();
				state.atomsList = stateAtoms;
			}
		}

	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("--------------------------\n");
		sb.append("\t\tAtoms\n");
		sb.append("--------------------------\n");
		sb.append(this.atomsList);
		sb.append("\n\n--------------------------\n");
		sb.append("\tStates with labels\n");
		sb.append("--------------------------\n");
		this.stateList.forEach(x -> sb.append(x.stateName).append(x.atomsList).append("\n"));
		sb.append("\n");
		sb.append("--------------------------\n");
		sb.append("\tTransitions\n");
		sb.append("--------------------------\n");
		this.transList.forEach(x -> sb.append(x.transitionName).append("(").append(x.fromState.stateName).append("-->")
				.append(x.toState.stateName).append(")\n"));

		return sb.toString();
	}

}
