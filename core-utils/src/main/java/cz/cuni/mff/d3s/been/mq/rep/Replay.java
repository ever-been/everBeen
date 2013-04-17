package cz.cuni.mff.d3s.been.mq.rep;

import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * @author Martin Sixta
 */
public final class Replay {
	private ReplayType replayType;

	private String value;

	Replay() {

	}

	public Replay(ReplayType replayType, String value) {
		this.replayType = replayType;
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

	public static Replay fromJson(String json) throws JSONUtils.JSONSerializerException {
		return JSONUtils.deserialize(json, Replay.class);
	}

	public ReplayType getReplayType() {
		return replayType;
	}

	public void setReplayType(ReplayType replayType) {
		this.replayType = replayType;
	}

	public String getValue() {
		return value;
	}
}
