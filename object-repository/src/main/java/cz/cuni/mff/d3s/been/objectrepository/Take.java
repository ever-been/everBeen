package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic take action.
 *
 * @param <T> Type of the item taken
 */
public interface Take<T> {

	/**
	 * Perform the take action
	 *
	 * @return The object taken
	 *
	 * @throws InterruptedException When interrupted while performing the take
	 */
    public T perform() throws InterruptedException;
}
