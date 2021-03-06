package eu.printingin3d.legobrick;

import java.io.File;
import java.io.IOException;

import eu.printingin3d.javascad.exceptions.IllegalValueException;
import eu.printingin3d.javascad.utils.SaveScadFiles;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalValueException 
	 */
	public static void main(String[] args) throws IllegalValueException, IOException {
		LegoBrick lego = new LegoBrick(10, 2);
/*		Abstract3dModel lego = new GenericLegoBrick()
				.addRow("111")
				.addRow("111")
				.addRow("101")
				.addRow("111")
				.addRow("010")
				.build();*/
		new SaveScadFiles(new File("C:/temp")).
				addModel("lego_brick.scad", lego).
				saveScadFiles();
	}
}
