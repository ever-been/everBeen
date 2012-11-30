package cz.cuni.mff.d3s.been.cluster.cshell;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Martin Sixta
 */
class Mode {
	protected String prompt;
	protected String[] actions;
	protected ConsoleReader reader;
	protected PrintWriter out;


	public String getPrompt() {
		return prompt;
	}

	public String[] getActions() {
		return actions;
	}

	protected Mode(ConsoleReader reader, String prompt, String[] actions) {
		this.prompt = prompt;
		this.actions = actions;

		setup(reader);

	}
	protected void setup(ConsoleReader reader) {
		this.reader = reader;

		out = new PrintWriter(reader.getOutput());

		for (Completer completer: reader.getCompleters()) {
			reader.removeCompleter(completer);
		}


		List<Completer> completors = new LinkedList<Completer>();
		completors.add(new StringsCompleter(actions));


		for (Completer c : completors) {
			reader.addCompleter(c);
		}

		reader.setPrompt(prompt);
	}

	protected Mode takeAction(String[] args) {
		for(String arg: args) {
		   out.println(arg);
		}

		return this;
	}
}
