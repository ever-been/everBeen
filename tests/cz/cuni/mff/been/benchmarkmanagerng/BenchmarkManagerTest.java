/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.benchmarkmanagerng;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.derby.implementation.DerbyPluggableModuleImpl;
import cz.cuni.mff.been.pluggablemodule.hibernate.implementation.HibernatePluggableModuleImpl;
import cz.cuni.mff.been.task.Task;

/**
 * 
 *
 *  @author: Jiri Tauber
 */
public class BenchmarkManagerTest {
	public static String TEST_DIR = "data/tests/benchmarkmanagerng"; 
	
	private class MockPluggableModuleManager implements PluggableModuleManager {
		public ClassLoader getClassLoader() {
			return this.getClass().getClassLoader();
		}

		public PluggableModule getModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
			if( moduleDescriptor.getName().equals("derby")){
				return new DerbyPluggableModuleImpl(this);
			}
			if( moduleDescriptor.getName().equals("hibernate")){
				return new HibernatePluggableModuleImpl(this);
			}
			if( moduleDescriptor.getName().equals("MockGeneratorModule")){
				return new MockGeneratorModule(this);
			}
			return null;
		}

		public boolean isModuleLoaded(PluggableModuleDescriptor moduleDescriptor) {
			return false;
		}

		public PluggableModule loadModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
			return getModule(moduleDescriptor);
		}
	}

	private static class TestDataProvider {
		public static String ANALYSIS1_NAME = "Test_Analysis";
		public static String ANALYSIS2_NAME = "Simple_test";
		public static String ANALYSIS1_DESCRIPTION = "Test description";
		public static String ANALYSIS2_DESCRIPTION = "Simple description";
		public static String GENERATOR_NAME = "MockGeneratorModule";
		public static String GENERATOR_VERSION = "0.0";

		/*public static Configuration generatorConfig(){
			return null;
		}*/

		public static BMGenerator generator(){
			BMGenerator result = new BMGenerator(GENERATOR_NAME, GENERATOR_VERSION);
			result.setConfiguration(MockGeneratorModule.getValidConfig());
			return result;
		}

		public static Analysis analysis1() throws AnalysisException{
			return new Analysis(ANALYSIS1_NAME, ANALYSIS1_DESCRIPTION, generator());
		}

		public static Analysis analysis2() throws AnalysisException{
			return new Analysis(ANALYSIS2_NAME, ANALYSIS2_DESCRIPTION, generator());
		}
	}

	private BenchmarkManagerImplementation bmng;

	//----- Test settings -----//
	@BeforeClass
	public static void setUpClass() throws Exception {
		// set where BMng should create the derby database
		System.setProperty(Task.PROP_DIR_WORK, System.getenv("BEEN_HOME")+"/"+TEST_DIR);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		DerbyPluggableModule derby;
		derby = new DerbyPluggableModuleImpl(new MockPluggableModuleManager());
		derby.startEngine(System.getProperty(Task.PROP_DIR_WORK), false);
		derby.dropDatabase(BenchmarkManagerImplementation.ANALYSES_DATABASE);
		derby.stopEngine();

		bmng = new BenchmarkManagerImplementation(new MockPluggableModuleManager());
	}
	
	@After
	public void tearDown() throws Exception {
		bmng.destroy();
	}

	//----- Tests -----//
	/**
	 * Creates two coppies of {@link Analysis} with same parameters
	 * and compares them. Then changes one of the parameters and repeats.
	 */
	@Test
	public void testCompareAnalyses() throws Exception {
		Analysis analysis1 = TestDataProvider.analysis1();
		Analysis analysis2 = TestDataProvider.analysis1();
		assertTrue("Same analyses are not equal", analysis1.equals(analysis2));
		analysis2 = new Analysis("different",analysis1.getDescription(),analysis1.getGenerator());
		assertTrue("Analyses with different names are equal", !analysis1.equals(analysis2));
		analysis2 = TestDataProvider.analysis1();
		analysis2.setDescription("different");
		assertTrue("Analyses with different descriptions are equal", !analysis1.equals(analysis2));
		analysis2.setDescription(analysis1.getDescription());
		analysis2.setGenerator(null);
		assertTrue("Analyses with different generators are equal", !analysis1.equals(analysis2));
		analysis2.setGenerator(TestDataProvider.generator());
		analysis2.getGenerator().setConfiguration(null);
		assertTrue("Analyses with different generator settings are equal", !analysis1.equals(analysis2));
	}
	
	@Test
	public void testCreateAnalysis() throws Exception {
		Analysis analysis1 = TestDataProvider.analysis1();
		Analysis analysis2 = TestDataProvider.analysis2();
		bmng.createAnalysis(analysis1);
		bmng.createAnalysis(analysis2);
		Collection<Analysis> list = bmng.getAnalyses();
		assertTrue("The number of analyses should be 2 but is "+list.size(), list.size() == 2);
		assertTrue("Retrieved analyses don't contain the first saved", list.contains(analysis1));
		assertTrue("Retrieved analyses don't contain the second saved", list.contains(analysis2));
	}
	
	@Test
	public void testPersistence() throws Exception {
		Analysis analysis = TestDataProvider.analysis1();
		bmng.createAnalysis(analysis);

		// restart the service
		bmng.destroy();
		bmng = new BenchmarkManagerImplementation(new MockPluggableModuleManager());

		Collection<Analysis> list = bmng.getAnalyses();
		assertTrue("The number of analyses should be 1 but is "+list.size(), list.size() == 1);
		assertTrue("Retrieved analyses don't contain the saved one", list.contains(analysis));
	}

	@Test
	public void testUpdate() throws Exception {
		Analysis analysis = TestDataProvider.analysis1();
		bmng.createAnalysis(analysis);
		Collection<Analysis> list = bmng.getAnalyses();
		assertTrue("Retrieved analyses don't contain the saved one", list.contains(analysis));
		analysis = list.iterator().next();
		analysis.setDescription(TestDataProvider.ANALYSIS1_NAME);
		bmng.updateAnalysis(analysis);
		list = bmng.getAnalyses();
		assertTrue("The number of analyses should be 1 but is "+list.size(), list.size() == 1);
		assertTrue("Retrieved analyses don't contain the saved one", list.contains(analysis));
	}

	//----- Utility functions -----//
	@SuppressWarnings("unused")
	private void printAnalyses(Collection<Analysis> list){
		for(Analysis analysis : list){
			BMGenerator generator = analysis.getGenerator();
			StringBuilder str = new StringBuilder(analysis.getName());
			str.append(" => ");
			str.append(analysis.getDescription());
			str.append(" <= ");
			if( generator != null ){
				str.append(generator.getPackageName());
			} else {
				str.append(" null ");
			}
			str.append('\n');
			System.out.print(str);
		}	
		
	}
}
