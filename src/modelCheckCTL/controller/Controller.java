package modelCheckCTL.controller;
import modelCheckCTL.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import modelCheckCTL.model.Model;
import modelCheckCTL.view.View;

public class Controller {
	
	public static void main(String[] args)
			throws Exception {

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
		System.out.println("you entered: " + ctlExpression);

		Path path = Path.of("Test Files/" + filename  + ".txt");
		String kripkeDef = Files.readString(path);

		KripkeStructure kripkeStructure = new KripkeStructure(kripkeDef);
		kripkeStructure.printStructure();

		for (State state : kripkeStructure.states)
		{
			CtlFormula ctlFormula = new CtlFormula(ctlExpression, state, kripkeStructure);
			System.out.println(state.name + ": " + ctlFormula.satisfies());
		}

	}
}
