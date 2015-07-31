package cz.cuni.mff.d3s.been.evaluators;

import cz.cuni.mff.d3s.been.results.DataSetResult;
import cz.cuni.mff.d3s.been.results.Result;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.UUID;

/**
 * A result of an evaluation on a set of results. Carries a byte array, which is interpreted based on the mime type which this result declares.
 *
 * @author Kuba Brecka
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
		@JsonSubTypes.Type(value = DataSetResult.class),
})
public class EvaluatorResult extends Result {
	private String id;
	private String benchmarkId;
	private long timestamp;
	private String filename;
	private String mimeType;
	protected byte[] data;

	public static final String MIME_TYPE_IMAGE_PNG = "image/png";
	public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_PLAIN = "text/plain";
	public static final String MIME_TYPE_ZIP = "application/zip";

	public static final String MIME_TYPE_XML = "application/xml";
	public static final String MIME_TYPE_JSON = "application/json";
	public static final String MIME_TYPE_CSV = "text/csv";

	/**
	 * Create an empty evaluator result
	 */
	public EvaluatorResult() {
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Get the evaluator result's unique ID
	 * @return The ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the evaluator's unique ID.
	 *
	 * The ID is automatically generated when the evaluator result is created, so you don't need to do this manually. We advise against using this method, it is visible only for serialization/deserialization purposes.
	 *
	 * @param id ID to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the timestamp associated with this result
	 *
	 * @return The timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set the timestamp for this result
	 *
	 * @param timestamp Timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Get the file name of carried data
	 *
	 * @return The file name
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Set the name of carried file
	 *
	 * @param filename Name to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Get the mime type of carried file
	 *
	 * @return The MIME type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Set the MIME type for carried file
	 *
	 * @param mimeType MIME type to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Get the content of carried file
	 *
	 * @return The data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Set carried content
	 *
	 * @param data Data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
}
