package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * A generic reply to a socket {@link Request}
 * 
 * @author Martin Sixta
 */
public final class Reply {

	private ReplyType replyType;
	private String value;

	Reply() {

	}

	/**
	 * Create a reply
	 *
	 * @param replyType Type of the reply
	 * @param value Value this reply carries
	 */
	public Reply(ReplyType replyType, String value) {
		this.replyType = replyType;
		this.value = value;
	}


	/**
	 * Serialize this reply to JSON
	 *
	 * @return JSON representation of this reply
	 */
	public String toJson() {
		//TODO should throw exception
		try {
			return JSONUtils.newInstance().serialize(this);
		} catch (JsonException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deserialize a reply from JSON
	 *
	 * @param json JSON representation of the reply
	 *
	 * @return The reply
	 *
	 * @throws JsonException When provided JSON doesn't represent a reply
	 */
	public static Reply fromJson(String json) throws JsonException {
		return JSONUtils.newInstance().deserialize(json, Reply.class);
	}

	/**
	 * Get the type of this reply
	 *
	 * @return The typ
	 */
	public ReplyType getReplyType() {
		return replyType;
	}

	/**
	 * Set the type of this reply
	 *
	 * @param replyType Type to set
	 */
	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

	/**
	 * Get the value carried by this reply
	 *
	 * @return The value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Reply)) {
			return false;
		}

		final Reply other = (Reply) obj;

		return ((value == null) ? other.value == null : value.equals(other.value)) || ((replyType == null)
				? other.replyType == null : replyType.equals(other.replyType));
	}
}
