package cz.cuni.mff.d3s.been.node;

import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: donarus
 * Date: 8/12/13
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceCleaner implements MembershipListener {
    private ClusterContext clusterContext;

    public ServiceCleaner(ClusterContext clusterContext) {
        this.clusterContext = clusterContext;
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        // IGNORE
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        String memberUuid = membershipEvent.getMember().getUuid();

        final SqlPredicate predicate = new SqlPredicate(String.format("hazelcastUuid = '%s'", memberUuid));
        Collection<ServiceInfo> infos = clusterContext.getServices().getServicesMap().values(predicate);
        for (ServiceInfo info : infos) {
            clusterContext.removeServiceInfo(info);
        }
    }

}
