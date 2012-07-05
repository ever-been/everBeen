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
package cz.cuni.mff.been.resultsrepositoryng;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.resultsrepositoryng.filestore.implementation.FileStore;
import cz.cuni.mff.been.resultsrepositoryng.filestore.implementation.FileStoreClientImpl;
import cz.cuni.mff.been.resultsrepositoryng.filestore.implementation.FileStoreImpl;

public class FileStoreTest {
	
	public final static String DBNAME = "RR_TEST";
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {	
		/* deletes all filestore's files 
		 */
		deleteDirectory(new File(HOME_DIR));
		
		createTestFile1();
		createTestFile2();
		
		{
			File f = getFile3(); 
			if (f.exists()) f.delete();
			
			f = getFile4(); 
			if (f.exists()) f.delete();
		}
		
	}
	
	@After
	public void tearDown() throws Exception {	
		
	}
	
	
	
	public static final String HOME_DIR = "filestore_test";
	/**
	 * Uploads a file, then downloads it and compares it with original file
	 */
	@Test
	public void testUploadFile() throws Exception {
		FileStore fileStore = new FileStoreImpl(HOME_DIR);
		FileStoreClient client = new FileStoreClientImpl(fileStore);
		
		
		File f1 = createTestFile1();
		UUID id = client.uploadFile(f1);
		
		File f3 = getFile3();
		client.downloadFile(id, f3);
		
		Assert.assertEquals(true, filesEqual(f1, f3) );
	}
	
	private File createTestFile1() throws Exception {
		File f = new File("testfile1");
		if (f.exists()) f.delete();
		
		String data = "content of first file";
		writeBytesToFile(f,data.getBytes());
		return f;
	}
	
	private File createTestFile2() throws Exception {
		File f = new File("testfile2");
		if (f.exists()) f.delete();
		
		String data = "content of second file";
		writeBytesToFile(f,data.getBytes());
		
		return f;
	}
	
	private File getFile3() {
		return new File("testfile3");
	}
	
	private File getFile4() {
		return new File("testfile4");
	}
	
	private boolean filesEqual(File f1, File f2) throws IOException {
		byte[] data1 = getBytesFromFile(f1);
		byte[] data2 = getBytesFromFile(f2);
		
		if (data1.length != data2.length) return false;
		
		for(int i = 0; i < data1.length; i ++) {
			if (data1[i] != data2[i]) return true;
		}
		return true;
	}
	
	private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
	
	private void writeBytesToFile(File f, byte[] data) throws IOException {
		FileOutputStream os = new FileOutputStream(f);
		os.write(data);
		os.close();
	}
	
	/**
	 * deletes directory recursively
	 * @param path
	 * @return
	 */
	static private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
