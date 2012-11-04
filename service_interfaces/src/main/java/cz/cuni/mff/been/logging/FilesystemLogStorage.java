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
package cz.cuni.mff.been.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.cuni.mff.been.utils.FileUtils;

/**
 * Log storage which stores the logs in files in a directory structure.
 * 
 * @author Jaroslav Urban
 */
public class FilesystemLogStorage implements LogStorage {
	/** Name of the file with task's log messages */
	private static final String LOG_FILE = "task.log";
	/** Name of the file with task's host name */
	private static final String HOSTNAME_FILE = "hostname.txt";
	/** Name of the file with task's standard output */
	private static final String STDOUT_FILE = "stdout.txt";
	/** Name of the file with task's error output */
	private static final String STDERR_FILE = "stderr.txt";
	/**
	 * If used in the log message interval specification, all messages will be
	 * returned.
	 */
	private static final long ALL_MESSAGES = -1;
	/** The root directory for the log storage's data */
	private final String basedir;

	/**
	 * Handle to the files with standard output and error output.
	 * 
	 * @author Jaroslav Urban
	 */
	private class FileOutputHandle extends UnicastRemoteObject implements OutputHandle {

		private static final long serialVersionUID = -4270082151611203333L;

		private final BufferedReader reader;
		private final String file;

		/**
		 * 
		 * Allocates a new <code>FileOutputHandle</code> object.
		 * 
		 * @param file
		 *          path to a file with the standard output or error output.
		 * @throws RemoteException
		 * @throws FileNotFoundException
		 */
		protected FileOutputHandle(String file) throws RemoteException,
				FileNotFoundException {
			super();

			this.file = file;
			reader = new BufferedReader(new FileReader(file));
		}

		@Override
		public String[] getNextLines(int count) throws RemoteException, IOException {
			ArrayList<String> output = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				String newline = reader.readLine();
				if (null == newline) {
					break;
				} else {
					output.add(newline);
				}
			}
			return output.toArray(new String[output.size()]);
		}

		@Override
		public void skipLines(long count) throws RemoteException, IOException {
			for (int i = 0; i < count; i++) {
				if (null == reader.readLine())
					break; // No reason to read past end.
			}
		}

		@Override
		public long getLineCount() throws RemoteException, IOException {
			long count = 0;
			BufferedReader r = new BufferedReader(new FileReader(file));
			while (r.readLine() != null) {
				count++;
			}
			r.close();
			return count;
		}
	}

	/**
	 * Contains partial information about a log message, the information has to be
	 * parsed later.
	 * 
	 * @author Jaroslav Urban
	 */
	private class LogMessagePart {
		private final String timestamp;
		private final String level;
		private final String message;

		/**
		 * 
		 * Allocates a new <code>LogMessagePart</code> object.
		 * 
		 * @param timestamp
		 * @param level
		 * @param message
		 */
		public LogMessagePart(String timestamp, String level, String message) {
			this.timestamp = timestamp;
			this.level = level;
			this.message = message;
		}

		/**
		 * @return the level
		 */
		public String getLevel() {
			return this.level;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return this.message;
		}

		/**
		 * @return the timestamp
		 */
		public String getTimestamp() {
			return this.timestamp;
		}
	}

	/**
	 * 
	 * Allocates a new <code>FilesystemLogStorage</code> object.
	 * 
	 * @param basedir
	 *          base directory for storing the logs; it will be created by this
	 *          object automacically. If the directory already exists, it will be
	 *          reused.
	 * @throws LogStorageException
	 *           if an old existing base directory wasn't found and a new one
	 *           couldn't be created.
	 */
	public FilesystemLogStorage(String basedir) throws LogStorageException {
		this.basedir = basedir;
		File basedirFile = new File(basedir);
		// try to find old existing base directory
		if (!basedirFile.isDirectory()) {
			// create a new base directory because older one not found
			if (!basedirFile.mkdir()) {
				throw new LogStorageException("Cannot create base directory: " + basedir);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#addContext(java.lang.String)
	 */
	@Override
	public void addContext(String name) throws LogStorageException, IllegalArgumentException, NullPointerException {
		if (name == null) {
			throw new NullPointerException("Context name is null");
		}
		if (name.equals("")) {
			throw new IllegalArgumentException("Context name is empty");
		}

		File contextDir = new File(basedir, name);
		// try to find old existing context directory
		if (!contextDir.isDirectory()) {
			// create a new context directory because old one not found
			if (!contextDir.mkdir()) {
				throw new LogStorageException("Cannot create context's log directory: " + contextDir.getAbsolutePath());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#addTask(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addTask(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		// try to find old existing task directory
		if (!taskDir.isDirectory()) {
			// create a new task directory because old one not found
			if (!taskDir.mkdir()) {
				throw new LogStorageException("Cannot create task's log directory: " + taskDir.getAbsolutePath());
			}
		}

		// create empty log files
		createLogFile(taskDir, LOG_FILE);
		createLogFile(taskDir, STDOUT_FILE);
		createLogFile(taskDir, STDERR_FILE);
	}

	/**
	 * Creates an empty log file.
	 * 
	 * @param directory
	 *          directory where the file will be created.
	 * @param filename
	 *          filename of the log file.
	 * @throws LogStorageException
	 *           if the file coudln't be created.
	 */
	private void createLogFile(File directory, String filename) throws LogStorageException {

		File logFile = null;
		try {
			logFile = new File(directory, filename);
			// try to find old existing log file
			if (!logFile.exists()) {
				// create a new log file because old one not found
				if (!logFile.createNewFile()) {
					throw new LogStorageException("Cannot create log file: " + logFile.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			throw new LogStorageException("Cannot create log file: " + (null == logFile
					? "[NULL]" : logFile.getAbsolutePath()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#getLogsForTask(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public LogRecord[] getLogsForTask(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		return getLogsForTask(context, taskID, ALL_MESSAGES, ALL_MESSAGES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#log(java.lang.String,
	 * java.lang.String, java.util.Date, cz.cuni.mff.been.logging.LogLevel,
	 * java.lang.String)
	 */
	@Override
	public void log(String context, String taskID, Date timestamp,
			LogLevel level, String message) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		if (timestamp == null) {
			throw new NullPointerException("Timestamp is null");
		}
		if (level == null) {
			throw new NullPointerException("Log level is null");
		}
		if (message == null) {
			throw new NullPointerException("Log message is null");
		}

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		File logfile = new File(taskDir, LOG_FILE);
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(logfile, true), true);

			SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateTimeInstance();
			format.applyPattern("dd.MM.yyyy HH:mm:ss.SSS");
			writer.println(format.format(timestamp) + "\t" + level + "\t" + message + "\t\t\t");

			writer.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot append the message to a log file: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#removeContext(java.lang.String)
	 */
	@Override
	public void removeContext(String name) throws LogStorageException, IllegalArgumentException, NullPointerException {
		if (name == null) {
			throw new NullPointerException("Context name is null");
		}
		if (name.equals("")) {
			throw new IllegalArgumentException("Context name is empty");
		}

		File contextDir = new File(basedir, name);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + name);
		}

		try {
			FileUtils.deleteDirectory(contextDir);
		} catch (IOException e) {
			throw new LogStorageException("Cannot delete the directory of the context: " + contextDir.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#setTaskHostname(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setTaskHostname(String context, String taskID, String hostname) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		if (hostname == null) {
			throw new NullPointerException("Hostname is null");
		}
		if (hostname.equals("")) {
			throw new IllegalArgumentException("Hostname is empty");
		}

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		File hostnameFile = new File(taskDir, HOSTNAME_FILE);
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(hostnameFile), true);
			writer.println(hostname);
			writer.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot store hostname in the file: " + hostnameFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#isContextRegistered(java.lang.String)
	 */
	@Override
	public boolean isContextRegistered(String name) throws LogStorageException, IllegalArgumentException, NullPointerException {
		if (name == null) {
			throw new NullPointerException("Context name is null");
		}
		if (name.equals("")) {
			throw new IllegalArgumentException("Context name is empty");
		}

		File contextDir = new File(basedir, name);
		return contextDir.isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#isTaskRegistered(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isTaskRegistered(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		return taskDir.isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#addErrorOutput(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void addErrorOutput(String context, String taskID, String output) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		if (output == null) {
			throw new NullPointerException("Output is null");
		}

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		File outputFile = new File(taskDir, STDERR_FILE);
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.print(output);
			writer.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot store error output in the file: " + outputFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#addStandardOutput(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void addStandardOutput(String context, String taskID, String output) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		if (output == null) {
			throw new NullPointerException("Output is null");
		}

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		File outputFile = new File(taskDir, STDOUT_FILE);
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.print(output);
			writer.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot store standard output in the file: " + outputFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#getStandardOutput(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public OutputHandle getStandardOutput(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		try {
			return new FileOutputHandle(taskDir.getAbsolutePath() + File.separator + STDOUT_FILE);
		} catch (Exception e) {
			throw new LogStorageException("Cannot create the output handle: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#getErrorOutput(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public OutputHandle getErrorOutput(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		try {
			return new FileOutputHandle(taskDir.getAbsolutePath() + File.separator + STDERR_FILE);
		} catch (Exception e) {
			throw new LogStorageException("Cannot create the output handle: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.logging.LogStorage#getLogCountForTask(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public long getLogCountForTask(String context, String taskID) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		File logfile = new File(taskDir, LOG_FILE);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(logfile));
		} catch (FileNotFoundException e) {
			throw new LogStorageException("Log file not found: " + logfile.getAbsolutePath(), e);
		}

		long count = 0;
		try {
			String line;

			while ((line = reader.readLine()) != null) {
				parseLogMessage(line, reader);
				count++;
			}

			reader.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot read log file: " + e.getMessage());
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#getLogsForTask(java.lang.String,
	 * java.lang.String, long, long)
	 */
	@Override
	public LogRecord[] getLogsForTask(String context, String taskID, long first,
			long last) throws LogStorageException, IllegalArgumentException, NullPointerException {
		checkTaskAndContextId(context, taskID);

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new IllegalArgumentException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskID);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskID);
		}

		ArrayList<LogRecord> logRecords = new ArrayList<LogRecord>();

		File logfile = new File(taskDir, LOG_FILE);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(logfile));
		} catch (FileNotFoundException e) {
			throw new LogStorageException("Log file not found: " + logfile.getAbsolutePath(), e);
		}

		long count = 0;
		try {
			String line;
			String hostname = null;

			while ((line = reader.readLine()) != null) {
				// some logs were produced because the log file is not empty,
				// so try to read the host name of the task (if it wasn't
				// already read)
				if (hostname == null) {
					hostname = getHostname(taskDir);
				}

				LogMessagePart logPart = parseLogMessage(line, reader);

				if (((count >= first) && (count <= last)) || ((first == ALL_MESSAGES) && (last == ALL_MESSAGES))) {
					// inside the interval, create the log record
					logRecords.add(createLogRecord(logPart, context, taskID, hostname));
				}

				count++;
			}

			reader.close();
		} catch (IOException e) {
			throw new LogStorageException("Cannot read log file: " + e.getMessage());
		}

		return logRecords.toArray(new LogRecord[logRecords.size()]);
	}

	/**
	 * Parses one log message from the log file.
	 * 
	 * @param firstLine
	 *          first line of the log message.
	 * @param reader
	 *          reader of the log file.
	 * @return the log message.
	 * @throws IOException
	 */
	private LogMessagePart parseLogMessage(String firstLine, BufferedReader reader) throws IOException {
		String line = firstLine;

		// parse the line from the log file
		int delimiter = line.indexOf("\t");
		String timestamp = line.substring(0, delimiter);
		int delimiter2 = line.indexOf("\t", delimiter + 1);
		String logLevel = line.substring(delimiter + 1, delimiter2);
		int endDelimiter = 0;
		int startIndex = delimiter2 + 1;
		String message = new String();

		// parse a possibly multiline log message
		endDelimiter = line.indexOf("\t\t\t", delimiter2 + 1);
		while (true) {
			if (endDelimiter == -1) {
				message += line.substring(startIndex) + "\n";
			} else {
				message += line.substring(startIndex, endDelimiter);
				break;
			}
			line = reader.readLine();
			// only the first line of a multiline log message doesn't begin
			// at the start of the line in the log file (there's the time stamp
			// and log level first), so for every following line set the
			// startIndex to 0 and search for the end delimiter from the
			// beginning of the line.
			startIndex = 0;
			endDelimiter = line.indexOf("\t\t\t");
		}

		return new LogMessagePart(timestamp, logLevel, message);
	}

	/**
	 * Creates a log record.
	 * 
	 * @param logPart
	 *          unparsed log message.
	 * @param context
	 *          context ID of the task.
	 * @param taskID
	 *          task ID of the task.
	 * @param hostname
	 *          host name of the task.
	 * @throws LogStorageException
	 */
	private LogRecord createLogRecord(LogMessagePart logPart, String context,
			String taskID, String hostname) throws LogStorageException {
		try {
			SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateTimeInstance();
			format.applyPattern("dd.MM.yyyy HH:mm:ss.SSS");

			return new LogRecord(context, taskID, hostname, format.parse(logPart.getTimestamp()), LogLevel.valueOf(logPart.getLevel()), logPart.getMessage());
		} catch (ParseException e) {
			throw new LogStorageException("Cannot parse timestamp in the log file: " + e.getMessage(), e);
		}

	}

	/**
	 * Gets the host name of a task.
	 * 
	 * @param taskDir
	 *          directory with task's logs.
	 * @return host name of the task.
	 * @throws LogStorageException
	 */
	private String getHostname(File taskDir) throws LogStorageException {
		File hostnameFile = new File(taskDir, HOSTNAME_FILE);
		BufferedReader hostnameReader = null;
		try {
			hostnameReader = new BufferedReader(new FileReader(hostnameFile));
			return hostnameReader.readLine();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Hostname for the task wasn't set");
		} catch (IOException e) {
			throw new LogStorageException("Cannot read the hostname from the file:" + hostnameFile.getAbsolutePath());
		} finally {
			try {
				if (hostnameReader != null) {
					hostnameReader.close();
				}
			} catch (Exception e) {
				// nothing to do
			}
		}
	}

	/**
	 * Checks validity of the context ID and task ID.
	 * 
	 * @param context
	 * @param taskID
	 */
	private void checkTaskAndContextId(String context, String taskID) {
		if (context == null) {
			throw new NullPointerException("Context name is null");
		}
		if (context.equals("")) {
			throw new IllegalArgumentException("Context name is empty");
		}
		if (taskID == null) {
			throw new NullPointerException("Task ID is null");
		}
		if (taskID.equals("")) {
			throw new IllegalArgumentException("Task ID is empty");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.logging.LogStorage#removeTask(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void removeTask(String context, String taskId) throws LogStorageException {

		if (context == null) {
			throw new LogStorageException("Context name is null");
		}
		if (context.equals("")) {
			throw new LogStorageException("Context name is empty");
		}

		if (taskId == null) {
			throw new LogStorageException("Task id is null");
		}
		if (taskId.equals("")) {
			throw new LogStorageException("Task id is empty");
		}

		File contextDir = new File(basedir, context);
		if (!contextDir.isDirectory()) {
			throw new LogStorageException("Context not registered: " + context);
		}

		File taskDir = new File(contextDir, taskId);
		if (!taskDir.isDirectory()) {
			throw new IllegalArgumentException("Task not registered: " + taskId);
		}

		try {
			FileUtils.deleteDirectory(taskDir);
		} catch (IOException e) {
			throw new LogStorageException("Cannot delete the directory of the task: " + taskDir.getAbsolutePath(), e);
		}

	}
}
