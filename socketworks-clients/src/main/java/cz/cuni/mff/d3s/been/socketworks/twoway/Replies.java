package cz.cuni.mff.d3s.been.socketworks.twoway;

/**
 * Request reply creation facade
 *
 * @author Martin Sixta
 */
public class Replies {

	/**
	 * Create an error reply with a message
	 *
	 * @param msg Error reason phrase
	 *
	 * @return The error reply
	 */
	public static Reply createErrorReply(String msg) {
		return new Reply(ReplyType.ERROR, msg);
	}

	/**
	 * Create an error reply with a message with arguments
	 *
	 * @param format Error reason phrase format
	 * @param args Error reason phrase arguments
	 *
	 * @return The error reply
	 */
	public static Reply createErrorReply(String format, Object... args) {
		return createErrorReply(String.format(format, args));
	}

	/**
	 * Create an OK reply with a message
	 *
	 * @param msg Message
	 *
	 * @return The reply
	 */
	public static Reply createOkReply(String msg) {
		return new Reply(ReplyType.OK, msg);
	}

	/**
	 * Create an OK reply with a formatted message
	 *
	 * @param format Message format
	 * @param args Message arguments
	 *
	 * @return The reply
	 */
	public static Reply createOkReply(String format, Object... args) {
		return createOkReply(String.format(format, args));
	}
}
