package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The status of a file upload attempt.
 *
 * @author darklight
 */
public class UploadStatus implements ProtocolObject {
	@JsonProperty("uploaded")
	private final boolean uploaded;

	@JsonCreator
	public UploadStatus(@JsonProperty("uploaded") boolean uploaded) {
		this.uploaded = uploaded;
	}

	/**
	 * Create an OK upload response
	 * @return The upload status corresponding to 'OK'
	 */
	public static UploadStatus ok() {
		return new UploadStatus(true);
	}

	/**
	 * Create a not-OK upload response
	 * @return The upload status corresponding to 'failed'
	 */
	public static UploadStatus fail() {
		return new UploadStatus(false);
	}

	/**
	 * Whether the upload outcome was a success or a fail
	 * @return <code>true</code> when upload succeeded; <code>false</code> otherwise
	 */
	public boolean isUploaded() {
		return uploaded;
	}
}
