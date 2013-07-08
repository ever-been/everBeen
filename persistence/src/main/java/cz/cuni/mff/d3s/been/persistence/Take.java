package cz.cuni.mff.d3s.been.persistence;

/**
 * A generic take action.
 */
public interface Take<T> {
    public T perform() throws InterruptedException;
}
