package cz.cuni.mff.d3s.been.web.components.packages;

import java.util.Collection;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.web.components.Component;

/**
 * User: donarus Date: 4/28/13 Time: 11:58 AM
 */
public class List extends Component {

	@Property
	private BpkIdentifier bpkIdentifier;

	@Inject
	private Block packagesBlock;

	@InjectComponent
	private Zone packagesZone;

	@Property
	private String message;

	@OnEvent(EventConstants.PROGRESSIVE_DISPLAY)
	public Object returnBlock() throws InterruptedException {
        reloadBpks();
		return packagesBlock;
	}

    private void reloadBpks() {
        this.bpkIdentifiers = this.api.getApi().getBpks();
    }

    @Property
	Collection<BpkIdentifier> bpkIdentifiers;

	Object onActionFromDelete(String groupId, String bpkId, String version) {
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setVersion(version);
		try {
			this.api.getApi().deleteBpk(bpkIdentifier);
		} catch (Exception e) {
			message = "Can't delete bpk package: " + e;
		}
        reloadBpks();
		return packagesZone;
	}

}
