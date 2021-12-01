package modelCheckCTL.controller;
import modelCheckCTL.model.KripkeStructure;
import modelCheckCTL.model.State;
import modelCheckCTL.model.CtlFormula;

import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


public class Controller {
	
	public static void main(String[] args) throws Exception {

		// Enter data using BufferReader
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));

		String[] filenames = {"1", "2", "3", "4", "5", "6", "7", "Microwave"};
		System.out.println("Welcome to our Model Check CTL!\n\n");
		System.out.print("Available Kripke structures: ");
		for (String fn : filenames)
			System.out.print(fn + " ");

		System.out.print("\n");

		System.out.print("Enter the desired model: ");
		String model = reader.readLine();

		boolean validName = false;
		for (String fn : filenames)
		{
			if (fn.equals(model))
				validName = true;
		}

		if (!validName)
			throw new Exception("invalid file name");

		String filename;
		if (model.equals("Microwave"))
			filename = "Microwave Model";
		else
			filename = "Model " + model;

		System.out.print("Enter the CTL formula: ");
		String ctlExpression = reader.readLine();

		Path path = Path.of("Test Files/" + filename  + ".txt");
		String kripkeDef = Files.readString(path);

		KripkeStructure kripkeStructure = new KripkeStructure(kripkeDef);
		System.out.println(kripkeStructure);

		CtlFormula ctlFormula = new CtlFormula(ctlExpression, kripkeStructure);
		List<State> satisfiedStates = ctlFormula.SAT(ctlFormula.expression);

		System.out.println("--------------------------");
		System.out.println("\t" + ctlExpression);
		System.out.println("--------------------------");
		for (State state : kripkeStructure.stateList)
			System.out.println(state.stateName + ": " + satisfiedStates.contains(state));
	}
}
