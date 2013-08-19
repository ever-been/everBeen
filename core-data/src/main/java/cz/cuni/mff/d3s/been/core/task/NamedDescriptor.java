package cz.cuni.mff.d3s.been.core.task;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * @author donarus
 */
public abstract class NamedDescriptor<T> implements Serializable {

	private final String name;
    private final String groupId;
    private final String bpkId;
    private final String bpkVersion;
	private final T descriptor;


    public NamedDescriptor(final String name, final String groupId, final String bpkId, final String bpkVersion, final T descriptor) {
        this.name = name;
        this.groupId = groupId;
        this.bpkId = bpkId;
        this.bpkVersion = bpkVersion;
        this.descriptor = descriptor;
    }

    public final String getName() {
		return name;
	}

    public final String getGroupId() {
        return groupId;
    }

    public final String getBpkId() {
        return bpkId;
    }

    public final String getBpkVersion() {
        return bpkVersion;
    }

	public final T getDescriptor() {
		return descriptor;
	}

}
