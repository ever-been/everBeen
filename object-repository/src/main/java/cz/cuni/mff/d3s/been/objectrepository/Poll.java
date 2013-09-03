package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic poll action.
 *
 * @param <T> Type of items polled
 */
public interface Poll<T> {

	/**
	 * Perform the poll action
	 *
	 * @return The polled object (or <code>null</code> if there was none)
	 */
    T perform();
}
