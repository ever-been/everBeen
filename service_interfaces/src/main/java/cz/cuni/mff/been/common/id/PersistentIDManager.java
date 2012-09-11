/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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
package cz.cuni.mff.been.common.id;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;


public class PersistentIDManager implements IDManagerInterface {

	private static final String RESTORE_SUBDIRECTORY = "idmanager";
	
	/**
	 * Counter holding the last value for a specific class
	 * @author Michal Tomcanyi
	 *
	 */
	private static class Counter {
		private long count = 0;
		
		public Counter(long value) {
			this.count = value;
		}
		
		public long getNext() {
			return count++;
		}
		
		public long getCount() {
			return count;
		}
		
		@Override
		public String toString() {
			return String.valueOf(count);
		}
	}
	
	/**
	 * Object[] stores pair (Counter,ObjectOutputStream)
	 */
	private final HashMap<Class< ? >, Object[]> idSet = new HashMap<Class< ? >, Object[]>(); /* Object[] = {Counter,FileChannel} */
	private final File dataDirectory;
	/** Byte buffer to be used for writing last getNext() long value */
	private final ByteBuffer buf = ByteBuffer.allocate(8);
	
	/* Access denied. This should never be instantiated by accident. */
	private PersistentIDManager() { this( null ); }
	
	private PersistentIDManager(File dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
	
	/**
	 * Creates new instance of manager. When log is found in <code>dataDirectory</code>
	 * the instance restores its state according to the log
	 * 
	 * @param dataDirectory	root directory under which the manager creates its log directory
	 * 						for storing its state
	 * @return	new or restored instance of manager
	 * 
	 * @throws FileNotFoundException	when log can't be found
	 * @throws ClassNotFoundException	when new instance of class stored under manager can't be created
	 * @throws IOException				when read operation from log fails
	 */
	public static PersistentIDManager createInstance(File dataDirectory) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		// we need to have only our files in the directory -> create separate subdirectory
		File workDir = new File(dataDirectory,RESTORE_SUBDIRECTORY);
		// create instance and if the work directory existed already, run restore 
		PersistentIDManager idManager = new PersistentIDManager(workDir);
		if (workDir.exists()) { 
			restore(idManager);
		} else {
			if (!workDir.mkdirs()) {
				throw new IOException("Unable to create working directory :" + workDir.getPath());
			}
		}
		return idManager;
	}
	
	/**
	 * Obtains next unique <code>long</code> value for given class.
	 * Before updating is status, the manager stores next value on disk.
	 * Such behaviour guerantees we never generate the same value after restore.
	 * 
	 * @param clazz	class for which to create next unique identifier
	 * @return	next unique identifier for given class
	 * @throws IllegalStateException when the next value can't be written on disk
	 */
	public synchronized <T extends OID> T getNext(Class<T> clazz) 
	throws IllegalStateException {
		Object[] value = idSet.get(clazz);
		Counter ctr = null;
		
		FileOutputStream os = null;
		try {
			if (value == null) {
				ctr = new Counter(0);
				File f = new File(dataDirectory,clazz.getCanonicalName());
				if (!f.exists()) {
					f.createNewFile();
				}
				os = new FileOutputStream(f);
				idSet.put(clazz, new Object[]{ctr,f});
			} else {
				ctr = (Counter)value[0];
				os =  new FileOutputStream((File)value[1]);
			}
	
			FileChannel channel = os.getChannel();
			/*
			 * The log must be written before doing update of counter.
			 * With such behavior in the worst case we have updated log, but not the counter, so we will skip
			 * one (or more) numbers in restore phase but no duplicities will be created
			 */
			buf.putLong(ctr.getCount());
			buf.flip();
			while (buf.hasRemaining()) {
				channel.write(buf);
			}
			channel.force(false);
			channel.close();
			buf.clear();
			
			/*
			 * 
			 */
			T instance = clazz.newInstance();
			instance.setValue(ctr.getNext());
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Can't write value to file",e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception ex) {
					// ignored
				}
			}
		}
	}

	/*
	 * <class,value> -> file with the name of the class holds the long value of last ID
	 */
	private static void restore(PersistentIDManager instance) 
	throws ClassNotFoundException, FileNotFoundException, IOException {
		ByteBuffer buf = ByteBuffer.allocateDirect(8);
		for (String keyName : instance.dataDirectory.list()) {
			File keyFile = new File(instance.dataDirectory,keyName);
			Class< ? > c = Class.forName(keyName);
			FileInputStream fos = new FileInputStream(keyFile);
			FileChannel channel = fos.getChannel();
			while (channel.read(buf) > 0) {
				// do nothing
			}
			fos.close();
			if (buf.hasRemaining()) {
				throw new IOException("The file " + keyFile.getPath() + " is corrupted");
			}
			buf.rewind();
			Long value = buf.getLong() + 1;
			buf.clear();
			instance.idSet.put(c,new Object[]{new Counter(value),keyFile});
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		PersistentIDManager manager = PersistentIDManager.restoreInstance(new File("c:/Data/MiSHo/BEEN/workspace/BEEN/data/benchmarkmanager"));
//		System.out.println(new Date(System.currentTimeMillis()));
//		for (int i=0; i<1000; i++){
//			manager.getNext(RID.class);
//			manager.getNext(BID.class)	;
//			manager.getNext(EID.class);
//		}
//		System.out.println(new Date(System.currentTimeMillis()));
//	}
	
}
