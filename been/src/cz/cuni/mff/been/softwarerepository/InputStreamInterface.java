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
package cz.cuni.mff.been.softwarerepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * A simple interface for input stream transporters that completely hides RMIIO.
 * 
 * @author Andrej Podzimek
 */
public interface InputStreamInterface extends Serializable {
	
	/**
	 * Input stream getter.
	 * 
	 * @return An input stream which points at the remote stream server.
	 * @throws IOException When something bad happens.
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Closes the underlying remote stream implementation.
	 * 
	 * @param success The flag set by the remote stream.
	 * @throws IOException When closing fails.
	 */
	public void close( boolean success ) throws IOException;
}
