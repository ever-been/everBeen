package cz.cuni.mff.d3s.been.node;

import cz.cuni.mff.d3s.been.pluger.IPluginActivator;
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator;

/**
 * Created by donarus on 8.3.15.
 */
public class NodePluginActivator implements IPluginActivator {

    private Runner runner;

    @Override
    public void activate(IServiceRegistrator registry) {
        runner = registry.registerService(Runner.class, Runner.class);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {
        runner.doMain();
    }

    @Override
    public void notifyStarted() {

    }
}
