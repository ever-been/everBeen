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

import java.io.File;
import java.io.FileWriter;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * View data stored in an event file. Samples can be output to the text file in nice table format.
 *
 * @author Branislav Repcek
 */
public class EventFileParser {
	
	private EventFileParser() {
	}

	/**
	 * Name of the output file into which table containing all samples will be written,
	 * If name is set to <tt>null</tt> no output file will be created.
	 */
	//private static final String FULL_TABLE_OUTPUT_FILE = "F:\\table.txt";
	private static final String FULL_TABLE_OUTPUT_FILE = null;

	/**
	 * @param args Command-line argument. First argument has to be name of the file to open.
	 * 
	 * @throws Exception If an error occured.
	 */
	public static void main(String[] args) throws Exception {

		String fileName = null;
		
		if (args.length == 0) {
			System.err.println("No filename specified on the command-line.");
		}
		
		fileName = args[0];
		
		File file = new File(fileName);
		
		System.out.println("Opening file: " + file.getAbsolutePath());
		System.out.println("File size: " + file.length());
		System.out.println();
		
		LoadFileParser< LoadMonitorEvent > fileParser = 
			new LoadFileParser< LoadMonitorEvent >(file, false, LoadMonitorEvent.class);
		
		int eventIndex = 0;
		for ( ; fileParser.hasNext(); ++eventIndex) {
		
			LoadMonitorEvent event = fileParser.getNext();
			
			System.out.println(Util.alignLeft("[" + eventIndex + "] ", 6) + " " + event.getType().toString()
					+ " {" + event.getType().ordinal() + "}");
			System.out.println(Util.fill(7) + "Time: " + MiscUtils.formatDate(event.getTime()));
			System.out.println(Util.fill(7) + "Host: " + event.getHostName());
			
			if (event.hasSample()) {
				LoadSample sample = event.getSample();
				
				System.out.println(Util.fill(7) + "Sample data:");
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
			
			if (event.hasHardwareDescription()) {
				HardwareDescription desc = event.getHardwareDescription();
				
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
		}
		
		System.out.println("Event count: " + eventIndex);
		
		if (FULL_TABLE_OUTPUT_FILE != null) {
			// Write table of all samples to the output file.
			FileWriter writer = new FileWriter(FULL_TABLE_OUTPUT_FILE);
			
			fileParser.seek(0);
			
			boolean header = false;
			
			while (fileParser.hasNext()) {
				LoadMonitorEvent event = fileParser.getNext();
				
				if (event.hasSample()) {
					if (!header) {
						writer.write(Util.alignCenter("Timestamp", 20) + "\t");
						writer.write(Util.alignCenter("Mem", 12) + "\t");
						writer.write(Util.alignCenter("PC", 6) + "\t");
						writer.write(Util.alignCenter("Q", 6) + "\t");
						for (int i = 0; i < event.getSample().getProcessorCount(); ++i) {
							writer.write(Util.alignCenter("CPU" + i, 6) + "\t");
						}
						for (int i = 0; i < event.getSample().getDriveCount(); ++i) {
							writer.write(Util.alignCenter("Drv" + i + " R", 12) + "\t");
							writer.write(Util.alignCenter("Drv" + i + " W", 12) + "\t");
						}
						for (int i = 0; i < event.getSample().getNetworkInterfaceCount(); ++i) {
							writer.write(Util.alignCenter("Net" + i + " I", 12) + "\t");
							writer.write(Util.alignCenter("Net" + i + " O", 12) + "\t");
						}
						
						writer.write("\n");
						
						header = true;
					}
					
					LoadSample sample = event.getSample();
					
					writer.write(Util.alignRight(sample.getTimeStamp(), 20) + "\t");
					writer.write(Util.alignRight(sample.getMemoryFree(), 12) + "\t");
					writer.write(Util.alignRight(sample.getProcessCount(), 6) + "\t");
					writer.write(Util.alignRight(sample.getProcessQueueLength(), 6) + "\t");
					for (short s: sample.getProcessorUsage()) {
						writer.write(Util.alignRight(s, 6) + "\t");
					}
					for (int i = 0; i < sample.getDriveCount(); ++i) {
						writer.write(Util.alignRight(sample.getDiskReadBytesPerSecond()[i], 12) + "\t");
						writer.write(Util.alignRight(sample.getDiskWriteBytesPerSecond()[i], 12) + "\t");
					}
					for (int i = 0; i < sample.getNetworkInterfaceCount(); ++i) {
						writer.write(Util.alignRight(sample.getNetworkBytesReceivedPerSecond()[i], 12) + "\t");
						writer.write(Util.alignRight(sample.getNetworkBytesSentPerSecond()[i], 12) + "\t");
					}
					
					writer.write("\n");
				}
			}
			
			writer.close();
		}
	}
}
