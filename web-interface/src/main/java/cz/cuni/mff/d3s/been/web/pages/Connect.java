package cz.cuni.mff.d3s.been.web.pages;

import java.net.InetSocketAddress;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import cz.cuni.mff.d3s.been.web.services.BeenApiService;

/**
 * User: donarus Date: 4/27/13 Time: 11:29 AM
 */
public class Connect {

	@Inject
	private BeenApiService api;

	@Persist
	@Property
	private ConnectionProperties connectionProperties;

	@Component
	private BeanEditForm connectForm;

	@InjectComponent
	private Zone connectFormZone;

	void onValidateFromConnectForm() {
		try {
            api.connect(connectionProperties.toInetSocketAddress());
		} catch (Exception e) {
			connectForm.recordError(e.getMessage());
		}
	}

	Object onSuccess() {
		return Index.class;
	}

	Object onFailure() {
		return connectFormZone.getBody();
	}

	public static final class ConnectionProperties {
		public String hostname;
		public int port;

		public InetSocketAddress toInetSocketAddress() throws IllegalArgumentException {
			return new InetSocketAddress(hostname, port);
		}
	}
}
