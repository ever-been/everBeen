package cz.cuni.mff.d3s.been.mq.rep;

import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
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
}
