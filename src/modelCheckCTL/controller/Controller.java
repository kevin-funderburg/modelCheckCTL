package modelCheckCTL.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.File;
import java.io.InputStream;

import modelCheckCTL.model.Model;
import modelCheckCTL.view.View;

public class Controller {
	
//	private Model model;
//	private View view;
//
//	public Controller()
//	{
//		model = new Model(this);
//		view = new View(this, model);
//	}
//	public void setModel(Model m)
//	{
//		model = m;
//	}
//	public void setView(View v)
//	{
//		view = v;
//	}
	
	public static void main(String[] args)
		throws IOException {

//		Controller control = new Controller();
		System.out.println("Welcome to our Model Check CTL\n" +
				"Choose from the following Kripke structures: model1, model2, model3, model4:");

		// Enter data using BufferReader
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));

		String modelName = reader.readLine();
		System.out.println("you entered: " + modelName);

		System.out.print("Enter the CTL formula: ");
		String ctlFormula = reader.readLine();
		System.out.println("you entered: " + ctlFormula);

	}
}
