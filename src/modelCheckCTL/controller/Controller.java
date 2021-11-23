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
		throws IOException {

		// Enter data using BufferReader
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));

//		Controller control = new Controller();
		System.out.println("Welcome to our Model Check CTL!\n\n" +
				"Available Kripke structures: model1, model2, model3, model4");

		System.out.print("Enter the desired model: ");
		String modelName = reader.readLine();

		System.out.print("Enter the CTL formula: ");
		String ctlFormula = reader.readLine();
		System.out.println("you entered: " + ctlFormula);

		Path path = Path.of(modelName + ".kripke");
		String kripkeDef = Files.readString(path);

		KripkeStructure kripkeStructure = new KripkeStructure(kripkeDef);
	}
}
