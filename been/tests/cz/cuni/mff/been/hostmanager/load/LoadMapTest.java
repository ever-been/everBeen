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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * Test output and input of the load map files.
 *
 * @author Branislav Repcek
 */
public class LoadMapTest {
	
	/**
	 * First test file.
	 */
	private static final String MAP1_FILE = System.getProperty("java.io.tmpdir") + "/map1";
	
	/**
	 * Second test file.
	 */
	private static final String MAP2_FILE = System.getProperty("java.io.tmpdir") + "/map2";
	
	/**
	 * Entries that are written to the file.
	 */
	private LoadMapFile.FileEntry []entries;
	
	@Before
	public void setUp() {
		
		entries = new LoadMapFile.FileEntry[10];
		
		for (int i = 0; i < entries.length; ++i) {
			entries[i] = new LoadMapFile.FileEntry(i, 5 * i, LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
		}
	}

	/**
	 * Remove test files if for some reason they are still in temp.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testDeleteFilesStart() throws Exception {
	
		MiscUtils.removeFile(MAP1_FILE);
		MiscUtils.removeFile(MAP2_FILE);
	}

	
	/**
	 * Write allsamples one-by-one.
	 *  
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testWriteAllSingle() throws Exception {
		
		System.out.println(MAP1_FILE);
		
		LoadMapFile loadMap = new LoadMapFile(MAP1_FILE, true);
		
		for (int i = 0; i < entries.length; ++i) {
			loadMap.append(entries[i]);
		}
		
		loadMap.close();
	}
	
	/**
	 * Read all sample one-by-one and compare them with original values.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testReadAllSingle() throws Exception {
		
		LoadMapFile loadMap = new LoadMapFile(MAP1_FILE, false);
		
		int count = loadMap.getCount();
		
		for (int i = 0; i < count; ++i) {
			LoadMapFile.FileEntry entry = loadMap.readFrom(i);
			assertEquals(entries[i], entry);
		}
		
		loadMap.close();
	}
	
	/**
	 * Write all entries at once.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testWriteAllMulti() throws Exception {
		
		LoadMapFile loadMap = new LoadMapFile(MAP2_FILE, true);
		
		loadMap.append(entries);
		
		loadMap.close();
	}
	
	/**
	 * Read all samples at once.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testReadAllMulti() throws Exception {
		
		LoadMapFile loadMap = new LoadMapFile(MAP2_FILE, false);
		
		int count = loadMap.getCount();
		
		List< LoadMapFile.FileEntry > res = loadMap.readFrom(0, count);
		
		for (int i = 0; i < res.size(); ++i) {
			assertEquals(entries[i], res.get(i));
		}
		
		loadMap.close();
	}
	
	/**
	 * Read some entries and compare with original data.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testReadAllSeek() throws Exception {
		
		LoadMapFile loadMap = new LoadMapFile(MAP1_FILE, false);
		
		int count = loadMap.getCount();
		int from = count / 2;
		
		List< LoadMapFile.FileEntry > res = loadMap.readFrom(from, count - from);
		
		assertEquals(count - from, res.size());
		
		for (int i = from, j = 0; i < count; ++i, ++j) {
			assertEquals(entries[i], res.get(j));
		}
		
		loadMap.close();
	}
	
	/**
	 * Not a test, just deletes files that have been created.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testDeleteFilesEnd() throws Exception {
		
		MiscUtils.removeFile(MAP1_FILE);
		MiscUtils.removeFile(MAP2_FILE);
	}
}
