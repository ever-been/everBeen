package cz.cuni.mff.d3s.been.persistence;

/**
 * A generic poll action.
 */
public interface Poll<T> {
    T perform();
}
