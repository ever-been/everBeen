package cz.cuni.mff.d3s.been.databus.hazelcast

import cz.cuni.mff.d3s.been.databus.IDataBus
import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator

/**
 * Created by donarus on 8.3.15.
 */
class DataBusHazelcastPluginActivator implements IPluginActivator {

    private DataBusHazelcast dataBus

    @Override
    void activate(IServiceRegistrator registry) {
        dataBus = registry.registerService(IDataBus, DataBusHazelcast)
    }

    @Override
    void initialize() {

    }

    @Override
    void start() {
        dataBus.connect()
    }

    @Override
    void notifyStarted() {

    }
}
