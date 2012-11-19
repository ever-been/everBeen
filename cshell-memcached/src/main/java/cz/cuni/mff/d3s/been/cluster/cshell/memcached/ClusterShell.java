package cz.cuni.mff.d3s.been.cluster.cshell.memcached;

/**
 *
 * @author Martin Sixta
 */

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;


public class ClusterShell {


	public static void main(String[] args) throws IOException {
		try {
			Character mask = null;
			String trigger = null;

			ConsoleReader reader = new ConsoleReader();

			reader.setPrompt("been> ");


			List<Completer> completors = new LinkedList<Completer>();
			completors.add(new StringsCompleter("connect", "disconnect", "set", "get"));


			for (Completer c : completors) {
				reader.addCompleter(c);
			}

			MemcachedClient client = null;
			String line;
			PrintWriter out = new PrintWriter(reader.getOutput());


			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(" ");

				if (tokens.length > 0) {
					if (tokens[0].equals("connect")) {
						if (tokens.length != 3) {
							out.println("connect host port");
						} else {
							client = new MemcachedClient(new InetSocketAddress(tokens[1], Integer.valueOf(tokens[2])));
						}

					} else if (tokens[0].equals("disconnect")) {
						if (client != null) {
							client.shutdown();
						}
					} else if (tokens[0].equalsIgnoreCase("set")) {
						if (tokens.length == 3) {
							client.set(tokens[1], 0, tokens[2]);
						} else {
							out.println("set key value");
						}
					} else if (tokens[0].equalsIgnoreCase("get")) {
						if (tokens.length == 2) {
							out.println(client.get(tokens[1]));
						} else {
							out.println("get key");
						}

					} else {
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



