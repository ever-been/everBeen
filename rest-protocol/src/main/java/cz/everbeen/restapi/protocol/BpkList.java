package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A listing of available BPKs
 *
 * @author darklight
 */
public class BpkList implements ProtocolObject {

	@JsonProperty("bpkIds")
	private final Collection<String> bpkIds;

	public BpkList(@JsonProperty("bpkIds") Collection<String> bpkIds) {
		this.bpkIds = Collections.unmodifiableCollection(bpkIds);
	}

	/**
	 * Get the BPK IDs
	 * @return The IDs of selected BPKs
	 */
	public Collection<String> getBpkIds() {
		return bpkIds;
	}
}
