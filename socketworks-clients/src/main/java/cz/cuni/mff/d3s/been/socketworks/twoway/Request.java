package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Two-way communication request
 *
 * @author Martin Sixta
 */
public class Request {
	protected String selector;
	protected String value;
	protected long timeout;

	/**
	 * Create an empty request
	 */
	public Request() {}

	/**
	 * Create a get request
	 * @param selector Targeted entity selector
	 */
	public Request(String selector) {
		this.selector = selector;
	}

	/**
	 * Create a value-setting request
	 *
	 * @param selector Targeted entity selector
	 * @param value Value to set
	 */
	public Request(String selector, String value) {
		this.selector = selector;
		this.value = value;
	}

	/**
	 * Create a get request subject to timeout
	 *
	 * @param selector Targeted entity selector
	 * @param timeout Timeout in milliseconds
	 */
	public Request(String selector, long timeout) {
		this.selector = selector;
		this.timeout = timeout;
	}

	/**
	 * Create a set request subject to timeout
	 *
	 * @param selector Targeted entity selector
	 * @param value Value to set
	 * @param timeout Timeout in milliseconds
	 */
	public Request(String selector, String value, long timeout) {
		this.selector = selector;
		this.value = value;
		this.timeout = timeout;
	}

	/**
	 * Serialize the request to JSON
	 *
	 * @return JSON representation of the request
	 */
	public String toJson() {
		try {
			return JSONUtils.newInstance().serialize(this);
		} catch (JsonException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserialize the request from JSON
	 *
	 * @param json JSON to interpret
	 *
	 * @return The Request
	 *
	 * @throws JsonException When provided JSON is not a request
	 */
	public static Request fromJson(String json) throws JsonException {
		return JSONUtils.newInstance().deserialize(json, Request.class);
	}

	/**
	 * Get the entity targeting selector of this request
	 *
	 * @return The selector
	 */
	public String getSelector() {
		return selector;
	}

	/**
	 * Set the entity targeting selector to this request
	 *
	 * @param selector Selector to set
	 */
	public void setSelector(String selector) {
		this.selector = selector;
	}

	/**
	 * Get the value carried by this request
	 *
	 * @return The value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value carried by this request
	 *
	 * @param value Value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the timeout this request is subject to
	 *
	 * @return The timeout in milliseconds
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Set the timeout this request will be subject to
	 *
	 * @param timeout The timeout, in milliseconds
	 */
	public void setTimeout(long timeout) {
		if (timeout <= 0) {
			this.timeout = 0;
		} else {
			this.timeout = timeout;
		}
	}
}
