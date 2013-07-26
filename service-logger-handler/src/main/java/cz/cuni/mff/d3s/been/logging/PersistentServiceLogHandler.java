package cz.cuni.mff.d3s.been.logging;

import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Implementation that submits log messages to the cluster to make them visible from user interfaces
 *
 * @author darklight
 */
public class PersistentServiceLogHandler implements ServiceLogHandler {

    static final ServiceLogPersister persister = new ServiceLogPersister();

    @Override
    public void log(LogMessage msg) throws JsonException {
        persister.log(msg);
    }
}
