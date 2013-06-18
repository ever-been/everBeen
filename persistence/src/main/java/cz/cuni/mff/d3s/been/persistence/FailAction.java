package cz.cuni.mff.d3s.been.persistence;

/**
 * A generic put action.
 */
public interface FailAction<T> {
    void perform(T on) throws InterruptedException;
}
