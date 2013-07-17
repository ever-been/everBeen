package cz.cuni.mff.d3s.been.repository;

/**
 * A generic poll action.
 */
public interface Poll<T> {
    T perform();
}
