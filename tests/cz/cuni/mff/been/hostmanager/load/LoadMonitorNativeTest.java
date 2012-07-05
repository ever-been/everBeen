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

package cz.cuni.mff.been.hostmanager.load;

import static org.junit.Assert.fail;

import java.io.File;
import java.text.SimpleDateFormat;

import org.junit.Test;

import cz.cuni.mff.been.hostmanager.util.TimeUtils;

/**
 * Simple test for the interface to the Load Monitor native library. This will write cool table
 * with the sample data.
 *
 * @author Branislav Repcek
 */
public class LoadMonitorNativeTest {
	
	public LoadMonitorNativeTest() {
	}
	
	@Test
	public void runTest() {
		// LoadMonitorNativeTest.main( null );
	}

	/**
	 * Sampling interval (delay between two samplings) in milliseconds. 
	 */
	private static final int SAMPLING_INTERVAL = 1000;
	
	/**
	 * Number of samples to take.
	 */
	private static final int TOTAL_SAMPLES = 100;

	/**
	 * Align value in the string.
	 * 
	 * @param <T> Type of the value.
	 * 
	 * @param value Value to convert to the string.
	 * @param length target length of the result.
	 * 
	 * @return String which has given length (at least) and value is aligned in the center.
	 */
	private static < T > String alignCenter(T value, int length) {
		
		String result = "";
		String vs = String.valueOf(value);
		
		for (int i = 0; i < (length - vs.length()) / 2; ++i) {
			result += " ";
		}
		
		result += vs;
		
		while (result.length() < length) {
			
			result += " ";
		}
		
		return result;
	}
	
	/**
	 * @param args Command-line arguments. Ignored.
	 */
	public static void main(String[] args) {

		// Path to the library.
		String libPath = System.getenv("BEEN_HOME") + File.separator
		                 + "native" + File.separator 
		                 + "monitor" + File.separator 
		                 + "bin" + File.separator;
		
		LoadMonitorNative monitor = new LoadMonitorNative(libPath);
		
		monitor.initialize();
		
		System.out.println();
		
		HardwareDescription desc = monitor.getHardwareDescription();

		// At first, write names of stuff we are monitoring.
		System.out.println("Hardware description:");
		System.out.println(" Network adapters:");
		
		for (int i = 0; i < desc.getAdapterCount(); ++i) {
			System.out.println("  Adapter[" + i + "]=\"" + desc.getAdapters()[i] + "\"");
		}

		System.out.println(" Drives:");
		for (int i = 0; i < desc.getDriveCount(); ++i) {
			System.out.println("  Drive[" + i + "]=\"" + desc.getDrives()[i] + "\"");
		}
		
		System.out.println();
		
		// Now count network interfaces and drives and processors.
		// Note that this is very simple program that assumes, that number of drives/processors/adapters
		// will not change while it is running. It will not crash, but it will screw formatting of
		// the table (of course).
		LoadSample tmpSample = monitor.getSample();
		int networkCount = tmpSample.getNetworkInterfaceCount();
		int driveCount = tmpSample.getDriveCount();
		int cpuCount = tmpSample.getProcessorCount();
		
		// Write table header.
		System.out.print(alignCenter("time", 20) + "\t"                    // Sample time.
				         + alignCenter("TSC", 20) + "\t"                   // CPU TSC.
				         + alignCenter("CPUs", 5) + "\t");                 // Number of CPUs.

		for (int i = 0; i < cpuCount; ++i) {
			System.out.print(alignCenter("CPU" + i, 5) + "\t");            // Usage of each CPU.
		}

		System.out.print(alignCenter("Memory", 15) + "\t"                  // Free memory (B).
				         + alignCenter("Proc", 6) + "\t"                   // Number of processes.
				         + alignCenter("Queue", 6) + "\t"                  // Processor queue length.
				         + alignCenter("Nets", 5) + "\t");                 // Number of network adapters.
				
		for (int i = 0; i < networkCount; ++i) {
			System.out.print(alignCenter("Net" + i + "+", 10) + "\t"       // Receive speed of the adapter (B).
					         + alignCenter("Net" + i + "-", 10) + "\t");   // Send speed of the adapter (B).
		}

		System.out.print(alignCenter("Drv", 5) + "\t");                    // Number of disk drives.
		
		for (int i = 0; i < driveCount; ++i) {
			System.out.print(alignCenter("Drv" + i + "R", 10) + "\t"       // Read speed of the drive (B).
			                 + alignCenter("Drv" + i + "W", 10) + "\t");   // Write speed of the drive (B).
		}
		
		System.out.println();
		
		// This is how we format time.
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		
		// Take some samples and write them to the stdout.
		for (int i = 0; i < TOTAL_SAMPLES; ++i) {
			LoadSample sample = monitor.getSample();

			if (sample == null) {
				// Uh-oh, null sample.
				System.out.println("**** null ****");
				fail("Load sample is null.");
				continue;													// Just suppressing warnings.
			}
			
			String ts = timeFormat.format(TimeUtils.convertWindowsTimeToJavaDate(sample.getTimeStamp()));
			
			// Now write one line of the table.
			System.out.print(alignCenter(ts, 20) + "\t"
			                 + alignCenter(sample.getTSC(), 20) + "\t"
			                 + alignCenter(sample.getProcessorCount(), 5) + "\t");
			
			for (int j = 0; j < sample.getProcessorCount(); ++j) {
				System.out.print(alignCenter(sample.getProcessorUsage()[j], 5) + "\t");
			}
			
			System.out.print(alignCenter(sample.getMemoryFree(), 15) + "\t"
			                 + alignCenter(sample.getProcessCount(), 6) + "\t"
			                 + alignCenter(sample.getProcessQueueLength(), 6) + "\t"
			                 + alignCenter(sample.getNetworkInterfaceCount(), 5) + "\t");
			
			for (int j = 0; j < sample.getNetworkInterfaceCount(); ++j) {
				System.out.print(alignCenter(sample.getNetworkBytesReceivedPerSecond()[j], 10) + "\t"
				                 + alignCenter(sample.getNetworkBytesSentPerSecond()[j], 10) + "\t");
			}
			
			System.out.print(alignCenter(sample.getDriveCount(), 5) + "\t");
			
			for (int j = 0; j < sample.getDriveCount(); ++j) {
				System.out.print(alignCenter(sample.getDiskReadBytesPerSecond()[j], 10) + "\t"
				                 + alignCenter(sample.getDiskWriteBytesPerSecond()[j], 10) + "\t");
			}
			
			System.out.println();
			try {
				Thread.sleep(SAMPLING_INTERVAL);
			} catch (Exception e) {
				// nooooo!
			}
		}
		
		// Shut down the thing.
		monitor.terminate();
	}
}
