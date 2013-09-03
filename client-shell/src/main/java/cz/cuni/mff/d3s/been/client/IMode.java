package cz.cuni.mff.d3s.been.client;

import jline.console.ConsoleReader;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.persistence.DAOException;

/**
 * @author Martin Sixta
 * 
 *         WARNING: this code is in incubator phase
 * 
 */
interface IMode {

	public String getPrompt();
	public String[] getActions();
	public void setup(ConsoleReader reader);
	public IMode takeAction(String[] args) throws DAOException, BeenApiException;
}
