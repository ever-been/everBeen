/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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
package cz.cuni.mff.been.task.xampler.execute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import cz.cuni.mff.been.common.OutputReader;
import cz.cuni.mff.been.common.StringUtils;
import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Chmod;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;

/**
 * Runs the Xampler benchmark.
 * 
 * @author Michal Tomcanyi
 */
public class XamplerExecute extends Job {

	/**
	 * Execution role
	 *
	 */
	private enum TaskRole {
		/* run as client */
		CLIENT,
		/* run as server */
		SERVER,
		/* run as both client and server */
		BOTH;
	}
	
	
	/**
	 * Name of Xampler file with IOR reference 
	 */
	private static final String XAMPLER_IOR_FILE = "Xampler.IOR";

	/**
	 * Directory with installed omniORB
	 */
	private static final String PROPERTY_OMNIORB_ROOT = "omniorb.root";
	
	/**
	 * Complete path to Xampler suite 
	 * (e.g Marshal_Type_Array/omniORB_4.0.X/Short_512_IN)
	 */
	private static final String PROPERTY_SUITE_PATH = "xampler.suite.path";
	
	/**
	 * Role of the process
	 * Property value is one of "server" or "client"
	 */
	private static final String PROPERTY_XAMPLER_ROLE = "xampler.role";
	
	/**
	 * Directory with compiled Xampler 
	 */
	private static final String PROPERTY_XAMPLER_ROOT = "xampler.root";
	
	/**
	 * TID of Xampler server exec task
	 */
	private static final String PROPERTY_SERVER_TID = "server.tid";
	
	/**
	 * Property carrying runtime parameters that will be passed to server process
	 */
	private static final String PROPERTY_SERVER_PARAMS = "xampler.server.params";
	
	/**
	 *  Property carrying runtime parameters that will be passed to server process
	 */
	private static final String PROPERTY_CLIENT_PARAMS = "xampler.client.params";
	
	/** If set, Xampler will be not executed for real */
    public static final String SIMULATE = "simulate";
	
	
	/**
	 * Name of checkpoint to reach when Xampler server starts
	 */
	public static final String CHECKPOINT_SERVER_STARTED = "server.started";
	
	/**
	 * Time in seconds to wait for Xampler server creates IOR reference file
	 */
	private static final int IOR_WAIT_DELAY = 60;
	
	// full path to client executable
	private String clientExecutable;
	
	// full path to server executable
	private String serverExecutable;	
	
	// value of LD_LIBRARY_PATH to use when executing
	private String ldLibraryPath;
	
	// client/server
	private String roleName;
	
	private TaskRole taskRole;
	
	
	// full path to suite directory
	private String suiteDir;
	
	// runtime parameters of client process
	private String[] clientRuntimeParams;
	
	//runtime parameters of server process
	private String[] serverRuntimeParams;

	// stdout reader
	private final OutputReader clientStdOutReader = new OutputReader();
	private final OutputReader serverStdOutReader = new OutputReader();
	
	// stderr reader
	private final OutputReader clientStdErrReader = new OutputReader();
	private final OutputReader serverStdErrReader = new OutputReader();

	private boolean simulate = false;

	
	public XamplerExecute() throws TaskInitializationException {
		super();
	}
	
	private TaskRole parseTaskRole(String roleName) throws TaskException {
		if ("client".equals(roleName)) {
			return TaskRole.CLIENT;
		}
		if ("server".equals(roleName)) {
			return TaskRole.SERVER;
		}
		if ("both".equals(roleName)) {
			return TaskRole.BOTH;
		}
		
		throw new TaskException("Error parsing task role \"" + roleName +"\"");
	}
	
	private void setup() throws TaskException {
		roleName = getTaskProperty(PROPERTY_XAMPLER_ROLE).toLowerCase();
		File omniorbRoot = new File(getTaskProperty(PROPERTY_OMNIORB_ROOT));
		ldLibraryPath = new File(omniorbRoot,"lib").getAbsolutePath();
		taskRole = parseTaskRole(roleName);
		File xamplerRoot = new File(getTaskProperty(PROPERTY_XAMPLER_ROOT));
		suiteDir = new File(xamplerRoot,"C++/_Suites/" + getTaskProperty(PROPERTY_SUITE_PATH)).getAbsolutePath();
		
		clientExecutable = new File(suiteDir, "Client").getAbsolutePath();
		serverExecutable = new File(suiteDir, "Server").getAbsolutePath();
		
		/* chmod executable */
		try {
			if (new File(clientExecutable).exists()) {
				Chmod.chmod(clientExecutable, "ugo+rx");
			}
			if (new File(serverExecutable).exists()) {
				Chmod.chmod(serverExecutable, "ugo+rx");
			}
		} catch (AntTaskException e) {
			throw new TaskException("Error chmoding Xampler executable", e);
		}
		 
		// parse command line arguments
		serverRuntimeParams = StringUtils.split(getTaskProperty(PROPERTY_SERVER_PARAMS), " ");
		clientRuntimeParams = StringUtils.split(getTaskProperty(PROPERTY_CLIENT_PARAMS), " ");
	}
	
	private void waitForIOR() throws TaskException {
		File iorFile = new File(getTempDirectory(), XAMPLER_IOR_FILE);
		 
		// wait until the IOR file is created by the server process
		int i = 0;
		for (i = 0; i < IOR_WAIT_DELAY; i++) {
			if (iorFile.exists() && iorFile.length() > 50) {
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logFatal("Interrupted");
					throw new TaskException("Interrupted");
				}
			}
		}
		
		if (i >= IOR_WAIT_DELAY) {
			throw new TaskException("Xampler server didn't start in allowed interval (" + IOR_WAIT_DELAY + "s)");
		}
	}
	
	private void reportIOR() throws TaskException {
		File iorFile = new File(getTempDirectory(), XAMPLER_IOR_FILE);
	
		waitForIOR();
		
		try {
			BufferedReader rd = new BufferedReader(new FileReader(iorFile));
			String iorValue = rd.readLine();
			rd.close();
			if (iorValue.length() == 0){
				logFatal("Corrupted IOR file encountered");
				throw new TaskException("Corrupted IOR file encountered");
			}
			checkPointReached(CHECKPOINT_SERVER_STARTED, iorValue);
		} catch (IOException e) {
			logFatal("I/O error occured when reading " + XAMPLER_IOR_FILE);
			throw new TaskException(XAMPLER_IOR_FILE + " not found", e);
		}
		
	}
	
	private void createIOR() throws TaskException {
		try {
			String ior = (String) getTasksPort().checkPointWait(
					null, // we are waiting for server in current context
					getTaskProperty(PROPERTY_SERVER_TID), 
					CHECKPOINT_SERVER_STARTED, 0 );
			File iorFile = new File(getTempDirectory(), XAMPLER_IOR_FILE);
			BufferedWriter wr = new BufferedWriter(new FileWriter(iorFile));
			
			if (ior == null) {
				throw new TaskException("Waiting for IOR reference timed out.");
			}
			wr.write(ior);
			wr.flush();
			wr.close();
			logInfo("Created Xampler.IOR: " + iorFile.length() + " bytes");
		} catch (IOException e) {
			throw new TaskException("Can't create IOR reference file", e);
		}
	}
	

	@Override
	protected void run() throws TaskException {
		
		setup();
		
		if (!simulate) {
			if (taskRole.equals(TaskRole.SERVER) || taskRole.equals(TaskRole.CLIENT)) {
				/* we are running in SERVER or CLIENT mode */
				boolean isServer = taskRole.equals(TaskRole.SERVER);
				
				logInfo("Starting executable: " + (isServer ? serverExecutable : clientExecutable));
				logInfo("Suite: " + getTaskProperty(PROPERTY_SUITE_PATH));
				
				Process proc = null;
				try {
					// create file with IOR iff running as client
					if (!isServer) {
						createIOR();
					}

					// fire xampler process
					if (isServer) {
						proc = fireXamplerServer();
					} else {
						proc = fireXamplerClient();
					}

					// when running as server, report IOR as value in checkpoint
					if (isServer) {
						reportIOR();
					}

					int result = proc.waitFor();
					if (isServer) {
						serverStdOutReader.join ();
						serverStdErrReader.join ();
					} else {
						clientStdOutReader.join ();
						clientStdErrReader.join ();
					}
					
					if (result != 0) {
						purgeClientOutFile();	// make sure client.out file is gone
						throw new TaskException("Xampler process exited with exitcode " + result);
					}

				} catch (InterruptedException e) {
					logFatal("Interrupted by external process - killing Xampler process");
					if (proc != null) {
						proc.destroy();
					}
					throw new TaskException("Interrupted by external signal");
				} catch (TaskException e) {
					logFatal("Xampler execution failed: " + e.getMessage());
					logFatal("Killing Xampler process");
					if (proc != null) {
						proc.destroy();
					}
					throw e;
				} finally {
					// stop reader threads in any case

					if (clientStdOutReader.isAlive()) {
						clientStdOutReader.interrupt();
					}
					if (clientStdErrReader.isAlive()) {
						clientStdErrReader.interrupt();
					}
					
					if (serverStdOutReader.isAlive()) {
						serverStdOutReader.interrupt();
					}
					if (serverStdErrReader.isAlive()) {
						serverStdErrReader.interrupt();
					}
				}
				
				
			} else {
				/* we are running in BOTH mode */
				
				Process serverProc = null;
				Process clientProc = null;
				try {
					// fire xampler server
					serverProc = fireXamplerServer();
					
					/* wait for IOR here */
					waitForIOR();
					
					// fire xampler client
					clientProc = fireXamplerClient();

					int serverResult = serverProc.waitFor();
					int clientResult = clientProc.waitFor();
					
					serverStdOutReader.join ();
					serverStdErrReader.join ();

					clientStdOutReader.join ();
					clientStdErrReader.join ();
					
					if (serverResult != 0 ) {
						logFatal("Xampler server process exited with exitcode " + serverResult);	
					}					
					if (clientResult != 0 ) {
						logFatal("Xampler client process exited with exitcode " + clientResult);	
					}
				
					if (serverResult != 0 || clientResult != 0) {
						purgeClientOutFile();	// make sure client.out file is gone
						throw new TaskException("Xampler server or client exited with nonzero exitcode.");
					}

				} catch (InterruptedException e) {
					logFatal("Interrupted by external process - killing Xampler processes");
					if (serverProc != null) { serverProc.destroy(); }								// 'if' avoids stupid warnings.
					if (clientProc != null) { clientProc.destroy(); }								// 'if' avoids stupid warnings.
					throw new TaskException("Interrupted by external signal");
				} catch (TaskException e) {
					logFatal("Xampler execution failed: " + e.getMessage());
					logFatal("Killing Xampler processes");
					if (serverProc != null) {
						serverProc.destroy();
					}
					if (clientProc != null) {
						clientProc.destroy();
					}
					throw e;
				} finally {
					// stop reader threads in any case

					if (clientStdOutReader.isAlive()) {
						clientStdOutReader.interrupt();
					}
					if (clientStdErrReader.isAlive()) {
						clientStdErrReader.interrupt();
					}
					
					if (serverStdOutReader.isAlive()) {
						serverStdOutReader.interrupt();
					}
					if (serverStdErrReader.isAlive()) {
						serverStdErrReader.interrupt();
					}
				}
				
			}
		} else {
			if (!taskRole.equals(TaskRole.SERVER)) {
				File clientOut = new File(getWorkingDirectory(), "client.out");
				try {
					clientOut.createNewFile();
				} catch (IOException e) {
					throw new TaskException("Could not create fake client.out file.");
				}
			} else {
				checkPointReached(CHECKPOINT_SERVER_STARTED, "simulated xampler execute");
			}
		}
	}

	/**
	 * Deletes client.out file, if it exists. 
	 */
	private void purgeClientOutFile() {
		File clientOut = new File(getWorkingDirectory(), "client.out");
		if (clientOut.exists()) {
			clientOut.delete();
		}
	}

	@Override
	protected void checkRequiredProperties() throws TaskException {
		// required properties
		final String requiredProperties[] = new String[] {
				PROPERTY_XAMPLER_ROLE,
				PROPERTY_XAMPLER_ROOT,
				PROPERTY_OMNIORB_ROOT,
				PROPERTY_SUITE_PATH,
		};
		
		checkRequiredProperties(requiredProperties);
		
		// role : client or server
		String role = getTaskProperty(PROPERTY_XAMPLER_ROLE).toLowerCase();
		
		
		if (!"server".equals(role) && !"client".equals(role) && !"both".equals(role)) {
			throw new TaskException("Property '" + PROPERTY_XAMPLER_ROLE + "' must be one of  'client' or 'server' or 'both'");
		}
		
		// when running as client, server TID must be specified 
		if ("client".equals(role)) {
			if (StringUtils.isEmpty(getTaskProperty(PROPERTY_SERVER_TID))) {
				throw new TaskException("Required property '" + PROPERTY_SERVER_TID + "' not set");
			}
		}
		
		simulate = ( getTaskProperty(SIMULATE) != null);
		
	}
	
	private Process fireXamplerServer() throws TaskException {
		
		Process proc = null;

		ProcessBuilder pb = new ProcessBuilder();
		if (serverRuntimeParams.length == 0) {
			pb.command(new String[]{serverExecutable});
		} else {
			String[] cmd = new String[serverRuntimeParams.length + 1];
			System.arraycopy(serverRuntimeParams, 0, cmd, 1, serverRuntimeParams.length);
			cmd[0] = serverExecutable;
			for (String c : cmd) {
				logDebug("'" + c + "'");
			}
			pb.command(cmd);
		}
		Map <String, String> env = pb.environment();
		String LD_LIBPATH = env.get("LD_LIBRARY_PATH");
		env.put("LD_LIBRARY_PATH", ldLibraryPath + ":" + LD_LIBPATH);
		pb.directory(new File(getTempDirectory()));

		try {
			serverStdOutReader.setOutputStream(
					new FileOutputStream(new File(getWorkingDirectory(), "server.out")));
			serverStdErrReader.setOutputStream(
					new FileOutputStream(new File(getWorkingDirectory(), "server.err")));
			proc = pb.start();
		} catch (FileNotFoundException e) {
			throw new TaskException("Can't create output file", e);
		} catch (IOException e) {
			throw new TaskException("Can't start xampler server process", e);
		}

		serverStdOutReader.setInputStream(proc.getInputStream());
		serverStdOutReader.start();
		
		serverStdErrReader.setInputStream(proc.getErrorStream());
		serverStdErrReader.start();

		return proc;
	}
	
	private Process fireXamplerClient() throws TaskException {
		
		Process proc = null;

		ProcessBuilder pb = new ProcessBuilder();
		if (clientRuntimeParams.length == 0) {
			pb.command(new String[]{clientExecutable});
		} else {
			String[] cmd = new String[clientRuntimeParams.length + 1];
			System.arraycopy(clientRuntimeParams, 0, cmd, 1, clientRuntimeParams.length);
			cmd[0] = clientExecutable;
			for (String c : cmd) {
				logDebug("'" + c + "'");
			}
			pb.command(cmd);
		}
		Map <String, String> env = pb.environment();
		String LD_LIBPATH = env.get("LD_LIBRARY_PATH");
		env.put("LD_LIBRARY_PATH", ldLibraryPath + ":" + LD_LIBPATH);
		pb.directory(new File(getTempDirectory()));

		try {
			clientStdOutReader.setOutputStream(
					new FileOutputStream(new File(getWorkingDirectory(), "client.out")));
			clientStdErrReader.setOutputStream(
					new FileOutputStream(new File(getWorkingDirectory(), "client.err")));
			proc = pb.start();
		} catch (FileNotFoundException e) {
			throw new TaskException("Can't create output file", e);
		} catch (IOException e) {
			throw new TaskException("Can't start xampler client process", e);
		}

		clientStdOutReader.setInputStream(proc.getInputStream());
		clientStdOutReader.start();
		
		clientStdErrReader.setInputStream(proc.getErrorStream());
		clientStdErrReader.start();

		return proc;
	}
	
}




