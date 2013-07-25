package cz.cuni.mff.d3s.been.web.pages.result;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kuba Brecka
 */
public class Download extends Page {

	Object onActivate(String resultId) {
		final EvaluatorResult result = this.api.getApi().getEvaluatorResult(resultId);
		final InputStream is = new ByteArrayInputStream(result.getData());

		return new StreamResponse() {
			@Override
			public String getContentType() {
				return result.getMimeType();
			}

			@Override
			public InputStream getStream() throws IOException {
				return is;
			}

			@Override
			public void prepareResponse(Response response) {
				response.setHeader("Content-Disposition", "attachment; filename=" + result.getFilename());
			}
		};
	}
}
