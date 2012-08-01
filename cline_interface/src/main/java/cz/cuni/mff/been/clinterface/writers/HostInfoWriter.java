/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface.writers;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.hostmanager.database.DiskDrive;
import cz.cuni.mff.been.hostmanager.database.DiskPartition;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NetworkAdapter;
import cz.cuni.mff.been.hostmanager.database.OperatingSystem;
import cz.cuni.mff.been.hostmanager.database.Processor;

/**
 * A Writer that outputs data from HostInfoInterface instances.
 * 
 * @author Andrej Podzimek
 */
public final class HostInfoWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public HostInfoWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Basic output with default data fields.
	 * 
	 * @param hostInfo The host info to write out.
	 * @throws IOException
	 */
	public void sendLinePlain( HostInfoInterface hostInfo ) throws IOException {
		builder()
		.append( hostInfo.getHostName() ).append( ' ' )
		.append( quotedLiteral( hostInfo.getCheckDateTime() ) ).append( ' ' )
		.append( quotedLiteral( hostInfo.getOperatingSystem().getName() ) ).append( ' ' )
		.append( hostInfo.getOperatingSystem().getFamily() ).append( ' ' )
		.append( hostInfo.getOperatingSystem().getArchitecture() ).append( ' ' )
		.append( hostInfo.getProcessorCount() ).append( ' ' )
		.append( hostInfo.getMemory().getPhysicalMemorySize() ).append( ' ' )
		.append( hostInfo.getMemory().getSwapSize() ).append( ' ' )
		.append( hostInfo.getDriveCount() ).append( ' ' )
		.append( hostInfo.getNetworkAdapterCount() ).append( '\n' );
		sendOut();
	}
	
	/**
	 * Outputs all the lines of history.
	 * 
	 * @param history The hisory dates to output.
	 * @param makeGap Whether to write an empty line.
	 * @throws IOException When it rains.
	 */
	public void sendLinesHistory( Date[] history, boolean makeGap ) throws IOException {
		DateFormat dateFormat;
		
		if ( makeGap ) { builder().append( '\n' ); }
		dateFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT );
		for ( Date historyDate : history ) {
			builder().append( dateFormat.format( historyDate ) ).append( '\n' );
		}
		sendOut();
	}
	
	/**
	 * Outputs a line with operating system information.
	 * 
	 * @param system An instance of operating system metadata.
	 * @param makeGap Whether to write an empty line.
	 * @throws IOException When it rains.
	 */
	public void sendLineSystem( OperatingSystem system, boolean makeGap ) throws IOException {
		if ( makeGap ) { builder().append( '\n' ); }
		builder()
		.append( quotedLiteral( system.getName() ) ).append( ' ' )
		.append( system.getFamily() ).append( ' ' )
		.append( system.getArchitecture() ).append( ' ' )
		.append( quotedLiteral( system.getVendor() ) ).append( '\n' );
		sendOut();
	}

	/**
	 * Outputs all the lines of CPU information.
	 * 
	 * @param hostInfo An instance of host info to obtain CPU records from.
	 * @param makeGap Whether to write an empty line.
	 * @throws IOException When it rains.
	 */
	public void sendLinesCPU( HostInfoInterface hostInfo, boolean makeGap ) throws IOException {
		Processor processor;
		int count;
			
		if ( makeGap ) { builder().append( '\n' ); }
		count = hostInfo.getProcessorCount();
		for ( int i = 0; i < count; ++i ) {
			processor = hostInfo.getProcessor( i );
			builder()
			.append( quotedLiteral( processor.getVendorName() ) ).append( ' ' )
			.append( quotedLiteral( processor.getModelName() ) ).append( ' ' )
			.append( processor.getSpeed() ).append( ' ' )
			.append( processor.getCacheSize() ).append( '\n' );
		}
		sendOut();
	}
	
	/**
	 * Outputs all the lines of storage device information.
	 * 
	 * @param hostInfo An instance of host info to obtain storage device records from.
	 * @param makeGap Whether to write an empty line.
	 * @throws IOException When it rains.
	 */
	public void sendLinesStorage( HostInfoInterface hostInfo, boolean makeGap ) throws IOException {
		DiskDrive diskDrive;
		DiskPartition diskPartition;
		int driveCount, partitionCount;
			
		if ( makeGap ) { builder().append( '\n' ); }
		driveCount = hostInfo.getDriveCount();
		for ( int i = 0; i < driveCount; ++i ) {
			diskDrive = hostInfo.getDiskDrive( i );
			builder()
			.append( diskDrive.getDeviceName() ).append( ' ' )
			.append( diskDrive.getMediaType() ).append( ' ' )
			.append( quotedLiteral( diskDrive.getModelName() ) ).append( ' ' )
			.append( diskDrive.getSize() ).append( ' ' );
			partitionCount = diskDrive.getPartitionCount();
			if ( partitionCount > 0 ) {
				diskPartition = diskDrive.getPartition( 0 );
				builder().append( '|' );
				builder()
				.append( diskPartition.getDeviceName() ).append( ' ' )
				.append( quotedLiteral( diskPartition.getName() ) ).append( ' ' )
				.append( diskPartition.getFileSystemName() ).append( ' ' )
				.append( diskPartition.getFreeSpace() ).append( ' ' )
				.append( diskPartition.getSize() );
				for ( int j = 1; j < partitionCount; ++j ) {
					diskPartition = diskDrive.getPartition( j );
					builder()
					.append( ',' )
					.append( diskPartition.getDeviceName() ).append( ' ' )
					.append( quotedLiteral( diskPartition.getName() ) ).append( ' ' )
					.append( diskPartition.getFileSystemName() ).append( ' ' )
					.append( diskPartition.getFreeSpace() ).append( ' ' )
					.append( diskPartition.getSize() );
				}
				builder().append( '|' );
			}
			builder().append( '\n' );
		}
		sendOut();
	}
	
	/**
	 * Outputs all the lines of network device information.
	 * 
	 * @param hostInfo An instance of host info to obtain network device records from.
	 * @param makeGap Whether to write an empty line.
	 * @throws IOException When it rains.
	 */
	public void sendLinesNetwork( HostInfoInterface hostInfo, boolean makeGap ) throws IOException {
		NetworkAdapter networkAdapter;
		int count;
		
		if ( makeGap ) { builder().append( '\n' ); }
		count = hostInfo.getNetworkAdapterCount();
		for ( int i = 0; i < count; ++i ) {
			networkAdapter = hostInfo.getNetworkAdapter( i );
			builder()
			.append( networkAdapter.getName() ).append( ' ' )
			.append( networkAdapter.getType() ).append( ' ' )
			.append( quotedLiteral( networkAdapter.getVendor() ) ).append( ' ' )
			.append( networkAdapter.getMacAddress() ).append( '\n' );
		}
		sendOut();
	}	
}
