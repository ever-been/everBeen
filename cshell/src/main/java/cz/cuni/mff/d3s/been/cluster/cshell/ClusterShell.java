package cz.cuni.mff.d3s.been.cluster.cshell;

/**
 *
 * @author Martin Sixta
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.d3s.been.cluster.Factory;
import cz.cuni.mff.d3s.been.cluster.Member;
import cz.cuni.mff.d3s.been.cluster.Message;
import cz.cuni.mff.d3s.been.cluster.Messaging;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


public class ClusterShell {


	public static void main(String[] args) throws IOException {
		try {
			Character mask = null;
			String trigger = null;
			boolean color = false;

			ConsoleReader reader = new ConsoleReader();

			reader.setPrompt("been> ");


			List<Completer> completors = new LinkedList<Completer>();
			completors.add(new StringsCompleter("connect", "disconnect", "send", "list"));

			color = true;



			for (Completer c : completors) {
				reader.addCompleter(c);
			}

			String line;
			PrintWriter out = new PrintWriter(reader.getOutput());




			Member member =  Factory.createMember("hazelcast", true);


			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");

				if (tokens.length > 0) {
					if (tokens[0].equals("connect")) {
						member.connect();
					} else if (tokens[0].equals("disconnect")) {
						member.disconnect();
					} else if (tokens[0].equals("send")) {
						Message message = new Message(0, tokens[1]);
						member.getMessaging().send("BEEN_HOSTRUNTIME_TOPIC", message);
					} else if (tokens[0].equals("list")) {
						if (tokens.length == 2) {
							Collection<String> list = member.getDataPersistence().list(tokens[1]);
							for (String str: list) {
								out.println(str);
							}
						} else {
							out.println("list [map|set|topic|list]");
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
	}
}



