package cz.everbeen.restapi.model;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import org.apache.commons.io.IOUtils;
import org.apache.http.annotation.Immutable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A binary BPK response
 *
 * @author darklight
 */
@Immutable
public class BPKStreamingOutput implements StreamingOutput {

	public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM_TYPE;

	private final InputStream bpk;

	public BPKStreamingOutput(InputStream bpk) {
		this.bpk = bpk;
	}

	@Override
	public void write(OutputStream output) throws IOException, WebApplicationException {
		IOUtils.copy(bpk, output);
	}
}
