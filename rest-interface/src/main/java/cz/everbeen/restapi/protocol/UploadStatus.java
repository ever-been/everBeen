package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The status of a file upload attempt.
 *
 * @author darklight
 */
public class UploadStatus implements ProtocolObject {
	@JsonProperty("uploaded")
	private final boolean uploaded;

	public UploadStatus(@JsonProperty("uploaded") boolean uploaded) {
		this.uploaded = uploaded;
	}

	/**
	 * Whether the upload outcome was a success or a fail
	 * @return <code>true</code> when upload succeeded; <code>false</code> otherwise
	 */
	public boolean isUploaded() {
		return uploaded;
	}
}
