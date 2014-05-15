package cz.everbeen.restapi.protocol;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import org.apache.http.annotation.Immutable;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A listing of available BPKs
 *
 * @author darklight
 */
@Immutable
public class BpkList implements ProtocolObject {

	private static final String BPK_ID_PATTERN = "%s:%s:%s";

	@JsonProperty("bpkIds")
	private final Collection<String> bpkIds;

	public BpkList(@JsonProperty("bpkIds") Collection<String> bpkIds) {
		this.bpkIds = Collections.unmodifiableCollection(bpkIds);
	}

	/**
	 * Create a {@link cz.everbeen.restapi.protocol.BpkList} from a collection of {@link cz.cuni.mff.d3s.been.bpk.BpkIdentifier}s
	 * @param bpkIds The BPK identifiers
	 * @return The bpk list
	 */
	public static BpkList fromIdCollection(Collection<BpkIdentifier> bpkIds) {
		final Collection<String> tmpIds = new ArrayList<String>(bpkIds.size());
		for (BpkIdentifier bpkId: bpkIds) tmpIds.add(bpkIdToString(bpkId));
		return new BpkList(tmpIds);
	}

	private static String bpkIdToString(BpkIdentifier bpkId) {
		return String.format(BPK_ID_PATTERN, bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion());
	}

	/**
	 * Get the BPK IDs
	 * @return The IDs of selected BPKs
	 */
	public Collection<String> getBpkIds() {
		return bpkIds;
	}
}
