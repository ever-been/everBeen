package cz.cuni.mff.d3s.been.hostruntime.cz.cuni.mff.d3s.been.hostruntime

import cz.cuni.mff.d3s.been.hostruntime.IHostRuntime
import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator

/**
 * Created by donarus on 8.3.15.
 */
class HostRuntimePluginActivator implements IPluginActivator {

    private HostRuntime hostRuntime

    @Override
    void activate(IServiceRegistrator registry) {
        hostRuntime = registry.registerService(IHostRuntime, HostRuntime)
    }

    @Override
    void initialize() {

    }

    @Override
    void start() {

    }

    @Override
    void notifyStarted() {

    }
}
