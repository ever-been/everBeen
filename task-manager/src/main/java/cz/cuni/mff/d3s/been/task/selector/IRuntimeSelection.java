package cz.cuni.mff.d3s.been.task.selector;

/**
 * 
 * Interface for different strategies of Host Runtime selection.
 * 
 * @author Martin Sixta
 */
public interface IRuntimeSelection {
	/**
	 * 
	 * Function which will be called to find an appropriate Host Runtime fot a
	 * task.
	 * 
	 * @return ID of a host to run a task on.
	 * 
	 * @throws NoRuntimeFoundException
	 *           if no suitable Host Runtime is found.
	 */
	String select() throws NoRuntimeFoundException;
}
