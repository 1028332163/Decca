package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * write result that calculated by jar-used.
 * 
 * @author asus
 *
 */
public class JarRiskWriter {
	public void write(String outPath, boolean append) throws Exception {
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
		
		printer.close();
	}

}
