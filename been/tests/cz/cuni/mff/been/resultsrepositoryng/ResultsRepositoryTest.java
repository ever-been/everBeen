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

import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.MockPluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.pluggablemodule.hibernate.implementation.HibernatePluggableModuleImpl;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor.DatasetType;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;
import cz.cuni.mff.been.resultsrepositoryng.data.ByteArrayDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.FloatDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.IntegerDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.LongDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.SerializableDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.StringDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.UUIDDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.implementation.EvaluatorScheduler;
import cz.cuni.mff.been.resultsrepositoryng.implementation.RRDataset;
import cz.cuni.mff.been.resultsrepositoryng.implementation.ResultsRepositoryImplementation;

public class ResultsRepositoryTest {
	
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
	 * Creates a new dataset
	 */
	@Test
	public void testCreateDataset1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		List<String> datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), dataset.getName());
	}
	
	/**
	 * Creates a new dataset
	 */
	@Test
	public void testCreateDataset1_1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset1 = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset1.getName(), dataset1.getDatasetDescriptor());

		RRDataset dataset3 = getTestDataset3();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset3.getName(), dataset3.getDatasetDescriptor());

		RRDataset dataset4 = getTestDataset4();
		resultsRepository.createDataset(dataset4.getAnalysis(),dataset4.getName(), dataset4.getDatasetDescriptor());
		
		List<String> datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(2, datasets.size());
		Assert.assertEquals(datasets.get(0), dataset1.getName());
		Assert.assertEquals(datasets.get(1), dataset3.getName());
		
		datasets = resultsRepository.getDatasets(dataset4.getAnalysis());
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), dataset4.getName());
	}
	
	/**
	 * Tries to create two datasets with the same name (should result in failure)
	 */
	@Test(expected = ResultsRepositoryException.class)
	public void testCreateDataset2() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		
	}
	
	/**
	 * Tries to create two datasets with the same name but each one in different analysis (should pass)
	 */
	@Test
	public void testCreateDataset2_1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		resultsRepository.createDataset("another_analysis",dataset.getName(), dataset.getDatasetDescriptor());
		
		List<String> datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), dataset.getName());
		
		datasets = resultsRepository.getDatasets("another_analysis");
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), dataset.getName());
	}
	
	/**
	 *  Creates a dataset, then deletes it and creates a dataset with the same name again.
	 *  (This tests whether entity names are salted) 
	 */
	@Test
	public void testCreateDataset3() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		
		RRDataset dataset1 = getTestDataset1();
		/* common dataset name */
		String datasetName = dataset1.getName();
		
		resultsRepository.createDataset(ANALYSIS_NAME,datasetName, dataset1.getDatasetDescriptor());
		
		/* check result */
		List<String> datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), datasetName);
		
		resultsRepository.deleteDataset(ANALYSIS_NAME,datasetName);
		
		/* check result */
		datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(0, datasets.size());
		
		RRDataset dataset2 = getTestDataset2();
		resultsRepository.createDataset(ANALYSIS_NAME,datasetName, dataset2.getDatasetDescriptor());
		
		/* check result */
		datasets = resultsRepository.getDatasets(ANALYSIS_NAME);
		Assert.assertEquals(1, datasets.size());
		Assert.assertEquals(datasets.get(0), datasetName);
	}
	
	/**
	 *  Tries to create dataset with illegal name 
	 */
	@Test(expected=ResultsRepositoryException.class)
	public void testCreateDataset4() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		
		RRDataset dataset1 = getTestDataset1();
		/* common dataset name */
		dataset1.setName("illegal dataset name");
		
		resultsRepository.createDataset(ANALYSIS_NAME,dataset1.getName(), dataset1.getDatasetDescriptor());
	}
	
	/**
	 *  Tries to create dataset with analysis name 
	 */
	@Test(expected=ResultsRepositoryException.class)
	public void testCreateDataset4_1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		
		RRDataset dataset1 = getTestDataset1();
		/* common dataset name */
		dataset1.setAnalysis("analysisnamečřčš");
		
		resultsRepository.createDataset(dataset1.getAnalysis(),dataset1.getName(), dataset1.getDatasetDescriptor());
	}
	
	/**
	 * Creates a dataset and saves data to it.
	 * Reads data again and check its content.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData1() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData1_1();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		DataHandleTuple r = result.get(0);
		for (String name : testdata.getKeys()) {
			Assert.assertEquals( testdata.get(name), r.get(name)  );
		}
		//Assert.assertEquals(datasets.get(0), dataset.getName());
	}
	
	/**
	 * Tests whether data handles containing null values can be stored in RR.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData1_2() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData1_3();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		DataHandleTuple r = result.get(0);
		for (String name : testdata.getKeys()) {
			Assert.assertEquals( testdata.get(name), r.get(name)  );
		}
		//Assert.assertEquals(datasets.get(0), dataset.getName());
	}

	
	/**
	 * Creates another dataset and saves data to it.
	 * Reads data again and check its content.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData2() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset3();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData3_1();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		DataHandleTuple r = result.get(0);
		for (String name : testdata.getKeys()) {
			Assert.assertEquals( testdata.get(name), r.get(name)  );
		}
		//Assert.assertEquals(datasets.get(0), dataset.getName());
	}


	/**
	 * Creates another dataset and saves data to it.
	 * Reads data again and check its content.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData3() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME,"");
		
		String datasetName = "SimpleTest";
		DatasetDescriptor dataset = new DatasetDescriptor();
		dataset.put("PK", DataHandle.DataType.STRING, true);
		dataset.put("data", DataHandle.DataType.SMALL_BINARY, false);
		resultsRepository.createDataset(ANALYSIS_NAME, datasetName, dataset);


		DataHandleTuple testdata = new DataHandleTuple();
		testdata.set("PK", UUID.randomUUID().toString() );
		testdata.set("data", "1 2 4 8 16 32 64 128 256 512 1024".getBytes() );
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);


		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		DataHandleTuple r = result.get(0);
		for (String name : testdata.getKeys()) {
			Assert.assertEquals( testdata.get(name), r.get(name)  );
		}
	}
	
	/**
	 * Creates a dataset with no key tag and saves more records to it.
	 * Checks whether all the records were saved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData4() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset5();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData5_1();
		DataHandleTuple testdata2 = getTestData5_2();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata2);
				
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(2, result.size());
	}
	
	/**
	 * Creates a dataset and saves data to it.
	 * Then closes results repository, creates a new 
	 * one and adds a record to it.
	 * Checks whether serial number of new record
	 * is correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData5() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData1_1();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		
		
		resultsRepository.destroy();
		
		ResultsRepositoryImplementation resultsRepository2 = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");		
		
		DataHandleTuple testdata2 = getTestData1_2();
		resultsRepository2.saveData(ANALYSIS_NAME,datasetName, testdata2);
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(2, result.size());
		
		Assert.assertEquals((Long) 2L, result.get(0).getSerial());
		Assert.assertEquals((Long) 3L, result.get(1).getSerial());
		
	}
	
	/**
	 * Creates a dataset, 
	 * then closes results repository, creates a new 
	 * one and adds a record to it.
	 * Checks whether serial number of new record
	 * is correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSaveData6() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();	
		
		resultsRepository.destroy();
		
		ResultsRepositoryImplementation resultsRepository2 = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");		
		
		DataHandleTuple testdata2 = getTestData1_2();
		resultsRepository2.saveData(ANALYSIS_NAME,datasetName, testdata2);
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, null, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		DataHandleTuple record = result.get(0);
		Assert.assertEquals((Long) 2L, record.getSerial());
		
	}


	/**
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadData() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple data1 = getTestData1_1();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, data1);
		DataHandleTuple data2 = getTestData1_2();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, data2);
		
		
		Condition cond1 = Restrictions.conjunction().add(
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class))
				);
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, cond1, null ,null);
		
		Assert.assertEquals(1, result.size());
		
		/* only data1 should be returned */
		DataHandleTuple r = result.get(0);
		for (String name : data1.getKeys()) {
			Assert.assertEquals( data1.get(name), r.get(name)  );
		}
		
		Condition cond2 = Restrictions.conjunction().add(
				Restrictions.eq("key_tag1", data2.get("key_tag1").getValue(Integer.class))
				);
		
		List<DataHandleTuple> result2 = resultsRepository.loadData(ANALYSIS_NAME,datasetName, cond2, null ,null);
		
		/* only data2 should be returned */
		r = result2.get(0);
		for (String name : data2.getKeys()) {
			Assert.assertEquals( data2.get(name), r.get(name)  );
		}
	}
	
	/**
	 * Saves record containing datahandles with null values.
	 * 
	 * Tries whether conditions in loadData method work correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoadData2() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		
		DataHandleTuple testdata = getTestData1_3();
		resultsRepository.saveData(ANALYSIS_NAME,datasetName, testdata);
		
		
		List<DataHandleTuple> result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, Restrictions.isNull("data1"), null, null);
		
		Assert.assertEquals(1, result.size());
		
		result = resultsRepository.loadData(ANALYSIS_NAME,datasetName, Restrictions.isNotNull("data1"), null, null);
		
		Assert.assertEquals(0, result.size());
		
	}
	
	/** Creates trigger and tests whether it exists, then deletes it */
	
	@Test
	public void testCreateDeleteTrigger() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		resultsRepository.createTrigger(trigger);
		
		List<RRTrigger> result = resultsRepository.getTriggers(ANALYSIS_NAME,dataset.getName());
		
		Assert.assertEquals(1, result.size());
		RRTrigger readTrigger = result.get(0);
		for (@SuppressWarnings("unused") String name : data1.getKeys()) {
			Assert.assertEquals( trigger.getDataset(), readTrigger.getDataset()  );
			Assert.assertEquals( trigger.getEvaluator(), readTrigger.getEvaluator()  );
			Assert.assertEquals( trigger.getId(), readTrigger.getId()  );
		}
		
		resultsRepository.deleteTriggers(ANALYSIS_NAME,dataset.getName(), null);
		
		List<RRTrigger> result2 = resultsRepository.getTriggers(ANALYSIS_NAME,dataset.getName());
		
		/* dataset's trigger should be gone */
		Assert.assertEquals(0, result2.size());
	}
	
	/** Tries to create two triggers with the same ID.*/
	
	@Test(expected=ResultsRepositoryException.class)
	public void testCreateTrigger() throws Exception {
		
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		resultsRepository.createTrigger(trigger);
		
		resultsRepository.createTrigger(trigger);
	}
	
	/** Tries to create trigger for TRANSACTION_ENABLED dataset.*/
	
	@Test(expected=ResultsRepositoryException.class)
	public void testCreateTrigger2() throws Exception {
		
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		
		dataset.getDatasetDescriptor().setDatasetType(DatasetType.TRANSACTION_ENABLED);
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		resultsRepository.createTrigger(trigger);
	}
	
	
	/** Creates and deletes dataset with trigger and checks whether associated trigger is also deleted */
	@Test
	public void testDeleteDataset() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		resultsRepository.createTrigger(trigger);
		
		resultsRepository.deleteDataset(ANALYSIS_NAME,dataset.getName());
		
		List<RRTrigger> result2 = resultsRepository.getTriggers(ANALYSIS_NAME,dataset.getName());
		
		Assert.assertEquals(0, result2.size());
	}
	
	/**
	 * Creates dataset with two triggers and tests whether triggers 
	 * fire when data are saved to dataset.
	 */
	@Test
	public void testFireTrigger() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		/* mock evaluator scheduler */
		MockEvaluatorScheduler scheduler = new MockEvaluatorScheduler();
		resultsRepository.setEvaluatorScheduler(scheduler);
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		DataHandleTuple data2 = getTestData1_2();
		
		RRTrigger trigger1 = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.eq("key_tag1", data1.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		RRTrigger trigger2 = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator2",
				Restrictions.eq("key_tag1", data2.get("key_tag1").getValue(Integer.class)),
				getTestTaskDescriptor()
			);
		
		resultsRepository.createTrigger(trigger1);
		resultsRepository.createTrigger(trigger2);
		long original_serial = resultsRepository.getLastSerialNumber();
		
		/* insert data and test that trigger is fired */
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data1);
		//long data1_serial = resultsRepository.getLastSerialNumber();
		
		Assert.assertEquals( 1, scheduler.getScheduleCounter() );
		Assert.assertEquals( trigger1.getId(), scheduler.getLastTriggerId());
		Assert.assertEquals( original_serial, scheduler.getLastLastProcessedSerial());
		
		scheduler.reset();
		
		/* insert data and test that trigger is fired */
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data2);
		//long data2_serial = resultsRepository.getLastSerialNumber();
		
		Assert.assertEquals( 1, scheduler.getScheduleCounter() );
		Assert.assertEquals( trigger2.getId(), scheduler.getLastTriggerId());
		Assert.assertEquals( original_serial, scheduler.getLastLastProcessedSerial());
		
	}
	
	/**
	 * Creates dataset with trigger.
	 * Inserts more data rows and test whether firing 
	 * trigger is correctly postponed when notifyDataProcessed
	 * was not called.
	 */
	@Test
	public void testPostponedFireTrigger() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		/* mock evaluator scheduler */
		MockEvaluatorScheduler scheduler = new MockEvaluatorScheduler();
		resultsRepository.setEvaluatorScheduler(scheduler);
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		//DataHandleTuple data2 = getTestData1_2();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.alwaysTrue(),
				getTestTaskDescriptor()
			);
		
		
		resultsRepository.createTrigger(trigger);
		
		long original_serial = resultsRepository.getLastSerialNumber();
		
		/* insert data and test that trigger is fired */
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data1);
		long data1_serial = resultsRepository.getLastSerialNumber();
		
		Assert.assertEquals( 1, scheduler.getScheduleCounter() );
		Assert.assertEquals( trigger.getId(), scheduler.getLastTriggerId());
		Assert.assertEquals( original_serial, scheduler.getLastLastProcessedSerial());
		
		scheduler.reset();
		
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data1);
		
		/* check that no evaluator was scheduled */
		Assert.assertEquals( 0, scheduler.getScheduleCounter() );
		
		resultsRepository.notifyDataProcessed(trigger.getId(), data1_serial);
		
		Assert.assertEquals( 1, scheduler.getScheduleCounter() );
		Assert.assertEquals( trigger.getId(), scheduler.getLastTriggerId());
		Assert.assertEquals( data1_serial, scheduler.getLastLastProcessedSerial());
		
	}
	
	/**
	 * Creates dataset with trigger.
	 * Inserts more data rows and test whether firing 
	 * trigger is correctly postponed when notifyDataProcessed
	 * was not called.
	 */
	@Test
	public void testPostponedFireTrigger2() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME, "");
		
		/* mock evaluator scheduler */
		MockEvaluatorScheduler scheduler = new MockEvaluatorScheduler();
		resultsRepository.setEvaluatorScheduler(scheduler);
		
		RRDataset dataset = getTestDataset1();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		DataHandleTuple data1 = getTestData1_1();
		DataHandleTuple data2 = getTestData1_2();
		
		RRTrigger trigger = new RRTrigger(
				ANALYSIS_NAME,
				dataset.getName(),
				"evaluator1",
				Restrictions.alwaysTrue(),
				getTestTaskDescriptor()
			);
		
		
		resultsRepository.createTrigger(trigger);
		
		long original_serial = resultsRepository.getLastSerialNumber();
		
		/* insert data and test that trigger is fired */
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data1);
		//long data1_serial = resultsRepository.getLastSerialNumber();
		
		Assert.assertEquals( 1, scheduler.getScheduleCounter() );
		Assert.assertEquals( trigger.getId(), scheduler.getLastTriggerId());
		Assert.assertEquals( original_serial, scheduler.getLastLastProcessedSerial());
		
		scheduler.reset();
		
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data2);
		resultsRepository.saveData(ANALYSIS_NAME,dataset.getName(), data1);
		
		long last_serial = resultsRepository.getLastSerialNumber();
		
		/* check that no evaluator was scheduled */
		Assert.assertEquals( 0, scheduler.getScheduleCounter() );
		
		resultsRepository.notifyDataProcessed(trigger.getId(), last_serial);
		
		Assert.assertEquals( 0, scheduler.getScheduleCounter() );
		
	}
	
	
	
	/**
	 * Creates another dataset and saves data to it.
	 * Reads data again and check its content.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetDataset() throws Exception {
		ResultsRepositoryImplementation resultsRepository = new ResultsRepositoryImplementation(hibernatePluggableModule, DBNAME,"");
		
		RRDataset dataset = getTestDataset3();
		resultsRepository.createDataset(ANALYSIS_NAME,dataset.getName(), dataset.getDatasetDescriptor());
		
		String datasetName = dataset.getName();
		DatasetDescriptor testDescriptor = dataset.getDatasetDescriptor();
		
		DatasetDescriptor result = resultsRepository.getDatasetDescriptor(ANALYSIS_NAME, datasetName);
		
		Assert.assertEquals(result, testDescriptor);
		
		for (String name : testDescriptor.tags()) {
			Assert.assertEquals( testDescriptor.get(name), result.get(name)  );
		}
	}


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
		return dataset;
	}
	
	/**
	 * 
	 * @return testing dataset definition
	 */
	private RRDataset getTestDataset2() {
		DatasetDescriptor dd = new DatasetDescriptor();
		dd.put("ktag1", DataHandle.DataType.STRING, true);
		dd.put("ktag2", DataHandle.DataType.INT, true);
		dd.put("d1", DataHandle.DataType.STRING, false);
		dd.put("d2", DataHandle.DataType.FLOAT, false);
		RRDataset dataset = new RRDataset(ANALYSIS_NAME,"testdataset2",dd);
		return dataset;
	}
	
	/**
	 * 
	 * @return testing dataset definition
	 */
	private RRDataset getTestDataset3() {
		DatasetDescriptor dd = new DatasetDescriptor();
		dd.put("ktag1", DataHandle.DataType.STRING, true);
		dd.put("ktag2", DataHandle.DataType.INT, true);
		//dd.put("d1", DataHandle.DataType.LARGE_BINARY, false);
		dd.put("d2", DataHandle.DataType.SMALL_BINARY, false);
		dd.put("d3", DataHandle.DataType.STRING, false);
		//dd.put("d4", DataHandle.DataType.PIPE, false);
		
		//PIPE and LARGE_BINARY types will be removed soon
		
		RRDataset dataset = new RRDataset(ANALYSIS_NAME,"testdataset3",dd);
		return dataset;
	}
	
	/**
	 * 
	 * @return testing dataset definition
	 */
	private RRDataset getTestDataset4() {
		DatasetDescriptor dd = new DatasetDescriptor();
		
		dd.put("timestamp", DataHandle.DataType.STRING, true);
		dd.put("source", DataHandle.DataType.SMALL_BINARY, false);
		RRDataset dataset = new RRDataset("testanalysis","omniorb_src",dd);
		return dataset;
	}
	
	/**
	 * 
	 * @return testing dataset definition
	 */
	private RRDataset getTestDataset5() {
		DatasetDescriptor dd = new DatasetDescriptor();
		
		dd.put("data", DataHandle.DataType.STRING, false);
		RRDataset dataset = new RRDataset("testanalysis","testdataset5",dd);
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
	private DataHandleTuple getTestData1_3() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("key_tag1", new IntegerDataHandle(5));
		data.set("key_tag2", new StringDataHandle("fddfsadf"));
		data.set("data1", new StringDataHandle(null));
		data.set("data2", new FloatDataHandle(null));
		data.set("data3", DataHandle.create(DataHandle.DataType.UUID, null));
		data.set("data4", DataHandle.create(DataHandle.DataType.LONG, null));
		data.set("data5", new ByteArrayDataHandle(null));
		data.set("data6", new SerializableDataHandle(null));
		return data;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData1_2() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("key_tag1", new IntegerDataHandle(6));
		data.set("key_tag2", new StringDataHandle("lalala"));
		data.set("data1", new StringDataHandle("dddd"));
		data.set("data2", new FloatDataHandle((float) 3.14159));
		data.set("data3", new UUIDDataHandle(UUID.randomUUID()));
		data.set("data4", new LongDataHandle( 462452344));
		data.set("data5", new ByteArrayDataHandle("some string 2".getBytes()));
		data.set("data6", new SerializableDataHandle("serialized string 2"));
		return data;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData3_1() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("ktag1", new StringDataHandle( "fdsaf") );
		data.set("ktag2", new IntegerDataHandle(7) );
		
		data.set("d2", new ByteArrayDataHandle("some rather large string posing as byte array".getBytes()) );
		data.set("d3", new StringDataHandle("some rather large string") );
		// we are deliberately missing d1 and d4
		return data;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData5_1() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("data", new StringDataHandle( "some data") );
		return data;
	}
	
	/**
	 * 
	 * @return testing data for dataset
	 */
	private DataHandleTuple getTestData5_2() {
		DataHandleTuple data = new DataHandleTuple();
		data.set("data", new StringDataHandle( "some data2") );
		return data;
	}
	
	private TaskDescriptor getTestTaskDescriptor() {
		TaskDescriptor td = Factory.TD.createTaskDescriptor();
		return td;
	}
	
	private class MockEvaluatorScheduler implements EvaluatorScheduler {

	    private TaskDescriptor lastTask = null;
		
		@SuppressWarnings("unused")
        public TaskDescriptor getLastTask() {
			return lastTask;
		}

		public UUID getLastTriggerId() {
			return lastTriggerId;
		}

		public long getLastLastProcessedSerial() {
			return lastLastProcessedSerial;
		}

		public int getScheduleCounter() {
			return scheduleCounter;
		}

		private UUID lastTriggerId = null;
		private long lastLastProcessedSerial;
		
		private int scheduleCounter =  0;
		
		@Override
		public void scheduleEvaluator(TaskDescriptor task, UUID triggerId,
				long lastProcessedSerial) throws ResultsRepositoryException {
			scheduleCounter ++;
			lastTask = task;
			lastTriggerId = triggerId;
			lastLastProcessedSerial = lastProcessedSerial; 
		}
		
		public void reset() {
			scheduleCounter = 0;
		}
		
	}
}
