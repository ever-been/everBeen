package cz.cuni.mff.d3s.been.logging;

import ch.qos.logback.classic.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Converter that handles conversion of log levels from Logback to BEEN numbering
 *
 * @author darklight
 */
class LogLevelConverter {
    private final Map<Level, Integer> levelMap;

    /**
     * Create and initialize the converter
     */
    LogLevelConverter() {
        levelMap = new HashMap<Level, Integer>();

        levelMap.put(Level.OFF, LogLevel.DUMMY.ordinal());
        levelMap.put(Level.ALL, LogLevel.DUMMY.ordinal());

        levelMap.put(Level.TRACE, LogLevel.TRACE.ordinal());
        levelMap.put(Level.DEBUG, LogLevel.DEBUG.ordinal());
        levelMap.put(Level.INFO, LogLevel.INFO.ordinal());
        levelMap.put(Level.WARN, LogLevel.WARN.ordinal());
        levelMap.put(Level.ERROR, LogLevel.ERROR.ordinal());
    }

    /**
     * Convert Logback log level to BEEN log level
     *
     * @param level Log level to convert
     *
     * @return BEEN numbering for given log level
     */
    public int getBeenLogLevel(Level level) {
        return levelMap.get(level);
    }
}
