package cz.cuni.mff.d3s.been.persistence;

/**
 * An action that specifies what should be done with an item once it's decided
 * that it should be persisted.
 * 
 * @author darklight
 * 
 * @param <T> Type of handled object
 */
public interface SuccessAction<T> {
	void perform(T what) throws Exception;
}
