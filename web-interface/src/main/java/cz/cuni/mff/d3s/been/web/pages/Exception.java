package cz.cuni.mff.d3s.been.web.pages;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.ClusterConnectionUnavailableException;
import cz.cuni.mff.d3s.been.api.SoftwareRepositoryUnavailableException;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ExceptionReporter;

/**
 * @author Kuba Brecka
 */
public class Exception extends Page implements ExceptionReporter {

    @Property
    private String message;

    @Property
    private Block correctExceptionBlock;

    @Property
    private String originalMessage;

    @Property
    private Throwable exception;

    @Property
    private Throwable originalException;

    @Inject
    private Block softwareRepositoryUnavailableExceptionBlock;

    @Inject
    private Block hazelcastDisconnectedExceptionBlock;

    @Inject
    private Block unknownExceptionBlock;

    @Override
    public void reportException(Throwable exception) {

        this.originalException = exception;
        this.originalMessage = exception.getMessage();

        this.exception = exception;
        this.message = "An unexpected exception has occurred";

        this.correctExceptionBlock = unknownExceptionBlock;

        Throwable t = exception;
        while (t != null) {

            if (BeenApiException.class.isAssignableFrom(t.getClass())) {
                handleBeenApiException(t);
            }

            t = t.getCause();
        }

        log.error("Exception in web interface.", exception);
    }

    private void handleBeenApiException(Throwable t) {

        this.message = t.getMessage();
        this.exception = t;

        if (t instanceof ClusterConnectionUnavailableException) {
            this.correctExceptionBlock = hazelcastDisconnectedExceptionBlock;
            api.disconnect();
        } else if (t instanceof SoftwareRepositoryUnavailableException) {
            this.correctExceptionBlock = softwareRepositoryUnavailableExceptionBlock;
        }



    }
}
