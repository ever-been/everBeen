package cz.cuni.mff.d3s.been.plugerized;

import cz.cuni.mff.d3s.been.pluger.PlugerException;
import cz.cuni.mff.d3s.been.pluger.impl.Pluger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by donarus on 8.3.15.
 */
public class Bootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<>();
        config.put(Pluger.PROGRAM_ARGS, args);
        config.put(Pluger.WORKING_DIRECTORY_KEY, Paths.get("target", "pluger"));
        config.put(Pluger.CLEAR_LIB_DIR_KEY, true);

        Pluger pluger = null;
        try {
            pluger = Pluger.create(config);
        } catch (PlugerException e) {
            LOG.error("Pluger framework can't be initialized", e);
            System.exit(99);
        }

        pluger.start();
    }

}
