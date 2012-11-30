package cz.cuni.mff.d3s.been.cluster.cshell;

/**
 *
 * @author Martin Sixta
 */

import java.io.IOException;
import java.io.PrintWriter;


import jline.console.ConsoleReader;


public class ClusterShell {


	public static void main(String[] args) throws IOException {
		try {

			ConsoleReader reader = new ConsoleReader();

			Mode mode = new DefaultMode(reader);

			mode.setup(reader);


			String line;
			PrintWriter out = new PrintWriter(reader.getOutput());



			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				String[] tokens = line.trim().split(" ");


				try {
					mode = mode.takeAction(tokens);
				} catch (IllegalArgumentException ex) {
					out.println(ex.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}

				out.flush();

			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}



