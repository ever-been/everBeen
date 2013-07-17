package cz.cuni.mff.d3s.been.repository;

/**
 * A generic take action.
 */
public interface Take<T> {
    public T perform() throws InterruptedException;
}
