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
package cz.cuni.mff.been.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


/**
 * Thread that reads from an InputStream and prints it on the standard output stream
 * or the standard error stream. It chooses where to print it according to the given type
 * of the Input Stream
 * 
 * @author Jaroslav Urban
 */
public class OutputReader extends java.lang.Thread	{
	/** The input stream for reading */
	private InputStream inputStream;
	/** The output stream for writing */
	private OutputStream outputStream;
	/** Buffer size **/
	private static final int BUFFER_SIZE = 1024;
	
	/** Type of the InputStrem (stdout or stderr), you can use OutputType class for giving
	 * the type
	 */
	private OutputType inputType;
	
	public OutputReader() {
		super();
	}
	
	/**
	 * 
	 * @param is InputStream that should be read
	 * @param type type of the InputStream (stdout or stderr), you can use OutputType 
	 * class for setting the type
	 */
	public OutputReader(java.io.InputStream is, OutputType type) {
		inputStream = is;
		inputType = type;
	}
	
	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}
	
	public void setOutputStream(OutputStream os) {
		this.outputStream = os;
	}
	
	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream),
				BUFFER_SIZE);
		BufferedWriter writer = null;
		if (outputStream != null) {
			writer = new BufferedWriter(
					new OutputStreamWriter(outputStream), 
					BUFFER_SIZE);
		} else {
			writer = new BufferedWriter(
					new OutputStreamWriter(inputType == OutputType.STDOUT ? System.out : System.err),
					BUFFER_SIZE);
		}

		char[] buf = new char[BUFFER_SIZE];
		int read = 0;
		try {
			while ((read = reader.read(buf)) != -1) {
				writer.write(buf, 0, read);
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
