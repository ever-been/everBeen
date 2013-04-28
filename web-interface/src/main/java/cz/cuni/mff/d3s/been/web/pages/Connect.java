package cz.cuni.mff.d3s.been.web.pages;

import java.net.InetSocketAddress;

import cz.cuni.mff.d3s.been.web.components.Layout;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Zone;

/**
 * User: donarus Date: 4/27/13 Time: 11:29 AM
 */

@Page.Navigation(section = Layout.Section.CONNECT)
public class Connect extends Page {

	@Persist
	@Property
	private ConnectionProperties connectionProperties;

	@Component
	private BeanEditForm connectForm;

	@InjectComponent
	private Zone connectFormZone;

    @Override
	Object onActivate() {
		return null;
	}

	void onValidateFromConnectForm() {
		try {
			api.connect(connectionProperties.hostname, connectionProperties.port, connectionProperties.groupName, connectionProperties.groupPassword);
		} catch (Exception e) {
			connectForm.recordError(e.getMessage());
		}
	}

	Object onSuccessFromConnectForm() {
		return Index.class;
	}

	Object onFailureFromConnectForm() {
		return connectFormZone.getBody();
	}

	public static final class ConnectionProperties {
		public String hostname = "localhost";
		public int port = 5701;
        public String groupName = "dev";
        public String groupPassword = "dev-pass";

	}
}
