package cz.cuni.mff.d3s.been.databus.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import cz.cuni.mff.d3s.been.databus.IDataBus
import cz.cuni.mff.d3s.been.databus.UUIDType
import cz.cuni.mff.d3s.been.pluger.InjectService
import cz.cuni.mff.d3s.been.pluger.PlugerServiceConstants

/**
 * Created by donarus on 8.3.15.
 */
class DataBusHazelcast implements IDataBus {

    private static final String HC_LOGGING_TYPE_KEY = "hazelcast.logging.type"

    private static final String HC_LOGGING_TYPE_DEFAULT = "slf4j"

    @InjectService(serviceName = PlugerServiceConstants.PROGRAM_ARGUMENTS)
    private String[] programArgs

    @InjectService(serviceName = PlugerServiceConstants.PLUGIN_CLASSLOADER)
    private ClassLoader pluginsClassLoader

    private HazelcastInstance hazelcastInstance

    void connect() {
        def config = new Config()
        config.setClassLoader(pluginsClassLoader)
        def loggingType = System.getenv().get(HC_LOGGING_TYPE_KEY)
        config.setProperty(HC_LOGGING_TYPE_KEY, loggingType != null ? loggingType : HC_LOGGING_TYPE_DEFAULT)
        hazelcastInstance = Hazelcast.newHazelcastInstance(config)
    }

    @Override
    void generateUUID(String groupName, UUIDType uuidType) {
        long generated = hazelcastInstance.getIdGenerator(groupName).newId()
        if(generated > uuidType.maxValue) {
            // TODO exception handling
            throw new RuntimeException();
        }
        generated.toString().padLeft(uuidType.hexCharCount, "0")
    }
}
