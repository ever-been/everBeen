package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

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

	public Reply(ReplyType replyType, String value) {
		this.replyType = replyType;
		this.value = value;
	}

	//TODO should throw exception
	public String toJson() {
		try {
			return JSONUtils.serialize(this);
		} catch (JSONUtils.JSONSerializerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Reply fromJson(String json) throws JSONUtils.JSONSerializerException {
		return JSONUtils.deserialize(json, Reply.class);
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

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
