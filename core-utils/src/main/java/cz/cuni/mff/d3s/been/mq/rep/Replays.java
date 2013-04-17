package cz.cuni.mff.d3s.been.mq.rep;

/**
 * @author Martin Sixta
 */
public class Replays {
	public static Replay createErrorReplay(String msg) {
		return new Replay(ReplayType.ERROR, msg);
	}

	public static Replay createErrorReplay(String format, Object... args) {
		return createErrorReplay(String.format(format, args));
	}

	public static Replay createOkReplay(String msg) {
		return new Replay(ReplayType.OK, msg);
	}

	public static Replay createOkReplay(String format, Object... args) {
		return createOkReplay(String.format(format, args));
	}
}
