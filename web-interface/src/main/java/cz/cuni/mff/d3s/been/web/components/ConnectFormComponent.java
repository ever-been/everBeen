package cz.cuni.mff.d3s.been.web.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: donarus
 * Date: 4/29/13
 * Time: 11:43 AM
 */
public class ConnectFormComponent extends Component {

    @Parameter(required = true)
    private Class<?> successPage;


    @Persist
    @Property
    private ConnectionProperties connectionProperties;

    @org.apache.tapestry5.annotations.Component
    private BeanEditForm connectForm;

    @InjectComponent
    private Zone connectFormZone;

    @Inject
    private Block connectingBlock;


    @OnEvent(EventConstants.PROGRESSIVE_DISPLAY)
    public Object returnBlock() throws InterruptedException {
        try {
            api.connect(connectionProperties.hostname, connectionProperties.port, connectionProperties.groupName, connectionProperties.groupPassword);
        } catch (Exception e) {
            alertManager.alert(Duration.TRANSIENT, Severity.WARN, e.getMessage());
            return connectFormZone.getBody();
        }
        return successPage;
    }

    Object onSubmitFromConnectForm() {
        return connectingBlock;
    }

    public static class ConnectionProperties {
        public String hostname = "localhost";
        public int port = 5701;
        public String groupName = "dev";
        public String groupPassword = "dev-pass";
    }

}
