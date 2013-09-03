package cz.cuni.mff.d3s.been.core.task;

import java.io.Serializable;

/**
 * Named Descriptor.
 * 
 * @author donarus
 */
public abstract class NamedDescriptor<T> implements Serializable {

	private final String name;
	private final String groupId;
	private final String bpkId;
	private final String bpkVersion;
	private final T descriptor;

	/**
	 * Creates new NamedDescriptor
	 * 
	 * @param name
	 *          name of the named descriptor
	 * @param groupId
	 *          groupId associated with the named descriptor
	 * @param bpkId
	 *          bpkId associated with the named descriptor
	 * @param bpkVersion
	 *          version associated with the named descriptor
	 * @param descriptor
	 *          the descriptor to save
	 */
	public NamedDescriptor(
			final String name,
			final String groupId,
			final String bpkId,
			final String bpkVersion,
			final T descriptor) {
		this.name = name;
		this.groupId = groupId;
		this.bpkId = bpkId;
		this.bpkVersion = bpkVersion;
		this.descriptor = descriptor;
	}

	/**
	 * Returns the name of the named descriptor.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the groupId
	 * 
	 * @return the groupId
	 */
	public final String getGroupId() {
		return groupId;
	}

	/**
	 * Returns the bpkId.
	 * 
	 * @return the bpkId
	 */
	public final String getBpkId() {
		return bpkId;
	}

	/**
	 * Returns the version.
	 * 
	 * @return the version
	 */
	public final String getBpkVersion() {
		return bpkVersion;
	}

	/**
	 * Returns the descriptor
	 * 
	 * @return the descriptor
	 */
	public final T getDescriptor() {
		return descriptor;
	}

}
