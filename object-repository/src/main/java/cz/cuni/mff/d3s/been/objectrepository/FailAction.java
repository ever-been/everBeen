package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic failure action.
 *
 * @param <T> Type of the failed item
 */
public interface FailAction<T> {

	/**
	 * Perform the fail action on the item
	 *
	 * @param on Item to enact this fail action on
	 */
    void perform(T on);
}
