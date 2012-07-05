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

import java.rmi.Naming;
import java.util.List;

import org.junit.Test;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * Output some statistics about the computer.
 *
 * @author Branislav Repcek
 */
public class StatsTest {
	
	public StatsTest() {
	}

	@Test
	public void startTest() {
		// StatsTest.main( null );
	}
	
	/**
	 * Event counter.
	 */
	private static int eventCounter = 0;
	
	/**
	 * Write event to the stdout.
	 * 
	 * @param event Event to write.
	 * @param shortFormat If set to <tt>false</tt> all properties of the event will be output 
	 *        (including sample and hw description data if present). If set to <tt>false</tt> only
	 *        short description of the event will be output. 
	 */
	@SuppressWarnings("unused")
	private static void printEvent(LoadMonitorEvent event, boolean shortFormat) {
		
		if (!shortFormat) {
			System.out.println(Util.alignLeft("[" + (eventCounter++) + "] ", 6) + " " + event.getType().toString());
			System.out.println(Util.fill(7) + "Time: " + MiscUtils.formatDate(event.getTime()));
			System.out.println(Util.fill(7) + "Host: " + event.getHostName());
			if (event.hasSample()) {
				LoadSample sample = event.getSample();
			
				printSample(sample);
			}
			
			if (event.hasHardwareDescription()) {
				HardwareDescription desc = event.getHardwareDescription();
				
				printHwDesc(desc);
			}
		} else {
			System.out.print(Util.alignLeft("[" + (eventCounter++) + "] ", 6) + " ");
			System.out.print(Util.alignLeft(event.getType().toString(), 30));
			System.out.print(Util.alignLeft(MiscUtils.formatDate(event.getTime()), 25));
			System.out.print((event.hasSample() ? "S" : ".") + (event.hasHardwareDescription() ? "D" : "."));
			System.out.print(" {" + event.getTimestamp() + "}");
			System.out.println();
		}
	}

	/**
	 * Write sample to te stdout.
	 * 
	 * @param sample Sample to write out.
	 */
	private static void printSample(LoadSample sample) {

		System.out.println(Util.fill(7) + "Sample data:");
		System.out.println(Util.fill(7) + "Timestamp: " + sample.getTimeStamp());
		System.out.println(Util.fill(10) + "CPU usage:   "
				+ Util.join(sample.getProcessorUsage(), "%, ", 0) + "%");
		System.out.println(Util.fill(10) + "Processes: " + sample.getProcessCount());
		System.out.println(Util.fill(10) + "PQL: " + sample.getProcessQueueLength());
		System.out.println(Util.fill(10) + "Free memory: " + sample.getMemoryFree());
		System.out.println(Util.fill(10) + "Drive write: "
				+ Util.join(sample.getDiskWriteBytesPerSecond(), ", ", 8));
		System.out.println(Util.fill(10) + "Drive read:  "
				+ Util.join(sample.getDiskReadBytesPerSecond(), ", ", 8));
		System.out.println(Util.fill(10) + "Network in:  "
				+ Util.join(sample.getNetworkBytesReceivedPerSecond(), ", ", 8));
		System.out.println(Util.fill(10) + "Network out: "
				+ Util.join(sample.getNetworkBytesSentPerSecond(), ", ", 8));
	}
	
	/**
	 * Write hw description to the output.
	 * 
	 * @param desc Hardware description to output.
	 */
	private static void printHwDesc(HardwareDescription desc) {

		System.out.println(Util.fill(7) + "Hardware description:");
		System.out.println(Util.fill(10) + "Processors: " + desc.getCpuCount());
		System.out.println(Util.fill(10) + "Memory size: " + desc.getMemorySize());
		
		if (desc.getDriveCount() > 0) {
			System.out.println(Util.fill(10) + "Drives: " + desc.getDrives()[0]);
			for (int j = 1; j < desc.getDriveCount(); ++j) {
				System.out.println(Util.fill(18) + desc.getDrives()[j]);
			}
		}
		
		if (desc.getAdapterCount() > 0) {
			System.out.println(Util.fill(10) + "Adapters: " + desc.getAdapters()[0]);
			for (int j = 1; j < desc.getAdapterCount(); ++j) {
				System.out.println(Util.fill(20) + desc.getAdapters()[j]);
			}
		}
	}
	
	/**
	 * Output statistical data to the stdout.
	 *  
	 * @param stats Data to write out.
	 */
	private static void printStats(ValueStatistics stats) {
		
		System.out.println(Util.fill(3) + "Name: " + stats.getName());
		System.out.println(Util.fill(5) + "Min: " + stats.getMin());
		System.out.println(Util.fill(5) + "Max: " + stats.getMax());
		System.out.println(Util.fill(5) + "Avg: " + stats.getAverage());
		System.out.println(Util.fill(5) + "Count: " + stats.getCount());
		System.out.println(Util.fill(5) + "Lower: " + (stats.getLimitMin() == null ? "-inf" : stats.getLimitMin().toString()));
		System.out.println(Util.fill(5) + "Upper: " + (stats.getLimitMax() == null ? "+inf" : stats.getLimitMax().toString()));
	}
	
	/**
	 * @param args Command-line arguments.Ignored.
	 * 
	 * @throws Exception If an error occured.
	 */
	public static void main(String[] args) throws Exception {
		
		LoadServerInterface loadServer = 
			(LoadServerInterface) Naming.lookup(RMI.URL_PREFIX + "/been/hostmanager/load");
		
		HostDataStatisticianInterface data = 
			loadServer.getStatsProvider(MiscUtils.getCanonicalLocalhostName());
		
		List< LoadSample > samples = data.getSamples((Long) null, (Long) null);
		List< LoadMonitorEvent > events = data.getEvents((Long) null, (Long) null);
		
		System.out.println("Samples: " + samples.size());
		System.out.println("Events: " + events.size());

		// Uncomment this to output all events from the file.
//		for (LoadMonitorEvent event: events) {
//			printEvent(event, true);
//		}
		
		Long start = null;
		Long end = null;
		
		System.out.println(Util.fill(50, "*"));
		System.out.println("Memory:");
		printStats(data.getFreeMemoryStats(start, end));
		
		System.out.println(Util.fill(50, "*"));
		System.out.println("Process count:");
		printStats(data.getProcessCountStats(start, end));
		
		System.out.println(Util.fill(50, "*"));
		System.out.println("Processor queue:");
		printStats(data.getProcessQueueLengthStats(start, end));
		
		System.out.println(Util.fill(50, "*"));
		System.out.println("Processors:");
		List< ValueStatistics > cpuStats = data.getProcessorStats(start, end);
		for (ValueStatistics v: cpuStats) {
			printStats(v);
		}
		
		System.out.println(Util.fill(50, "*"));
		System.out.println("Drives:");
		List< Pair< ValueStatistics, ValueStatistics > > driveStats = data.getDriveStats(start, end);
		for (Pair< ValueStatistics, ValueStatistics > p: driveStats) {
			printStats(p.getKey());
			printStats(p.getValue());
		}

		System.out.println(Util.fill(50, "*"));
		System.out.println("Network:");
		List< Pair< ValueStatistics, ValueStatistics > > netStats = data.getNetworkStats(start, end);
		for (Pair< ValueStatistics, ValueStatistics > p: netStats) {
			printStats(p.getKey());
			printStats(p.getValue());
		}
		
		data.close();
	}
}
