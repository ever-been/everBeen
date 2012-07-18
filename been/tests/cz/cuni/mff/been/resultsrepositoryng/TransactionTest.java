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
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.been.pluggablemodule.MockPluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.pluggablemodule.hibernate.implementation.HibernatePluggableModuleImpl;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor.DatasetType;
import cz.cuni.mff.been.resultsrepositoryng.data.ByteArrayDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.FloatDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.IntegerDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.LongDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.SerializableDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.StringDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.UUIDDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.implementation.RRDataset;
import cz.cuni.mff.been.resultsrepositoryng.implementation.ResultsRepositoryImplementation;
import cz.cuni.mff.been.resultsrepositoryng.transaction.RRTransaction;

public class TransactionTest {
	
	private static DerbyPluggableModule derbyPluggableModule;
	private static HibernatePluggableModule hibernatePluggableModule;
	
	private final static String ANALYSIS_NAME = "test_analysis";
	
	public final static String DBNAME = "RR_TEST";
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		PluggableModuleManager pmm = new MockPluggableModuleManager();
		
		derbyPluggableModule = new DerbyPluggableModuleImpl(pmm);		
		hibernatePluggableModule = new HibernatePluggableModuleImpl(pmm);
		derbyPluggableModule.startEngine("", false);
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		derbyPluggableModule.stopEngine();
	}
	
	@Before
	public void setUp() throws Exception {	
		
		derbyPluggableModule.dropDatabase(DBNAME);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	/**
	 * Inserts data to dataset in a transaction and check they're not 
	 * visible from outside before they're commited.
	 */
	@Test
	public void testCommit1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		RRTransaction t = resultsRepository.getTransaction(ANALYSIS_NAME, dataset.getName());
		
		DataHandleTuple data1 = getTestData1_1();
		DataHandleTuple data2 = getTestData1_2();
		
		t.saveData(data1);
		t.saveData(data2);
		
		//RRTransaction t2 = resultsRepository.getTransaction(ANALYSIS_NAME, dataset.getName());
		
		List<DataHandleTuple> result;
		
		/* data visible from transaction scope */
		result = t.loadData(null);
		Assert.assertEquals(2, result.size());
		
		/* data not visible from outside before commit */
		//result = t2.loadData(null);
		//Assert.assertEquals(0, result.size());
		
		t.commit();
		
		/* data visible from outside after commit */
		result = resultsRepository.loadData(ANALYSIS_NAME, dataset.getName(), null, null, null);
		Assert.assertEquals(2, result.size());
		
	}
	
	/**
	 * Inserts data to dataset in a transaction and check they're 
	 * rolled back correctly.
	 */
	@Test
	public void testRollback1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		RRTransaction t = resultsRepository.getTransaction(ANALYSIS_NAME, dataset.getName());
		
		DataHandleTuple data1 = getTestData1_1();
		DataHandleTuple data2 = getTestData1_2();
		
		t.saveData(data1);
		t.saveData(data2);
		
		List<DataHandleTuple> result;
		
		/* data visible from transaction scope */
		//result = t.loadData(null);
		//Assert.assertEquals(2, result.size());
		
		/* data not visible from outside before commit */
		//result = resultsRepository.loadData(ANALYSIS_NAME, dataset.getName(), null, null, null);
		//Assert.assertEquals(0, result.size());
		
		t.rollback();
		
		/* data visible from outside after commit */
		result = resultsRepository.loadData(ANALYSIS_NAME, dataset.getName(), null, null, null);
		Assert.assertEquals(0, result.size());
		
	}
	
//	
//	this test does not work (causes deadlock and
//	could not obtain lock error) because of derby's flawed transaction code.
//	
//	@Test 
//	public void testConcurrency() throws Exception {
//		final ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
//		
//		final RRDataset dataset = getTestDataset1();
//		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
//		
//		TestThread thread1 = new TestThread(resultsRepository, "thread1", dataset.getName());  
//		TestThread thread2 = new TestThread(resultsRepository, "thread2", dataset.getName());
//		
//		//RRTransaction t2 = resultsRepository.getTransaction(ANALYSIS_NAME, dataset.getName());
//		
//		//List<DataHandleTuple> result;
//		
//		/* data visible from transaction scope */
//		//result = t.loadData(null);
//		//Assert.assertEquals(2, result.size());
//		
//		/* data not visible from outside before commit */
//		//result = t2.loadData(null);
//		//Assert.assertEquals(0, result.size());
//		
//		//t.commit();
//		
//		/* data visible from outside after commit */
//		
//		thread1.start();
//		thread2.start();
//		
//		thread1.join();
//		thread2.join();
//		
//		
//		System.out.println(thread1);
//		System.out.println(thread2);
//		Assert.assertTrue(thread1.isSucceeded());
//		Assert.assertTrue(thread2.isSucceeded());
//		
//	}
	
	/**
	 * 
	 * @return testing dataset definition
	 */
	private RRDataset getTestDataset1() {
		DatasetDescriptor dd = new DatasetDescriptor();
		dd.put("key_tag1", DataHandle.DataType.INT, true);
		dd.put("key_tag2", DataHandle.DataType.STRING, false);
		dd.put("data1", DataHandle.DataType.STRING, false);
		dd.put("data2", DataHandle.DataType.FLOAT, false);
		dd.put("data3", DataHandle.DataType.UUID, false);
		dd.put("data4", DataHandle.DataType.LONG, false);
		dd.put("data5", DataHandle.DataType.SMALL_BINARY, false);
		dd.put("data6", DataHandle.DataType.SERIALIZABLE, false);
		RRDataset dataset = new RRDataset(ANALYSIS_NAME,"testdataset1",dd);
		dd.setDatasetType(DatasetType.TRANSACTION_ENABLED);
		return dataset;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData1_1() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("key_tag1", new IntegerDataHandle(5));
		data.set("key_tag2", new StringDataHandle("fddfsadf"));
		data.set("data1", new StringDataHandle("fddfsadf"));
		data.set("data2", new FloatDataHandle((float) 3.14159));
		data.set("data3", new UUIDDataHandle(UUID.randomUUID()));
		data.set("data4", new LongDataHandle( 46446644));
		data.set("data5", new ByteArrayDataHandle("some string".getBytes()));
		data.set("data6", new SerializableDataHandle("serialized string"));
		return data;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData1_2() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("key_tag1", new IntegerDataHandle(11));
		data.set("key_tag2", new StringDataHandle("aaaa"));
		data.set("data1", new StringDataHandle("fddfsadf"));
		data.set("data2", new FloatDataHandle((float) 2.5));
		data.set("data3", new UUIDDataHandle(UUID.randomUUID()));
		data.set("data4", new LongDataHandle( 46444));
		data.set("data5", new ByteArrayDataHandle("some string2".getBytes()));
		data.set("data6", new SerializableDataHandle("serialized string"));
		return data;
	}
	
//	/**
//	 * 
//	 * @return testing data for dataset
//	 */
//	private DataHandleTuple getTestData1_3() {
//		DataHandleTuple data = new DataHandleTuple();
//		data.set("key_tag1", new IntegerDataHandle(8));
//		data.set("key_tag2", new StringDataHandle("2334"));
//		data.set("data1", new StringDataHandle("fddfsadf"));
//		data.set("data2", new FloatDataHandle((float) 3.14159));
//		data.set("data3", new UUIDDataHandle(UUID.randomUUID()));
//		data.set("data4", new LongDataHandle( 46446));
//		data.set("data5", new ByteArrayDataHandle("some string3".getBytes()));
//		data.set("data6", new SerializableDataHandle("serialized string"));
//		return data;
//	}
	
	
	class TestThread extends Thread {
		
		private ResultsRepositoryImplementation resultsRepository;
		private String threadName;
		private String datasetName;
		private boolean succeeded = false;
		
		public TestThread(ResultsRepositoryImplementation rr, String threadName, String datasetName) {
			super();
			this.resultsRepository = rr;
			this.threadName = threadName;
			this.datasetName = datasetName;
		}

		@Override
		public void run() {
			try {
				
				msg("thread started");
				
				for (int i = 0; i < 20; i++) {
					msg("loop " + i);
					
					RRTransaction t = resultsRepository.getTransaction(ANALYSIS_NAME, datasetName);

					DataHandleTuple data1 = getTestData1_1();
					//DataHandleTuple data2 = getTestData1_2();

					List<DataHandleTuple> result;
					Thread.sleep(100);

					t.saveData(data1);

					msg("inserted data1");

					result = t.loadData(null);
					msg(result.size() +" rows visible");

					
					/*t.saveData(data2);
					msg("inserted data2");

					result = t.loadData(null);
					msg(result.size() +" rows visible");*/

					//Thread.sleep(100);

					t.commit();
					msg("commited data");

				}				
				succeeded = true;
				msg("finishing");

			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		private void msg(String s) {
			System.err.println(threadName + ": " + s);
		}
		
		public boolean isSucceeded() {
			return succeeded;
		}
	};
}
