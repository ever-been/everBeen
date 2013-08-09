package cz.cuni.mff.d3s.been.cluster;

import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import cz.cuni.mff.d3s.been.cluster.context.Services;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;

/**
 * @author donarus
 */
public class SwRepositoryInfoCleaner implements MembershipListener {

    private final Services services;

    public SwRepositoryInfoCleaner(Services services) {
        this.services = services;
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        // ignore
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        String memberUUID = membershipEvent.getMember().getUuid();
        SWRepositoryInfo swRepositoryInfo = services.getSWRepositoryInfo();

        if (swRepositoryInfo != null && swRepositoryInfo.getUuid().equals(memberUUID)) {
            services.removeSoftwareRepositoryInfo();
        }
    }
}
