package cz.cuni.mff.d3s.been.cluster.cshell.rest;

/**
 *
 * @author Martin Sixta
 */

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;




public class ClusterShell {





	public static void main(String[] args) {
		/*
		URIFactory factory = new URIFactory();

		Parser parser = new Parser(factory);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			System.exit(1);
		}


		try {
			Character mask = null;
			String trigger = null;

			ConsoleReader reader = new ConsoleReader();

			reader.setPrompt("been> ");


			List<Completer> completors = new LinkedList<Completer>();
			completors.add(new StringsCompleter("connect", "map", "queue"));


			for (Completer c : completors) {
				reader.addCompleter(c);
			}

			String line;
			PrintWriter out = new PrintWriter(reader.getOutput());

			out.println(factory.getHost() + ":" + factory.getPort());


			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");

				if (tokens.length > 0) {
					if (tokens[0].equals("connect")) {
						if (tokens.length == 3) {
							factory.setHost(tokens[1]);
							factory.setPort(Integer.valueOf(tokens[2]));
						}

					} else if (tokens[0].equalsIgnoreCase("map")) {
						MapCommand cmd = new MapCommand(factory, out);
						Parser mapParser = new Parser(cmd);

						try {
							mapParser.parse(tokens);
							cmd.execute();
						} catch (ParseException e) {
							mapParser.usage();
						}

					} else if (tokens[0].equalsIgnoreCase("queue")) {
						QueueCommand cmd = new QueueCommand(factory, out);
						Parser mapParser = new Parser(cmd);

						try {
							mapParser.parse(tokens);
							cmd.execute();
						} catch (ParseException e) {
							mapParser.usage();
						}
					}

				}

				out.flush();

				// If we input the special word then we will mask
				// the next line.
				if ((trigger != null) && (line.compareTo(trigger) == 0)) {
					line = reader.readLine("password> ", mask);
				}
				if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
					break;
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		*/
	}
}



