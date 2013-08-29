package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic failure action.
 */
public interface FailAction<T> {
    void perform(T on);
}
