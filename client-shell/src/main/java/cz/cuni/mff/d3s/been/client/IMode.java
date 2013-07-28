package cz.cuni.mff.d3s.been.client;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import jline.console.ConsoleReader;

/**
 * @author Martin Sixta
 */
interface IMode {

	public String getPrompt();
	public String[] getActions();
	public void setup(ConsoleReader reader);
	public IMode takeAction(String[] args) throws DAOException, BeenApiException;
}
