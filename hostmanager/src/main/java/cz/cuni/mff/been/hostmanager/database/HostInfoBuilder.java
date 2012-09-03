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

package cz.cuni.mff.been.hostmanager.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.InputSource;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;


/**
 * This class allows you to construct instance of HostInfoInterface from data stored in file or save
 * such data into your own file.
 *
 * @author Branislav Repcek
 */
public class HostInfoBuilder {

	/**
	 * Read host's data from the file.
	 * 
	 * @param fileName Name of the input file.
	 * 
	 * @return Instance of HostInfoInterface which contains data from the file.
	 * 
	 * @throws FileNotFoundException If given file has not been found.
	 * @throws InputParseException If an error occurred while parsing input file.
	 */
	public static HostInfoInterface readFromFile(String fileName) 
		throws FileNotFoundException, InputParseException {
		
		FileInputStream inputStream = new FileInputStream(fileName);
		
		HostInfo hi = new HostInfo(new InputSource(inputStream));
		
		return hi;
	}
	
	/**
	 * Read host's data from the input source.
	 * 
	 * @param input InputSource containing host's data.
	 * 
	 * @return Instance of HostInfoInterface which contains data from the input.
	 * 
	 * @throws InputParseException If an error occurred while parsing input.
	 */
	public static HostInfoInterface readFromInputSource(InputSource input) throws InputParseException {
	
		HostInfo hi = new HostInfo(input);
		
		return hi;
	}
	
	/**
	 * Save host's data to the XML file.
	 * 
	 * @param fileName Name of the output file. File will be overwritten if it already exists.
	 * @param hostInfo Data to be saved.
	 * 
	 * @throws IOException If an error occurred while writing data.
	 */
	public static void saveToFile(String fileName, HostInfoInterface hostInfo) throws IOException {
		
		try {
			XMLHelper.saveXMLSerializable(hostInfo, fileName, true, "UTF-16");
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * Empty ctor.
	 */
	private HostInfoBuilder() {
	}
}
