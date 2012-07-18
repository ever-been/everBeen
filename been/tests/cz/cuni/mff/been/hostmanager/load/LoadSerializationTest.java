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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * Test serialization routines for LoadSample and LoadMonitorEvent types.
 *
 * @author Branislav Repcek
 */
public class LoadSerializationTest {

	/**
	 * Name of the test file for sample serialization.
	 */
	private static final String SAMPLES_TEST_FILE_NAME = System.getenv( "BEEN_HOME" ) + "/data/samples.test";

	/**
	 * Name of the test file for event serialization.
	 */
	private static final String EVENTS_TEST_FILE_NAME = System.getenv( "BEEN_HOME" ) + "/data/events.test";
	
	/**
	 * Samples used in tests.
	 */
	private LoadSample []samples;
	
	/**
	 * Events used in tests.
	 */
	private LoadMonitorEvent []events;
	
	/**
	 * Hardware descriptions.
	 */
	private HardwareDescription []descriptions;
	
	private static long sampleSkip;
	
	/**
	 * Initialize all samples and events.
	 */
	@Before
	public void setUp() {
		
		File f = new File(System.getenv("BEEN_HOME") + "/" + "data");
		f.mkdirs();
		
		samples = new LoadSample[8];
		
		samples[0] = new LoadSample();
		samples[0].setTimeStamp(0L);
		samples[0].setTSC(5646465321L);
		samples[0].setMemoryFree(51546464L);
		samples[0].setProcessCount(57);
		samples[0].setProcessQueueLength(2);

		samples[1] = new LoadSample();
		samples[1].setTimeStamp(1L);
		samples[1].setTSC(87897646231L);
		samples[1].setMemoryFree(688796313L);
		samples[1].setProcessCount(84);
		samples[1].setProcessQueueLength(0);
		samples[1].setProcessorUsage(new short[]{74, 89});
	
		samples[2] = new LoadSample();
		samples[2].setTimeStamp(2L);
		samples[2].setTSC(87764331L);
		samples[2].setMemoryFree(897977L);
		samples[2].setProcessCount(29);
		samples[2].setProcessQueueLength(4);
		samples[2].setDiskReadAndWriteBytes(new long[]{874646L, 23121L}, new long[]{0, 67879L});

		samples[3] = new LoadSample();
		samples[3].setTimeStamp(3L);
		samples[3].setTSC(6846531365464L);
		samples[3].setMemoryFree(879865413L);
		samples[3].setProcessCount(197);
		samples[3].setProcessQueueLength(1);
		samples[3].setProcessorUsage(new short[]{89, 0, 6});
		samples[3].setDiskReadAndWriteBytes(new long[]{89797L, 0L}, new long[]{7870L, 167879L});

		samples[4] = new LoadSample();
		samples[4].setTimeStamp(4L);
		samples[4].setTSC(6846531365464L);
		samples[4].setMemoryFree(879865413L);
		samples[4].setProcessCount(197);
		samples[4].setProcessQueueLength(1);
		samples[4].setProcessorUsage(new short[]{89, 0, 6});
		samples[4].setDiskReadAndWriteBytes(new long[]{89797L, 0L}, new long[]{7870L, 167879L});

		samples[5] = new LoadSample();
		samples[5].setTimeStamp(5L);
		samples[5].setTSC(23221323425L);
		samples[5].setMemoryFree(8796546L);
		samples[5].setProcessCount(52);
		samples[5].setProcessQueueLength(6);
		samples[5].setProcessorUsage(new short[]{17});
		samples[5].setDiskReadAndWriteBytes(new long[]{28L}, new long[]{987425L});
		samples[5].setNetworkReadWriteSpeed(new int[]{1287}, new int[]{5874448});

		samples[6] = new LoadSample();
		samples[6].setTimeStamp(6L);
		samples[6].setTSC(23221323425L);
		samples[6].setMemoryFree(8796546L);
		samples[6].setProcessCount(52);
		samples[6].setProcessQueueLength(6);
		samples[6].setProcessorUsage(new short[]{17});
		samples[6].setNetworkReadWriteSpeed(new int[]{1287}, new int[]{5874448});
		
		samples[7] = new LoadSample();
		samples[7].setTimeStamp(7L);
		samples[7].setTSC(23221323425L);
		samples[7].setMemoryFree(8796546L);
		samples[7].setProcessCount(52);
		samples[7].setProcessQueueLength(6);
		samples[7].setNetworkReadWriteSpeed(new int[]{1287}, new int[]{5874448});
		
		
		descriptions = new HardwareDescription[4];

		descriptions[0] = new HardwareDescription();
		descriptions[0].setCpuCount((short) 3);
		descriptions[0].setTimeStamp(110);

		descriptions[1] = new HardwareDescription();
		descriptions[1].setTimeStamp(111);
		descriptions[1].setCpuCount((short) 3);
		descriptions[1].setAdapters(new String[]{"adapter1", "adapter2"});
		descriptions[1].setDrives(new String[]{"drive1", "drive2", "drive3longname"});
		
		descriptions[2] = new HardwareDescription();
		descriptions[2].setCpuCount((short) 3);
		descriptions[2].setTimeStamp(112);
		descriptions[2].setDrives(new String[]{"drive1", "drive2", "drive3anotherlongname"});

		descriptions[3] = new HardwareDescription();
		descriptions[3].setCpuCount((short) 3);
		descriptions[3].setTimeStamp(113);
		descriptions[3].setAdapters(new String[]{"adapter1", "adapter2"});
		
		
		events = new LoadMonitorEvent[9];

		events[0] = new LoadMonitorEvent();
		events[0].setHostName("sjdh.djf");
		events[0].setTime(new Date(6));
		events[0].setHardwareDescription(descriptions[1]);
		events[0].setType(LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
		
		events[1] = new LoadMonitorEvent();
		events[1].setSample(samples[5]);
		events[1].setHostName("test.host.another");
		events[1].setTime(new Date(1));
		events[1].setType(LoadMonitorEvent.EventType.MONITOR_START);
		
		events[2] = new LoadMonitorEvent();
		events[2].setSample(samples[1]);
		events[2].setHostName("very.long.and.annoying.host.name");
		events[2].setTime(new Date(2));
		events[2].setType(LoadMonitorEvent.EventType.MONITOR_START);
		
		events[3] = new LoadMonitorEvent();
		events[3].setSample(samples[7]);
		events[3].setHostName("");
		events[3].setTime(new Date(3));
		events[3].setType(LoadMonitorEvent.EventType.MONITOR_START);

		LoadSample bigSample = new LoadSample();
		bigSample.setTimeStamp(0xABCDEFL);
		bigSample.setTSC(124474655547L);
		bigSample.setMemoryFree(89789631324L);
		bigSample.setProcessCount(132);
		bigSample.setProcessQueueLength(4);
		bigSample.setProcessorUsage(new short[]{23, 0, 11, 14, 19, 88, 94, 65});
		bigSample.setDiskReadAndWriteBytes(new long[]{28L, 988455L, 112344L}, new long[]{987425L, 8885412L, 154L});
		bigSample.setNetworkReadWriteSpeed(new int[]{775, 0, 0, 0, 0, 0, 19}, new int[]{44277656, 21323, 0, 0, 0, 0, 998744});
		
		events[4] = new LoadMonitorEvent();
		events[4].setSample(bigSample);
		events[4].setHostName("this.has.big.sample");
		events[4].setTime(new Date(4));
		events[4].setType(LoadMonitorEvent.EventType.MONITOR_STOP);

		events[5] = new LoadMonitorEvent();
		events[5].setSample(samples[4]);
		events[5].setHostName("something.something");
		events[5].setTime(new Date(5));
		events[5].setType(LoadMonitorEvent.EventType.MONITOR_SAMPLE);
		
		events[6] = new LoadMonitorEvent();
		events[6].setHostName("sjdh.djf");
		events[6].setTime(new Date(6));
		events[6].setHardwareDescription(descriptions[1]);
		events[6].setType(LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);

		events[7] = new LoadMonitorEvent();
		events[7].setHostName("13sda.iuewgyfdigdj");
		events[7].setTime(new Date(7));
		events[7].setHardwareDescription(descriptions[0]);
		events[7].setSample(samples[1]);
		events[7].setType(LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
	
		events[8] = new LoadMonitorEvent();
		events[8].setHostName("kdjfhkshafkj");
		events[8].setTime(new Date(8));
		events[8].setHardwareDescription(descriptions[3]);
		events[8].setSample(samples[4]);
		events[8].setType(LoadMonitorEvent.EventType.MONITOR_SAMPLE);
	}

	/**
	 * Write sample and read it back. Then compare both samples.
	 * 
	 * @param s Sample to write and read back.
	 * 
	 * @return <tt>true</tt> on success(read and write succeeded and samples are same),
	 *         <tt>false</tt> otherwise.
	 */
	private boolean writeAndRead(LoadSample s) {
		
		ByteBuffer buffer = ByteBuffer.allocate(4000);
		
		try {
			s.save(buffer);
		} catch (Exception e) {
			System.out.println("Write failed: " + e.getMessage());
			return false;
		}

		buffer.position(0);
		
		LoadSample sample = new LoadSample();
		
		try {
			sample.load(buffer);
		} catch (Exception e) {
			System.out.println("Read failed: " + e.getMessage());
			return false;
		}
		
		return sample.equals(s);
	}
	
	/**
	 * Write event and read it back. Then compare both events. 
	 * 
	 * @param event Event to write and read back.
	 * 
	 * @return <tt>true</tt> on success (read and write succeeded and events are same), 
	 *         <tt>false</tt> otherwise.
	 */
	private boolean writeAndReadEvent(LoadMonitorEvent event) {
		
		ByteBuffer buffer = ByteBuffer.allocate(4000);
		
		try {
			event.save(buffer);
		} catch (Exception e) {
			System.out.println("Event write failed: " + e.getMessage());
			fail();
		}
		
		buffer.position(0);
		
		LoadMonitorEvent e = new LoadMonitorEvent();
		
		try {
			e.load(buffer);
		} catch (Exception f) {
			System.out.println("Event read failed: " + f.getMessage());
			fail();
		}
		
		return event.equals(e);
	}
	
	/**
	 * Write hardware description and read it back. Then compare both values.
	 * 
	 * @param hardware Description to write and read.
	 * 
	 * @return <tt>true</tt> on success (read and write succeeded and descriptions are same), 
	 *         <tt>false</tt> otherwise.
	 */
	private boolean writeAndReadHW(HardwareDescription hardware) {
		
		ByteBuffer buffer = ByteBuffer.allocate(4000);
		
		try {
			hardware.save(buffer);
		} catch (Exception e) {
			System.out.println("Description write failed: " + e.getMessage());
			fail();
		}
		
		buffer.position(0);
		
		HardwareDescription hwd = new HardwareDescription();
		
		try {
			hwd.load(buffer);
		} catch (Exception f) {
			System.out.println("Description read failed: " + f.getMessage());
			fail();
		}
		
		return hardware.equals(hwd);
	}
	
	/**
	 * Test 1st sample (index 0).
	 */
	@Test
	public void test0() {
		
		assertTrue(writeAndRead(samples[0]));
	}
	
	/**
	 * Test 2nd sample (index 1).
	 */
	@Test
	public void test1() {
		
		assertTrue(writeAndRead(samples[1]));
	}

	/**
	 * Test 3rd sample (index 2).
	 */
	@Test
	public void test2() {
		
		assertTrue(writeAndRead(samples[2]));
	}
	
	/**
	 * Test 4th sample (index 3).
	 */
	@Test
	public void test3() {
		
		assertTrue(writeAndRead(samples[3]));
	}
	
	/**
	 * Test 5th sample (index 4).
	 */
	@Test
	public void test4() {
		
		assertTrue(writeAndRead(samples[4]));
	}
	
	/**
	 * Test 6th sample (index 5).
	 */
	@Test
	public void test5() {
		
		assertTrue(writeAndRead(samples[5]));
	}
	
	/**
	 * Test 7th sample (index 6).
	 */
	@Test
	public void test6() {
		
		assertTrue(writeAndRead(samples[6]));
	}
	
	/**
	 * Test 8th sample (index 7).
	 */
	@Test
	public void test7() {
		
		assertTrue(writeAndRead(samples[7]));
	}

	/**
	 * Test 1st event (index 0).
	 */
	@Test
	public void testE0() {
		
		assertTrue(writeAndReadEvent(events[0]));
	}
	
	/**
	 * Test 2nd event (index 1).
	 */
	@Test
	public void testE1() {
		
		assertTrue(writeAndReadEvent(events[1]));
	}
	
	/**
	 * Test 3rd event (index 2).
	 */
	@Test
	public void testE2() {
		
		assertTrue(writeAndReadEvent(events[2]));
	}

	/**
	 * Test 4th event (index 3).
	 */
	@Test
	public void testE3() {
		
		assertTrue(writeAndReadEvent(events[3]));
	}

	/**
	 * Test 5th event (index 4).
	 */
	@Test
	public void testE4() {
		
		assertTrue(writeAndReadEvent(events[4]));
	}
	
	/**
	 * Test 6th event (index 5).
	 */
	@Test
	public void testE5() {
		
		assertTrue(writeAndReadEvent(events[5]));
	}

	/**
	 * Test 1st hardware description (index 0).
	 */
	@Test
	public void testH0() {
		
		assertTrue(writeAndReadHW(descriptions[0]));
	}
	
	/**
	 * Test 2nd hardware description (index 1).
	 */
	@Test
	public void testH1() {
		
		assertTrue(writeAndReadHW(descriptions[1]));
	}

	/**
	 * Test 3rd hardware description (index 2).
	 */
	@Test
	public void testH2() {
		
		assertTrue(writeAndReadHW(descriptions[2]));
	}

	/**
	 * Test 4th hardware description (index 3).
	 */
	@Test
	public void testH3() {
		
		assertTrue(writeAndReadHW(descriptions[3]));
	}

	/**
	 * Write all samples and read them back and compare. This tests if resulting file does not have
	 * any "holes".
	 */
	@Test
	public void testSamplesMulti() {
		
		ByteBuffer buffer = ByteBuffer.allocate(20000);
		
		for (int i = 0; i < samples.length; ++i) {
			
			try {
				samples[i].save(buffer);
			} catch (Exception e) {
				System.out.println("Failed writing sample " + i + ": " + e.getMessage());
				fail();
			}
		}
		
		LoadSample []ns = new LoadSample[samples.length];
		
		buffer.position(0);
		
		for (int i = 0; i < samples.length; ++i) {
			
			try {
				ns[i] = new LoadSample();
				ns[i].load(buffer);
			} catch (Exception e) {
				System.out.println("Failed reading sample " + i + ": " + e.getMessage());
				fail();
			}
		}
		
		for (int i = 0; i < samples.length; ++i) {
			if (!samples[i].equals(ns[i])) {
				System.out.println("!equal at sample " + i);
				fail();
			}
		}
	}
	
	/**
	 * Write all events and read them back and compare. This tests if resulting file does not have
	 * any "holes".
	 */
	@Test
	public void testEventsMulti() {
		
		ByteBuffer buffer = ByteBuffer.allocate(20000);
		
		for (int i = 0; i < events.length; ++i) {
			
			try {
				events[i].save(buffer);
			} catch (Exception e) {
				System.out.println("Failed writing event " + i + ": " + e.getMessage());
				fail();
			}
		}

		LoadMonitorEvent []evs = new LoadMonitorEvent[events.length];
		
		buffer.position(0);
		
		for (int i = 0; i < events.length; ++i) {
			
			evs[i] = new LoadMonitorEvent();
	
			try {
				evs[i].load(buffer);
			} catch (Exception e) {
				System.out.println("Failed reading event " + i + ": " + e.getMessage());
				fail();
			}
		}
	
		for (int i = 0; i < events.length; ++i) {
			if (!events[i].equals(evs[i])) {
				System.out.println("!equals event " + i);
				fail();
			}
		}
	}
	
	/**
	 * Write sample to the file using LoadFileParser.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserSampleSequentialAppend() throws Exception {

		File file = new File(SAMPLES_TEST_FILE_NAME);
		
		if (file.exists()) {
			file.delete();
		}
		
		LoadFileParser< LoadSample > parser = 
			new LoadFileParser< LoadSample >(file, true, LoadSample.class);
		
		int i = 0;
		
		for ( ; i < 4; ++i) {
			parser.append(samples[i]);
		}
		
		sampleSkip = parser.getPosition();
		
		for (; i < samples.length; ++i) {
			parser.append(samples[i]);
		}

		parser.close();
	}
	
	/**
	 * Test sequential reads of LoadSamples with the LoadFileParser.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserSampleSequentialRead() throws Exception {
		
		System.out.println("Sample sequential read");
		
		File file = new File(SAMPLES_TEST_FILE_NAME);
		
		LoadFileParser< LoadSample > parser =
			new LoadFileParser< LoadSample >(file, false, LoadSample.class);
		
		for (int i = 0; i < samples.length; ++i) {
			
			System.out.print("  Reading sample: " + i);

			LoadSample s = parser.getNext();
			
			if (!s.equals(samples[i])) {
				System.out.println("  ... FAILED");
				fail("Invalid sample data for sample: " + i);
			} else {
				System.out.println("  ... OK");
			}
		}
		
		parser.close();
	}
	
	/**
	 * Test if skip method in LoadFileParser works correctly.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserSampleSkipAndRead() throws Exception {
		
		System.out.println("Samples skip & read");
		
		File file = new File(SAMPLES_TEST_FILE_NAME);
		
		LoadFileParser< LoadSample > parser =
			new LoadFileParser< LoadSample >(file, false, LoadSample.class);
		
		// read 2 samples
		for (int i = 0; i < 2; ++i) {
			
			System.out.print("  Reading sample: " + i);
			
			LoadSample s = parser.getNext();
			
			if (!s.equals(samples[i])) {
				System.out.println("  ... FAILED");
				fail("Invalid sample data for sample: " + i);
			} else {
				System.out.println("  ... OK");
			}
		}

		System.out.println("  Skiping 3 samples");
		parser.skip(3);
		
		// Read next two samples, we should get samples[5] and samples[6].
		for (int i = 0; i < 2; ++i) {
			
			System.out.print("  Reading sample: " + i);
			
			LoadSample s = parser.getNext();
			
			if (!s.equals(samples[5 + i])) {
				System.out.println("  ... FAILED");
				fail("Invalid sample after skip: " + i + ". Timestamp: " + s.getTimeStamp());
			} else {
				System.out.println("  ... OK");
			}
		}
		
		parser.close();
	}
	
	/**
	 * Test file seeking.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserSampleSeekAndRead() throws Exception {
		
		System.out.println("Samples seek & read");
		
		File file = new File(SAMPLES_TEST_FILE_NAME);
		
		LoadFileParser< LoadSample > parser =
			new LoadFileParser< LoadSample >(file, false, LoadSample.class);

		parser.seek(sampleSkip);

		// now read one sample, it is sufficient since we already read whole file in previous test
		// so if that one didn't fail, reading more samples will not provide more useful data
		LoadSample s = parser.getNext();
		
		parser.close();
		
		if (!s.equals(samples[4])) {
			fail();
		}

		// Delete test file, it is no longer needed.
		file.delete();
	}
	
	/**
	 * Write events to the file using LoadFileParser.
	 *
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserEventSequentialAppend() throws Exception {
	
		LoadFileParser< LoadMonitorEvent > parser =
			new LoadFileParser< LoadMonitorEvent >(new File(EVENTS_TEST_FILE_NAME), true, LoadMonitorEvent.class);
		
		parser.append(events);
		parser.close();
	}
	
	/**
	 * Test sequential reads of events from the file.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserEventSequentialRead() throws Exception {
		
		System.out.println("Event sequential read");
		
		File file = new File(EVENTS_TEST_FILE_NAME);
		
		LoadFileParser< LoadMonitorEvent > parser =
			new LoadFileParser< LoadMonitorEvent >(file, false, LoadMonitorEvent.class);
		
		for (int i = 0; i < events.length; ++i) {
			
			System.out.print("  Reading event: " + i);
			
			LoadMonitorEvent s = parser.getNext();
			
			if (!s.equals(events[i])) {
				System.out.println("  ... FAILED");
				fail("Invalid event data for event: " + i);
			} else {
				System.out.println("  ... OK");
			}
		}
		
		parser.close();
	}
	
	/**
	 * Test if skip works correctly for events.
	 * 
	 * @throws Exception If an error occured.
	 */
	@Test
	public void testParserEventSkipAndRead() throws Exception {
		
		System.out.println("Events skip & read");
		
		File file = new File(EVENTS_TEST_FILE_NAME);
		
		LoadFileParser< LoadMonitorEvent > parser =
			new LoadFileParser< LoadMonitorEvent >(file, false, LoadMonitorEvent.class);
		
		// Read 3 events
		for (int i = 0; i < 3; ++i) {
			
			System.out.print("  Reading event: " + i);
			
			LoadMonitorEvent s = parser.getNext();
			
			if (!s.equals(events[i])) {
				System.out.println("  ... FAILED");
				fail("Invalid sample data for sample: " + i);
			} else {
				System.out.println("  ... OK");
			}
		}

		System.out.println("  Skiping 2 events");
		parser.skip(2);
		
		// Read last three events, we should get events[5], events[6] and events[7].
		for (int i = 0; i < 3; ++i) {
			
			System.out.print("  Reading event: " + i);
			
			LoadMonitorEvent s = parser.getNext();
			
			if (!s.equals(events[5 + i])) {
				System.out.println("  ... FAILED");
				fail("Invalid event after skip: " + i);
			} else {
				System.out.println("  ... OK");
			}
		}
		
		parser.close();

		// Delete test file, it is no longer needed.
		file.delete();
	}
}
