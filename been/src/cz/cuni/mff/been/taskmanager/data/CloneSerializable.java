/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class with static method for clonning of objects implementing
 * <code>Serializable</code> interface.
 * 
 * @author Antonin Tomecek
 */
public class CloneSerializable {
	
	private CloneSerializable() {
		// Do nothing... (overwrites default constructor...)
	}
	
	/**
	 * Clone object implementing <code>Serializable</code> interface.
	 * 
	 * @param object Object implementing <code>Serializable</code> interface.
	 * @return Clone of <code>object</code>.
	 */
	protected static Serializable cloneSerializable(Serializable object) {
		Serializable clonedObject = null;
		
		try {
			/* Prepare ObjectOutputStream. */
			ByteArrayOutputStream byteArrayOutputStream
				= new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream
				= new ObjectOutputStream(byteArrayOutputStream);
			
			/* Write object. */
			objectOutputStream.writeObject(object);
			
			/* Get buffer. */
			byte[] buffer = byteArrayOutputStream.toByteArray();
			
			/* Close all output streams. */
			objectOutputStream.close();
			byteArrayOutputStream.close();
			
			/* Prepare ObjectInputStream. */
			ByteArrayInputStream byteArrayInputStream
				= new ByteArrayInputStream(buffer);
			ObjectInputStream objectInputStream
				= new ObjectInputStream(byteArrayInputStream);
			
			/* Read object */
			clonedObject = (Serializable) objectInputStream.readObject();
			
			/* Close all input streams. */
			objectInputStream.close();
			byteArrayInputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not clone");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Deserialization failed");
		}
		
		return clonedObject;
	}
}
