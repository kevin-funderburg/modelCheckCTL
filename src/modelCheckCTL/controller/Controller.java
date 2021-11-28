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

		System.out.println("Welcome to our Model Check CTL!\n\n" +
				"Available Kripke structures: 1, 2, 3, 4, 5, 6, 7, Microwave");

		System.out.print("Enter the desired model: ");
		String model = reader.readLine();

		System.out.print("Enter the CTL formula: ");
		String ctlExpression = reader.readLine();
		System.out.println("you entered: " + ctlExpression);

		String filename;
		if (model.equals("Microwave"))
			 filename = "Microwave Model";
		else
			filename = "Model " + model;

		Path path = Path.of("Test Files/" + filename  + ".txt");
		String kripkeDef = Files.readString(path);

		KripkeStructure kripkeStructure = new KripkeStructure(kripkeDef);
		kripkeStructure.printStructure();
		CtlFormula ctlFormula = new CtlFormula(ctlExpression, kripkeStructure.states.get(1), kripkeStructure);
		ctlFormula.satisfies();
	}
}
