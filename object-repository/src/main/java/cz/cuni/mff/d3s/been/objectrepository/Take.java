package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A generic take action.
 */
public interface Take<T> {
    public T perform() throws InterruptedException;
}
