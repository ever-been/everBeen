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
package cz.cuni.mff.been.task.test.logtester;

import java.util.Random;

import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * <p>Tests the log storage by generating log messages or standard and 
 * error output.</p>
 * 
 * <b>Task properties:</b><br>
 * <ul>
 * <li>{@value #MESSAGE_COUNT}: <br>
 * 		- how many messages will be generated <br>
 * 		- mandatory <br>
 * <li>{@value #MESSAGE_DELAY}: <br>
 * 		- time delay between messages, in milliseconds <br>
 * 		- mandatory <br>
 * <li>{@value #ACTION}: <br>
 * 		- if value is {@value #ACTION_LOG}, then log messages will be 
 * 		generated. If the value is {@value #ACTION_OUTPUT}, then standard 
 * 		and error output will be generated. <br>
 * 		- mandatory <br>
 * </ul>
 * 
 * @author Jaroslav Urban
 */
public class LogTester extends Job {
	/** 
	 * Task property name for the delay between sending messages, the 
	 * delay is in milliseconds.
	 */
	public static final String MESSAGE_DELAY = "message.delay";
	/**
	 * Task property name for the number of messages that will be sent.
	 */
	public static final String MESSAGE_COUNT = "message.count";
	/**
	 * Task property name for the action which will be executed by the task.
	 */
	public static final String ACTION = "action";
	/**
	 * Valid value of the {@value #ACTION} task property for generating
	 * log messages.
	 */
	public static final String ACTION_LOG = "log";
	/**
	 * Valid value of the {@value #ACTION} task property for generating
	 * standard and error output.
	 */
	public static final String ACTION_OUTPUT = "output";
	/**
	 * Task property name for the probability of an empty line in an output 
	 * message sent to the task manager.
	 */
	private static final String EMPTY_LINE_PROBABILITY = "empty-line.probability";

	private static final Random RAND = new Random(System.currentTimeMillis());

	/** Default probability of an empty line in the output */
	private static final double DEFAULT_EMPTY_PROBABILITY = 0.2;
	/** Probability of an empty line in the output */
	private double emptyProbability = DEFAULT_EMPTY_PROBABILITY;
	
	/**
	 * 
	 * Allocates a new <code>LogTester</code> object.
	 *
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public LogTester() throws TaskInitializationException {
		super();
	}

	@Override
	protected void run() throws TaskException {
		int messageCount = Integer.valueOf(getTaskProperty(MESSAGE_COUNT));
		logInfo("Number of messages that will be logged: " + messageCount);
		int delay = Integer.valueOf(getTaskProperty(MESSAGE_DELAY));
		logInfo("Delay between logged messages, in milliseconds: " + delay);
		
		String action = getTaskProperty(ACTION);
		if (action.equals(ACTION_LOG)) {
			logInfo("Action: log");
			for (int i = 0; i < messageCount; i++) {
				sendLogMessage(i);
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// nothing bad happened
				}
			}
		} else if (action.equals(ACTION_OUTPUT)) {
			logInfo("Action: output");
			String emptyProbabilityProperty = getTaskProperty(EMPTY_LINE_PROBABILITY);
			if (emptyProbabilityProperty != null) {
				emptyProbability = Double.valueOf(emptyProbabilityProperty);
			}
			
			for (int i = 0; i < messageCount; i++) {
				String line = null;
				if (RAND.nextDouble() < emptyProbability) {
					line = "";
				} else {
					line = "Line " + i;
				}
				if (RAND.nextBoolean()) {
					System.out.println(line);
				} else {
					System.err.println(line);		
				}

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// nothing bad happened
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	/**
	 * Sends a log message with a random severity.
	 * 
	 * @param messageIndex number of the message.
	 */
	private void sendLogMessage(int messageIndex) {
		int severity = RAND.nextInt(6);
		String message  = "Generated log message #" + messageIndex; 
		while (RAND.nextBoolean()) {
			message += "\nadditional line: blah blah blah blah blah blah blah blah";
		}
		if (severity == 0) {
			logTrace(message);
		}
		if (severity == 1) {
			logDebug(message);
		}
		if (severity == 2) {
			logInfo(message);
		}
		if (severity == 3) {
			logWarning(message);
		}
		if (severity == 4) {
			logError(message);
		}
		if (severity == 5) {
			logFatal(message);
		}
	}

	
	@Override
	protected void checkRequiredProperties() throws TaskException {
		checkRequiredProperties(new String[]{
				MESSAGE_COUNT,
				MESSAGE_DELAY,
				ACTION
		});
	}
	
}
