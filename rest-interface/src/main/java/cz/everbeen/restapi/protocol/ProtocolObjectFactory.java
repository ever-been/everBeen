package cz.everbeen.restapi.protocol;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A factory for {@link cz.everbeen.restapi.protocol.ProtocolObject} instances
 *
 * @author darklight
 */
public final class ProtocolObjectFactory {

	private static final String BPK_ID_PATTERN = "%s:%s:%s";

	/**
	 * Create a {@link cz.everbeen.restapi.protocol.BpkList} from a collection of {@link cz.cuni.mff.d3s.been.bpk.BpkIdentifier}s
	 * @param bpkIds The BPK identifiers
	 * @return The bpk list
	 */
	public static BpkList bpkList(Collection<BpkIdentifier> bpkIds) {
		final Collection<String> tmpIds = new ArrayList<String>(bpkIds.size());
		for (BpkIdentifier bpkId: bpkIds) tmpIds.add(bpkIdToString(bpkId));
		return new BpkList(tmpIds);
	}

	private static String bpkIdToString(BpkIdentifier bpkId) {
		return String.format(BPK_ID_PATTERN, bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion());
	}
}
