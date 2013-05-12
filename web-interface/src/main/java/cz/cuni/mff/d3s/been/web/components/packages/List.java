package cz.cuni.mff.d3s.been.web.components.packages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

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

	Object onActionFromDownload(String groupId, String bpkId, String version) {
		BpkIdentifier bpkIdentifier = constructBpkIdentifier(
				groupId,
				bpkId,
				version);
		String filename = String.format("%s-%s.bpk", bpkId, version);
		try {
			final InputStream contentStream = api.getApi().downloadBpk(bpkIdentifier);
			return createBpkResponse(filename, contentStream);
		} catch (Exception e) {
			alertManager.alert(
					Duration.SINGLE,
					Severity.ERROR,
					"Can't download bpk package: " + e.getMessage());
		}
		reloadBpks();
		return packagesZone;
	}

	private Object createBpkResponse(final String filename,
			final InputStream contentStream) {
		return new StreamResponse() {
			@Override
			public String getContentType() {
				return "application/zip";
			}

			@Override
			public InputStream getStream() throws IOException {
				return contentStream;
			}

			@Override
			public void prepareResponse(Response response) {
				response.setHeader(
						"Content-Disposition",
						"attachment; filename=" + filename);
			}
		};
	}

	private BpkIdentifier constructBpkIdentifier(String groupId, String bpkId,
			String version) {
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setVersion(version);
		return bpkIdentifier;
	}

}
