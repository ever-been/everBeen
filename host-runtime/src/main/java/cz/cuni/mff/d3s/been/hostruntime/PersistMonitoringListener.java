package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.detectors.MonitoringListener;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author Kuba Brecka
*/
class PersistMonitoringListener implements MonitoringListener {

	private static final Logger log = LoggerFactory.getLogger(PersistMonitoringListener.class);

	private ClusterContext ctx;
	private HostRuntime runtime;

	public PersistMonitoringListener(ClusterContext ctx, HostRuntime runtime) {
		this.ctx = ctx;
		this.runtime = runtime;
	}

	@Override
	public void sampleGenerated(MonitorSample sample) {
		try {
			MonitoringEntity entity = new MonitoringEntity();
			entity.setSample(sample);
			entity.setRuntimeId(runtime.getId());
			entity.setCreated(sample.getTimestamp() / 1_000_000);
			ctx.getPersistence().asyncPersist(Entities.LOG_MONITORING.getId(), entity);
		} catch (DAOException e) {
			log.warn("Cannot persist monitoring sample for Host Runtime {}", runtime.getId(), e);
		}
	}

	@Override
	public void close() {

	}
}
