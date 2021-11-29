package modelCheckCTL.model;

import java.util.ArrayList;
import java.util.List;

public class State {

	public List<String> atomsList = new ArrayList<>();
	public String stateName;
	
	public State() {
		
	}
	
	public State(String stateName) {
		this.stateName = stateName;
	}
	
	@Override
	public boolean equals(Object state) {
		State ms = (State)state;
		return this.stateName.equals(ms.stateName);
	}

}