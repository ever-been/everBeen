package cz.cuni.mff.d3s.been.evaluators;

import cz.cuni.mff.d3s.been.results.Result;

import java.util.UUID;

/**
 * @author Kuba Brecka
 */
public class EvaluatorResult extends Result {
	private String id;
	private String benchmarkId;
	private long timestamp;
	private String filename;
	private String mimeType;
	private byte[] data;

	public static final String MIME_TYPE_IMAGE_PNG = "image/png";
	public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_PLAIN = "text/plain";
	public static final String MIME_TYPE_ZIP = "application/zip";

	public EvaluatorResult() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getBenchmarkId() {
		return benchmarkId;
	}

	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
}
