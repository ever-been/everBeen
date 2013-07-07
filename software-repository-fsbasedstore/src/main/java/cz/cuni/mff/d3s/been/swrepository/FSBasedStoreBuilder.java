package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.datastore.SoftwareStore;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A filesystem based implementation of the software store
 */
public class FSBasedStoreBuilder implements SoftwareStoreBuilder {

    private static final Logger log = LoggerFactory.getLogger(FSBasedStoreBuilder.class);

    private Properties properties = new Properties();

    @Override
    public SoftwareStoreBuilder withProperties(Properties properties) {
        if (properties != null) {
            this.properties = properties;
        } else {
            log.error("Trying to attribute null properties to {}", getClass().getSimpleName());
        }
        return this;
    }

    @Override
    public SoftwareStore buildCache() {
        return FSBasedStore.createCache(properties);
    }

    @Override
    public SoftwareStore buildServer() {
        return FSBasedStore.createServer(properties);
    }
}
