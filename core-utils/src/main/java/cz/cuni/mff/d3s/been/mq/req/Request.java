package cz.cuni.mff.d3s.been.mq.req;

import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * @author Martin Sixta
 */
public final class Request {
	private RequestType type;
	private String selector;
	private String value;
	private long timeout;

	public Request() {

	}

	public Request(RequestType type, String selector) {
		this.type = type;
		this.selector = selector;
	}

	public Request(RequestType type, String selector, String value) {
		this.type = type;
		this.selector = selector;
		this.value = value;
	}

	public Request(RequestType type, String selector, long timeout) {
		this.type = type;
		this.selector = selector;
		this.timeout = timeout;
	}

	public Request(RequestType type, String selector, String value, long timeout) {
		this.type = type;
		this.selector = selector;
		this.value = value;
		this.timeout = timeout;
	}

	public String toJson() {
		try {
			return JSONUtils.serialize(this);
		} catch (JSONUtils.JSONSerializerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Request fromJson(String json) throws JSONUtils.JSONSerializerException {
		return JSONUtils.deserialize(json, Request.class);
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		if (timeout < 0) {
			timeout = 0;
		}
		this.timeout = timeout;
	}
}
