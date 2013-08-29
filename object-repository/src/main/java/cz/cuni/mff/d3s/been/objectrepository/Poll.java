package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic poll action.
 */
public interface Poll<T> {
    T perform();
}
