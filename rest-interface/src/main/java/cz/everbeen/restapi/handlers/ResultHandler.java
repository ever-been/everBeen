package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.everbeen.restapi.BeenApiOperation;
import cz.everbeen.restapi.model.SimpleStreamingOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;

/**
 * EverBEEN REST API handler for
 * @author darklight
 */
@Path("/result")
public class ResultHandler extends Handler {

	@GET
	@Path("/{resultId}")
	public StreamingOutput downloadResult(@QueryParam("resultId") final String resultId) {
		return perform(new BeenApiOperation<StreamingOutput>() {
			@Override
			public String name() {
				return "downloadResult";
			}

			@Override
			public StreamingOutput perform(BeenApi beenApi) throws BeenApiException {
				return new SimpleStreamingOutput(
					new ByteArrayInputStream(beenApi.getEvaluatorResult(resultId).getData())
				);
			}

			@Override
			public StreamingOutput fallbackValue(Throwable error) {
				return null;
			}
		});
	}
}
