package micropolisj.engine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import micropolisj.engine.tool.MicropolisTool;
import micropolisj.engine.tool.ToolStroke;

public class TestMicropolis {

	public void testMicropolisIntIntRandom() throws IOException {
		Micropolis city = new Micropolis(120, 100, new Random(100));
		File fIn = new File("C:\\Users\\markus\\git\\micropolis\\micropolis-java\\test-resources\\testIn.cty");
//		File fOut = new File("C:\\Users\\markus\\git\\micropolis\\micropolis-java\\test-resources\\testOut3.cty");
		File fOutOrig = new File(
				"C:\\Users\\markus\\git\\micropolis\\micropolis-java\\test-resources\\testOutOrig.cty");
		city.load(fIn);
		
		// city.budget.totalFunds = 10000;
		System.out.println("budget: "+city.budget.totalFunds);
		System.out.println(Arrays.toString(city.history.ind));
//		System.out.println(city.budget.totalFunds);
//		System.out.println(city.getTile(19, 19));
		ToolStroke ts = MicropolisTool.RESIDENTIAL.beginStroke(city, 20, 20);
		ts.apply();
		for (int i = 0; i < 50; i++) {
			city.animate();
		}
		
		//city.save(fOutOrig);

		System.out.println(Arrays.toString(city.history.ind));
		System.out.println("budget after animate(): "+city.budget.totalFunds);
		Micropolis expectedCity=new Micropolis(120,100,new Random(100));
		expectedCity.load(fOutOrig);
		System.out.println(expectedCity.equals(city));
		//		city.save(fOut);

//		System.out.println(compareFiles(fOut, fOutOrig));
	}
	
	
	

	public boolean compareFiles(File file1, File file2) throws IOException {
		System.out.println("File length " + file1.length());
		if (file1.length() != file2.length()) {
			return false;
		}

		try (InputStream in1 = new BufferedInputStream(new FileInputStream(file1));
				InputStream in2 = new BufferedInputStream(new FileInputStream(file2));) {

			int value1, value2, counter=0;
			do {
				counter++;
				// since we're buffered read() isn't expensive
				value1 = in1.read();
				value2 = in2.read();
				if (value1 != value2) {
					System.out.println("difference at byte: " + counter);
					return false;
				}
			} while (value1 >= 0);

			// since we already checked that the file sizes are equal
			// if we're here we reached the end of both files without a mismatch
			return true;
		}
	}

}
