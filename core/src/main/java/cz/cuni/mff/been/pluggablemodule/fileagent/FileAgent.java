/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.pluggablemodule.fileagent;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents simple interface for uploading/downloading files
 * to/from BEEN-wide persistent storage. This storage can be results repository.
 * 
 * 
 * @author Jan Tattermusch
 *
 */
public interface FileAgent {
	
	/**
	 * Uploads local file to BEEN-wide persistent storage.
	 * 
	 * @param file file to be stored. If null, no file will be stored and null will be stored in RR dataset.
	 * @param tags tags that identify file among others in the same storage 
	 */
	void storeFile(File file, Map<String, Serializable> tags) throws IOException;
	
	/**
	 * Downloads file from persistent storage to local file
	 * @param file destination file
	 * @param tags tags that identify desired file in storage
	 */
	void loadFile(File file, Map<String, Serializable> tags) throws IOException; 

	
	/**
	 * @param tags tags that identify file in storage
	 * @return true if file identified by tags exists
	 * @throws IOException
	 */
	boolean fileExists(Map<String, Serializable> tags) throws IOException;

}
