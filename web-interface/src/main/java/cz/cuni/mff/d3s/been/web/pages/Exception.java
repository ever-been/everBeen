package cz.cuni.mff.d3s.been.web.pages;

import com.hazelcast.client.NoMemberAvailableException;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

/**
 * @author Kuba Brecka
 */
public class Exception extends Page implements ExceptionReporter {

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String message;

	@Property
	private Throwable exception;

	@Override
	public void reportException(Throwable exception) {
		this.exception = exception;
		this.message = exception.getMessage();

		Throwable t = exception;
		while (t != null) {
			if (t instanceof RuntimeException &&
					t.getMessage().equals("HazelcastClient is no longer active.")) {
				log.error(t.getMessage(), t);
				api.disconnect();
				break;
			}

			if (t instanceof NoMemberAvailableException &&
					t.getMessage().equals("No cluster member available to connect")) {
				log.error(t.getMessage(), t);
				api.disconnect();
				break;
			}

			t = t.getCause();
		}

		log.error("Exception in web interface.", exception);
	}
}
