package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;

/**
 * @author donarus
 */
public class ServiceInfoUpdater implements Runnable {

    private final ClusterContext clusterCtx;

    private final ServiceInfo info;

    private final int timeout;

    public ServiceInfoUpdater(ClusterContext clusterCtx, ServiceInfo info, int timeout) {
        this.clusterCtx = clusterCtx;
        this.info = info;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        clusterCtx.storeServiceInfo(info, timeout);
    }

}
