package cz.cuni.mff.d3s.been.web.pages.bpkpackage;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.services.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kuba Brecka
 */
public class Download extends Page {

	Object onActivate(String groupId, String bpkId, String version) throws BeenApiException {
		BpkIdentifier bpkIdentifier = constructBpkIdentifier(groupId, bpkId, version);
		String filename = String.format("%s-%s.bpk", bpkId, version);

		final InputStream contentStream = api.getApi().downloadBpk(bpkIdentifier);
		return createBpkResponse(filename, contentStream);
	}

	private BpkIdentifier constructBpkIdentifier(String groupId, String bpkId, String version) {
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setVersion(version);
		return bpkIdentifier;
	}

	private Object createBpkResponse(final String filename, final InputStream contentStream) {
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
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			}
		};
	}

}
