package cz.cuni.mff.d3s.been.task.selector;

/**
 * @author Martin Sixta
 */
public interface IRuntimeSelection {
	String select() throws NoRuntimeFoundException;
}
