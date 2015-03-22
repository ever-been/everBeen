package cz.cuni.mff.d3s.been.hostruntime.cz.cuni.mff.d3s.been.hostruntime

import cz.cuni.mff.d3s.been.databus.IDataBus
import cz.cuni.mff.d3s.been.databus.UUIDType
import cz.cuni.mff.d3s.been.hostruntime.IHostRuntime
import cz.cuni.mff.d3s.been.pluger.InjectService
import cz.cuni.mff.d3s.been.pluger.PlugerServiceConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by donarus on 8.3.15.
 */
class HostRuntime implements IHostRuntime {

    private static final Logger LOG = LoggerFactory.getLogger(HostRuntime)

    @InjectService
    private IDataBus dataBus

    @InjectService(serviceName = PlugerServiceConstants.PROGRAM_ARGUMENTS)
    private String[] programArgs

    @InjectService(serviceName = PlugerServiceConstants.PLUGINS_WORKING_DIRECTORY)
    private File pluginsWorkingDirectory

    private String uuid

    public void start() {
        uuid = dataBus.generateUUID(HostRuntime.getName(), UUIDType.INTEGER)
        LOG.info("generated uuid: $uuid")
    }
}
