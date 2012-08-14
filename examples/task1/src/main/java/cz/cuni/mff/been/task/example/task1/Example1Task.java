/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task.example.task1;

import hello.Hello;

import cz.cuni.mff.been.common.anttasks.Copy;
import cz.cuni.mff.been.logging.ConsoleLogger;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * <p>Example task explaining how to write tasks, use Ant to perform various 
 * operations and use 3rd party libraries. Ant is usefull for it's
 * multiplatform implementation of various file operations. For example, 
 * you may need to customize some source code after downloading it 
 * from the Software Repository or from the internet, which can be easily done 
 * with Ant. The task uses Ant to copy a file.
 * </p>
 * <p>It also demostrates how to use external libraries (there is a special 
 * simple hello.jar library) and logging. Logging is demonstrated for a task 
 * (it's log messages are sent to the log storage) and for a class which is not 
 * a task (it's log messages are written to the standard output).</p>
 * 
 * <b>Task properties:</b><br>
 * <ul>
 * <li>{@value #SOURCE_FILE}: <br>
 * 		- path to the file which will be copied <br>
 * 		- mandatory <br>
 * <li>{@value #DESTINATION_FILE}: <br>
 * 		- name of the file's copy <br>
 * 		- mandatory<br>
 * </ul>
 *
 * @author Jaroslav Urban
 */
public class Example1Task extends Job {
	//------------------------CONSTANTS--------------------
	
	/*
	 * The task properties in this task are just as an example of how to use them.
	 * Typically when you use Ant to perform some operation, the filenames etc will
	 * be always the same, so you will use constants in the task's source.
	 */
	/**
	 * Task property name for the file to be copied
	 */
	public static final String SOURCE_FILE = "src.file";
	/**
	 * Task property name for the name of the file's copy
	 */
	public static final String DESTINATION_FILE = "dest.file";
	
	//-----------------------ATTRIBUTES--------------------
	
	/**
	 * File to be copied
	 */
	private String srcFile = null;
	/**
	 * Filename of the copy of the file
	 */
	private String destFile = null;
	
	/**
	 * Small class used to demonstrate how to log with classes that 
	 * don't extend the Task class. It's usefull for classes that aren't tasks
	 * (e.g.Host Runtime) so they  can log to stdout in a format which is 
	 * consistent with Task's loggin format.
	 * This class also demonstrates how to get a reference to the current
	 * running Task from classes that don't extend the Task class, but are
	 * used by a running task. This is usefull for more complicated tasks, 
	 * e.g. services, which are made of several big classes which need to 
	 * use the task's logging facilities. It would be annoying to have to pass 
	 * the reference to the task to the other classes, so they can obtain 
	 * it from a static field of the Task class.
	 * 
	 * @author Jaroslav Urban
	 */
	private class Foo {
		// ConsoleLogger logs to stdout. The logger must have a name. Loggers
		// are in a hierarchy based on their names, e.g. "cz" is the parent logger
		// for "cz.cuni". Therefore it's recommended to use full classnames for the 
		// logger's names. You can pass a reference to the class using the logger
		// in the logger's constructor, and it will name itself automatically. Alternatively,
		// there is a contructor with a String parameter, so you can name the logger
		// as you want.
		// The logger can be static, it doesn't change the behavior. It can't be static here
		// because Foo is in inner class.
		private ConsoleLogger logger = new ConsoleLogger(this);
		
		/**
		 * Example method of the class, only does some example loggind.
		 *
		 */
		public void bar() {
			// default debug level for ConsoleLogger is INFO, so the logDebug
			// message will not print because it has lower debug level than INFO
			logger.logDebug("Foo debug message");
			logger.logInfo("Foo info message");
			logger.logFatal("Foo fatal message");
			
			// we set the logger's debug level higher, and now even the logInfo log message
			// will not print
			logger.setLevel(LogLevel.ERROR);
			logger.logInfo("Foo info message");
			
			// obtain the reference to the task which is using this class and 
			// use it's logging facilities
			Task myTask = Task.getTaskHandle();
			myTask.logInfo("Class Foo is logging through it's task");
		}
	}

	/**
	 * 
	 * Allocates a new <code>Example1Task</code> object.
	 *
	 * @throws TaskInitializationException
	 */
	public Example1Task() throws TaskInitializationException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	public void run() throws TaskException {
		// determine the source and destination file for copying. Get
		// the filenames from task properties. 
		srcFile = this.getTaskProperty(SOURCE_FILE);
		destFile = this.getTaskProperty(DESTINATION_FILE);
		
		this.logInfo("Starting copying");
		this.logDebug("Source file: " + srcFile);
		this.logDebug("Destination file: " + destFile);
		// the CopyFile class uses Ant to copy files, look at it's source to see how it's done
		try {
			Copy.copy(srcFile, destFile);
		} catch (Exception e) {
			throw new TaskException("Cannot copy file " + srcFile + " to " + destFile, e);
		}
		this.logInfo("Copying succesfull");
		
		// use an external library
		this.logInfo("Using the Hello library");
		(new Hello()).hello();
		
		// use the Foo class which demonstrates logging for "non tasks"
		this.logInfo("Using the Foo class");
		(new Foo()).bar();
	}
	
	@Override
	protected void checkRequiredProperties() throws TaskException {
		checkRequiredProperties(new String[]{SOURCE_FILE,DESTINATION_FILE});
	}
}
