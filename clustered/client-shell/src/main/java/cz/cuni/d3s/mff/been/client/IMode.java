package cz.cuni.d3s.mff.been.client;

import jline.console.ConsoleReader;

/**
 * @author Martin Sixta
 */
interface IMode {

	public String getPrompt();
	public String[] getActions();
	public void setup(ConsoleReader reader);
	public IMode takeAction(String[] args);
}
