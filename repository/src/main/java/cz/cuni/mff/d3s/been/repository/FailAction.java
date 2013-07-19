package cz.cuni.mff.d3s.been.repository;

/**
 * A generic put action.
 */
public interface FailAction<T> {
    void perform(T on) throws InterruptedException;
}
