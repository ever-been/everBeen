package cz.cuni.mff.d3s.been.socketworks.twoway;

/**
 * @author Martin Sixta
 */
public class Replies {
	public static Reply createErrorReply(String msg) {
		return new Reply(ReplyType.ERROR, msg);
	}

	public static Reply createErrorReply(String format, Object... args) {
		return createErrorReply(String.format(format, args));
	}

	public static Reply createOkReply(String msg) {
		return new Reply(ReplyType.OK, msg);
	}

	public static Reply createOkReply(String format, Object... args) {
		return createOkReply(String.format(format, args));
	}
}
