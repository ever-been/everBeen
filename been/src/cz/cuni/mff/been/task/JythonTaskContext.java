package cz.cuni.mff.been.task;

/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */

import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.Logger;

import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogLevel;

/**
 * <p>Represents task context for Python script. Acts as an adapter to task's interface.</p>
 * 
 * At the time, the adapter only forwards request to the associated task class.
 * 
 * @author Jan Tattermusch
 */
public class JythonTaskContext {

    /**
     * Allocates a new adapter object for given task
     * @param task the adapted task object
     */
    public JythonTaskContext(Task task) {
        this.task = task;
    }
    private Task task;

    /**
     * Returns the tasksPort
     * @return the tasksPort
     */
    public TasksPortInterface getTasksPort() {
        return task.getTasksPort();
    }

    /**
     * Returns the directory  where task can store its results (permanently)
     * @return directory name
     */
    public String getWorkingDirectory() {
        return task.getWorkingDirectory();
    }

    /**
     * Returns the temporary working directory for the task
     * @return directory name
     */
    public String getTempDirectory() {
        return task.getTempDirectory();
    }

    /**
     * Returns the directory with the task package's content
     * @return directory name
     */
    public String getTaskDirectory() {
        return task.getTaskDirectory();
    }

    /**
     * Sets the log level for logging to stdout
     * @param level 
     */
    public void setLogLevel(LogLevel level) {
        task.setLogLevel(level);
    }

    /**
     * @return log level for logging to stdout
     */
    public LogLevel getLogLevel() {
        return task.getLogLevel();
    }

    /**
     * Logs a message on the DEBUG debug level
     * @param message
     */
    public void logDebug(String message) {
        task.getLogger().debug(message);
    }

    /**
     * Logs a message on the ERROR debug level
     * @param message
     */
    public void logError(String message) {
        task.getLogger().error(message);
    }

    /**
     * Logs a message on the FATAL debug level
     * @param message
     */
    public void logFatal(String message) {
        task.getLogger().fatal(message);
    }

    /**
     * Logs a message on the INFO debug level
     * @param message
     */
    public void logInfo(String message) {
        task.getLogger().info(message);
    }

    /**
     * Logs a message on the TRACE debug level
     * @param message
     */
    public void logTrace(String message) {
        task.getLogger().trace(message);
    }

    /**
     * Logs a message on the WARN debug level
     * @param message
     */
    public void logWarning(String message) {
        task.getLogger().warn(message);
    }

    /**
     * Retrieve logger that is used by the task.
     * 
     * @return logger used by the task.
     */
    public Logger getLogger() {

        return task.getLogger();
    }

    /**
     * Signals that a checkpoint was reached by the task.
     * 
     * @param name name of the checkpoint
     * @param value value of the checkpoint
     * @throws TaskException if anything goes wrong.
     */
    public void checkPointReached(String name, Serializable value) throws TaskException {
        task.checkPointReached(name, value);
    }

    /**
     * Waits until a task reaches a checkpoint of specified type and returns its
     * value.
     *        
     * @param contextID context ID, if null then the context of the calling task
     * will be used.
     * @param taskID task ID
     * @param name checkpoint name
     * @param timeout number of milliseconds to wait for a checkpoint;
     *         <code>0</code> means that the call will not block and returns
     *         immediately
     * @return value of the checkpoint.
     * @throws TaskException if anything goes wrong.
     */
    public Serializable checkPointWait(String contextID,
            String taskID,
            String name,
            long timeout)
            throws TaskException {
        return task.checkPointWait(contextID, taskID, name, timeout);
    }

    /**
     * Test if given property is null or not. This can be used to test presence of a property, however cannot be used
     * in all cases since null may be valid property value.
     * 
     * @param name Name of the property to test.
     * 
     * @return true if property is null, false if property is not null.
     */
    public boolean isPropertyNull(String name) {

        return task.isPropertyNull(name);
    }

    /**
     * Get all properties of the task.
     * 
     * @return all properties of the task.
     */
    public Properties getTaskProperties() {

        return task.getTaskProperties();
    }

    /**
     * Returns the value of a task's property
     * 
     * @param name name of the task's property
     * @return value of the task's property or <code>null</code>
     * if the specified property is not found
     */
    public String getTaskProperty(String name) {
        return task.getTaskProperty(name);
    }

    /**
     * Get value of the Task's property.
     * 
     * @param name Name of the property to retrieve.
     * @param defaultValue Default value which will be used when property is not set (if it is null).
     * 
     * @return Value of the property of default value.
     */
    public String getTaskProperty(String name, String defaultValue) {

        return task.getTaskProperty(name, defaultValue);
    }

    /**
     * Returns the value of a task property that can have boolean
     * values, e.g. "yes", "true" etc.
     * @param name name of the task property.
     * @return true if the value of the task property is was "yes" or "true".
     */
    public boolean getBooleanTaskProperty(String name) {
        return task.getBooleanTaskProperty(name);
    }

    /**
     * @param name name of the task property.
     * @return the object value of the task property.
     */
    public Serializable getTaskPropertyObject(String name) {
        return task.getTaskPropertyObject(name);
    }

    /**
     * Exits the task which successfully finished.
     */
    public static void exitSuccess() {
        throw new UnsupportedOperationException();
    }

    /**
     * Exits the task that finished with an error.
     *
     */
    public static void exitError() {
        throw new UnsupportedOperationException();
        
    }

    /**
     * @return the task descriptor of this task.
     */
    public TaskDescriptor getTaskDescriptor() {
        return task.getTaskDescriptor();
    }

    /**
     * 
     * @return true if the task is running in Windows.
     */
    public boolean isRunningInWindows() {
        return task.isRunningInWindows();
        
    }

    /**
     * 
     * @return true if the task is running in Linux.
     */
    public boolean isRunningInLinux() {
        return task.isRunningInLinux();
    }
}