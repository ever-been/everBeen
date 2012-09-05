/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.task.detector;

import java.io.File;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.SimpleDateFormat;

import java.util.Date;

import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.common.util.MiscUtils;

import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * This class acts as an adapter to the native libraries that are provided with the Detector Task.
 * It will attempt to initialize correct library and query data about the host. If an error occured
 * while initializing native library or if no native library has been found for current platform,
 * only primitive Java implementation of the detector will be used.
 *
 * @author Branislav Repcek
 */
public class NativeDetector {

	/**
	 * Initialize native library.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> otherwise.
	 */
	private native boolean nativeInitialize();

	/**
	 * Execute native detector which will collect all data.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> otherwise. It should always return 
	 *         <code>true</code> since native detectors are written so that in case of error they can collect data
	 *         not affected by given error.
	 */
	private native boolean nativeExecute();

	/**
	 * Get data from the native detector as String.
	 * 
	 * @return Detector data in string. This is XML file data converted to string.
	 */
	private native String nativeGetData();

	/**
	 * Get messages produced by the native detector. These messages may contain info about errors in the native
	 * library or successful operations. 
	 * 
	 * @return String containing all messages. Each message is formated on one line.
	 */
	private native String nativeGetMessages();
	
	/**
	 * Free data used by the native library.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> otherwise. 
	 */
	private native boolean nativeDestroy();
	
	/**
	 * Get encoding used in native library.
	 * 
	 * @return String with name of the encoding (eg. utf-8, windows-1252...).
	 */
	private native String nativeGetEncoding();

	/**
	 * Data collected by the native library.
	 */
	private String data;

	/**
	 * Native library message string.
	 */
	private String messages;

	/**
	 * <code>true</code> if native library succeeded.
	 */
	private boolean success;

	/**
	 * Flag which specifies whether detection will be done using native libraries.
	 */
	private boolean usingNative;
	
	/**
	 * Character encoding used in native library.
	 */
	private String encoding;
	
	/**
	 * Task we are called from.
	 */
	private Task task;

	/**
	 * Constructor. This will attempt to load native library based on the Operating System on which it is run.
	 * If corresponding native library is not found or initialization fails, using_native flag will be set to
	 * false.
	 */
	public NativeDetector() {

		task = CurrentTaskSingleton.getTaskHandle();
		
		data = new String("");
		messages = new String("");

		// Which OS are we running on?
		String sysname = System.getProperty("os.name").toLowerCase();
		
		// Find out where we are running.
		String libraryPath = System.getProperty("user.dir") + File.separatorChar;
		task.logTrace("Library path: " + libraryPath);

		try {
			// now we try to load native libraries
			
			if (sysname.indexOf("windows") != -1) {
				// we are running on Windows.
				task.logDebug("Attempting to load Windows native detector library.");
				
				System.load(libraryPath + "HWDet3.wnd");
				
				task.logDebug("Link successful.");
				usingNative = true;
			} else if (sysname.indexOf("linux") != -1) {
				// we are running on Linux.
				task.logDebug("Attempting to load Linux native detector library.");
				
				System.load(libraryPath + "HWDet3.lnd");
				
				task.logDebug("Link successful.");
				usingNative = true;
			} else if (sysname.indexOf("solaris") != -1) {
				// we are running on Solaris.
				task.logDebug("Attempting to load Solaris native detector library.");
				
				System.load(libraryPath	+ "HWDet3.snd");
				
				task.logDebug("Link successful.");
				usingNative = true;
			} else {
				// We are running on OS which does not have native datector.
				usingNative = false;
			}
		} catch (Exception e) {
			usingNative = false;
			task.logError("Unable to load native detector library, message: \"" + e.getMessage() + "\".");
		} catch (Error e) {
			usingNative = false;
			task.logError("Unable to load native detector library, message: \"" + e.getMessage() + "\".");
		}
	}

	/**
	 * Do all the work - call native methods or detect everything with Java and compose resulting data.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 */
	public boolean execute() {

		String nativeData;

		if (usingNative) {
			// we are using native libs so initialize them...
			
			task.logDebug("Initializing native detector.");
			
			boolean init = nativeInitialize();
			
			if (init) {
				task.logDebug("Native initialization successful.");
			} else {
				task.logError("Unable to initialize native library.");
				messages = nativeGetMessages();
				nativeDestroy();
				return false;
			}
						
			task.logDebug("Executing native detector.");
			
			// ...detect all we can... 
			boolean exec = nativeExecute();
			
			if (exec) {
				task.logDebug("Native detector finished successfully.");
			} else {
				task.logError("Unable to collect data from the library.");
				messages = nativeGetMessages();
				nativeDestroy();
				return false;
			}
			
			// ...retrieve data collected...
			nativeData = nativeGetData();
			
			// ...and finally get any error reports we may have generated in native code.
			messages = nativeGetMessages();
			
			// get encoding in the native lib
			encoding = nativeGetEncoding();
			
			// Now free the resources.
			nativeDestroy();

			success = true;
		} else {
			// No native lib for current os, we will use Java to get very limited data.

			encoding = "UTF-16";
			
			Date date = new Date();
			nativeData = new String();

			nativeData = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
			           + "<hostInfo>\n"
			           + makeXMLElement("hostName", MiscUtils.getCanonicalLocalhostName(), 1)
			           + makeXMLElement("lastCheckDate", formatDate(date, "yyyy/MM/dd"), 1)
			           + makeXMLElement("lastCheckTime", formatDate(date, "HH:mm.ss"),	1)
			           + makeXMLElement("detector", HostInfoInterface.Detectors.GENERIC.toString(), 1)
			           + "\t<operatingSystem>\n"
			           + "\t\t<basicInfo>\n" 
			           + makeXMLElement("name", System.getProperty("os.name"), 3) 
			           + makeXMLElement("vendor", "unknown", 3) 
			           + makeXMLElement("arch", System.getProperty("os.arch"), 3) 
			           + "\t\t</basicInfo>\n" 
			           + "\t\t<advancedInfo>\n" 
			           + makeXMLElement("version", System.getProperty("os.version"), 3) 
			           + "\t\t</advancedInfo>\n" 
			           + "\t</operatingSystem>\n" 
			           + "\t<network>\n" 
			           + "\t</network>\n" 
			           + "\t<memory>\n" 
			           + "\t\t<physicalMemorySize>0</physicalMemorySize>\n" 
			           + "\t\t<virtualMemorySize>0</virtualMemorySize>\n" 
			           + "\t\t<swapSize>0</swapSize>\n" 
			           + "\t\t<pagingFileSize>0</pagingFileSize>\n"
			           + "\t</memory>\n" 
			           + "\t<processors>\n" 
			           + "\t</processors>\n" 
			           + "\t<installedProducts>\n"
			           + "\t</installedProducts>\n" 
			           + "\t<diskDrives>\n" 
			           + "\t</diskDrives>\n" 
			           + "\t<beenDisk>\n" 
			           + makeXMLElement("path", System.getenv("BEEN_HOME"), 2) 
			           + makeXMLElement("size", "-1", 2) 
			           + makeXMLElement("freeSpace", "-1", 2) 
			           + "\t</beenDisk>\n";

			success = true;
		}

		// detect java info
		String javaVersion = System.getProperty("java.version");
		String javaVendor = System.getProperty("java.vendor");
		String javaRuntimeName = System.getProperty("java.runtime.name");
		String javaVMVersion = System.getProperty("java.vm.version");
		String javaVMVendor = System.getProperty("java.vm.vendor");
		String javaRuntimeVersion = System.getProperty("java.runtime.version");
		String javaSpecificationVersion = System.getProperty("java.specification.version");

		String javaInfo = "\t<javaInfo>\n"
		                + makeXMLElement("version", javaVersion, 2)
		                + makeXMLElement("vendor", javaVendor, 2)
		                + makeXMLElement("runtimeName", javaRuntimeName, 2)
		                + makeXMLElement("vmVersion", javaVMVersion, 2)
		                + makeXMLElement("vmVendor", javaVMVendor, 2) 
		                + makeXMLElement("runtimeVersion", javaRuntimeVersion, 2) 
		                + makeXMLElement("specification", javaSpecificationVersion, 2) 
		                + "\t</javaInfo>\n";

		if (usingNative) {
			// If using native libs, insert JavaInfo before the closing tag. 
			int pos = nativeData.indexOf("</hostInfo>");

			data = nativeData.substring(0, pos) + javaInfo	+ nativeData.substring(pos);
			
			/* This is not very nice, since we replace data from native detector with the data
			 * detected by Java, but it is needed for consistency.
			 */
			int hnPos1 = data.indexOf("<hostName>");
			int hnPos2 = data.indexOf("</hostName>");
			
			try {
				data = data.substring(0, hnPos1)
				     + "<hostName>"
				     + InetAddress.getLocalHost().getCanonicalHostName()
				     + data.substring(hnPos2);
			} catch (UnknownHostException e) {
				task.logError("Unknown host in HAX!");
			}
		} else {
			// Append JavaInfo and closing tag.
			data = nativeData + javaInfo + "</hostInfo>";
		}
		
		return success;
	}

	/**
	 * Get message string produced by native libraries.
	 * 
	 * @return Message string generated by native library.
	 */
	public String getMessageString() {

		return messages;
	}

	/**
	 * Get data collected.
	 * 
	 * @return Get all data collected during call to execute() method.
	 */
	public String getDataString() {

		return data;
	}
	
	/**
	 * Get character encoding used in native library.
	 *  
	 * @return String with encoding name.
	 */
	public String getEncoding() {
		
		return encoding;
	}

	/**
	 * Create string representing XML element with given name and value at the given column (columns are tabs).
	 * 
	 * @param name Name of the element to create.
	 * @param value Value fo the element.
	 * @param pos Number of tabs before the opening angle bracket of the element.
	 * @return String representing XML element.
	 */
	private static String makeXMLElement(String name, String value, int pos) {

		String s = new String();

		for (int i = 0; i < pos; ++i) {
			s += "\t";
		}

		return new String(s + "<" + name + ">" + value + "</" + name + ">\n");
	}

	/**
	 * Format date according to given format string.
	 * 
	 * @param date Date to format.
	 * @param format Format string.
	 * @return Date in requested format. 
	 */
	private static String formatDate(Date date, String format) {

		SimpleDateFormat formatter = new SimpleDateFormat(format);

		return formatter.format(date);
	}
}
