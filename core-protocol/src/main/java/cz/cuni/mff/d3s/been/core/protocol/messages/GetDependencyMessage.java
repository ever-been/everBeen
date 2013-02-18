package cz.cuni.mff.d3s.been.core.protocol.messages;


@SuppressWarnings("serial")
public final class GetDependencyMessage extends BaseMessage {

	/**
	 * fill relative path to dependency stored in software repository
	 */
	public String fullName;

}
