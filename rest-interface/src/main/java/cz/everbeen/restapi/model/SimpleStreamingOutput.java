package cz.everbeen.restapi.model;

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
public class SimpleStreamingOutput implements StreamingOutput {

	public static final MediaType DFLT_MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM_TYPE;

	private final InputStream data;
	private final MediaType mediaType;

	public SimpleStreamingOutput(InputStream data) {
		this.data = data;
		this.mediaType = DFLT_MEDIA_TYPE;
	}

	public SimpleStreamingOutput(InputStream data, MediaType mediaType) {
		this.data = data;
		this.mediaType = mediaType;
	}

	@Override
	public void write(OutputStream output) throws IOException, WebApplicationException {
		IOUtils.copy(data, output);
	}

	public MediaType getMediaType() {
		return mediaType;
	}
}
