package cz.cuni.mff.been.benchmarkmanagerng.common.planner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.junit.Test;


public class SimpleDownloadBuildRunPlannerTest {
	
	private Set<String> suites;
	
	public SimpleDownloadBuildRunPlannerTest() {
		suites = new HashSet<String>();
		
		suites.add("Suite1");
		suites.add("Suite2");
		suites.add("Suite3");
	}
	
	
	
	@Test
	public void testPlan() throws Exception {
		DBRPlanner planner = new SimpleDownloadBuildRunPlanner(suites,
				100, 1, 4, 2, 
				2.0f, 15, 15,  new HashSet<SourceKey>() );
		
		planner.plan(getCvsSources1(), getSources1(), getBuilds1(), getBuilds1(), getRuns1());
		
		Assert.assertEquals(2, planner.getDownloadActivities().size());
		Assert.assertEquals("125", planner.getDownloadActivities().get(0).getVersion()); 
		Assert.assertEquals("123", planner.getDownloadActivities().get(1).getVersion());
		
		
		Assert.assertEquals(3, planner.getBuildActivities().size());
		Assert.assertEquals("124", planner.getBuildActivities().get(0).getVersion()); 
		Assert.assertEquals("122", planner.getBuildActivities().get(1).getVersion());
		Assert.assertEquals("121", planner.getBuildActivities().get(2).getVersion());
		
		
		List<RunActivity> runAct =  planner.getRunActivities();
		
		Assert.assertEquals(2, runAct.size());
		Assert.assertEquals("121", runAct.get(0).getVersion()); 
		Assert.assertEquals(1, runAct.get(0).getBuildNumber());
		Assert.assertEquals(1, runAct.get(0).getRunNumber());
		Assert.assertEquals(suites, runAct.get(0).getSuites());
		
		
		Assert.assertEquals("121", runAct.get(1).getVersion()); 
		Assert.assertEquals(2, runAct.get(1).getBuildNumber());
		Assert.assertEquals(1, runAct.get(1).getRunNumber());
		Assert.assertEquals(suites, runAct.get(1).getSuites());
	}
	
	@Test
	public void testPlan2() throws Exception {
		DBRPlanner planner =  new SimpleDownloadBuildRunPlanner(suites,
				100, 1, 4, 2, 
				2.0f, 15, 15,  new HashSet<SourceKey>() ); 
			
		
		planner.plan(new HashSet<SourceKey>(), getSources2(), getBuilds2(), getBuilds2(), getRuns2());
		
		Assert.assertEquals(2, planner.getBuildActivities().size()); 
		Assert.assertEquals("122", planner.getBuildActivities().get(0).getVersion());
		Assert.assertEquals("121", planner.getBuildActivities().get(1).getVersion());
	
	}
	
	@Test
	public void testPlan2_1() throws Exception {
		DBRPlanner planner = new SimpleDownloadBuildRunPlanner(suites,
				100, 1, 4, 2, 
				2.0f, 15, 15,  new HashSet<SourceKey>() );
		
		planner.plan(new HashSet<SourceKey> (), getSources2(), getBuilds2(), getBuilds2(),  getRuns2());
		
		Assert.assertEquals(2, planner.getBuildActivities().size());
		Assert.assertEquals("122", planner.getBuildActivities().get(0).getVersion());
		Assert.assertEquals("121", planner.getBuildActivities().get(1).getVersion());
	}
	
	/* test build activity queue (it should be sorted  existing build number asc, version desc) */	
	@Test 
	public void testPlan3() throws Exception {
		
		Set<String> suiteSet = new HashSet<String>();
		suiteSet.add("DEFAULT");
		
		DBRPlanner planner = new SimpleDownloadBuildRunPlanner(suiteSet,
				4, 1, 1, 1, 
				2.0f, 3, 10,  new HashSet<SourceKey>() );
		
		Set<SourceKey> sources = new HashSet<SourceKey>();
		
		/* versions with one build each */
		MultiMap<BuildKey, BuildProps> plannedBuilds = new MultiHashMap<BuildKey, BuildProps>();
		plannedBuilds.put( new BuildKey("v1"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v2"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v3"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v4"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v5"), new BuildProps(1));
		
		MultiMap<BuildKey, BuildProps> availableBinaries = new MultiHashMap<BuildKey, BuildProps>();
		availableBinaries.put( new BuildKey("v6"), new BuildProps(1));
		
		availableBinaries.put( new BuildKey("v7"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v7"), new BuildProps(2));
		
		availableBinaries.put( new BuildKey("v8"), new BuildProps(1));
		
		
		availableBinaries.put( new BuildKey("v9"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v9"), new BuildProps(2));
		
		MultiHashMap<RunKey, RunProps> runs = new MultiHashMap<RunKey, RunProps>();
		
		addRuns(runs, "v1", 1, 4);
		addRuns(runs, "v2", 1, 4);
		addRuns(runs, "v3", 1, 4);
		addRuns(runs, "v4", 1, 4);
		addRuns(runs, "v5", 1, 4);
		addRuns(runs, "v6", 1, 4);
		addRuns(runs, "v7", 1, 4);
		addRuns(runs, "v9", 1, 4);
		
		planner.plan(new HashSet<SourceKey> (), sources, plannedBuilds, availableBinaries,  runs);
		
		//System.out.println(planner.getBuildActivities());
		//System.out.println(planner.getRunActivities());
		
		Assert.assertEquals(2, planner.getBuildActivities().size());
		Assert.assertEquals("v8", planner.getBuildActivities().get(0).getVersion());
		Assert.assertEquals("v6", planner.getBuildActivities().get(1).getVersion());
	}
	
	@Test
	public void testPlan4() throws Exception {
		
		Set<String> suiteSet = new HashSet<String>();
		suiteSet.add("DEFAULT");
		
		DBRPlanner planner = new SimpleDownloadBuildRunPlanner(suiteSet,
				8, 1, 1, 1, 
				2.0f, 3, 10,  new HashSet<SourceKey>() );
		
		Set<SourceKey> sources = new HashSet<SourceKey>();
		
		/* versions with one build each */
		MultiMap<BuildKey, BuildProps> plannedBuilds = new MultiHashMap<BuildKey, BuildProps>();
		plannedBuilds.put( new BuildKey("v1"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v2"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v3"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v4"), new BuildProps(1));
		plannedBuilds.put( new BuildKey("v5"), new BuildProps(1));
		
		MultiMap<BuildKey, BuildProps> availableBinaries = new MultiHashMap<BuildKey, BuildProps>();
		availableBinaries.put( new BuildKey("v1"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v2"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v3"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v4"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v5"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v6"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v7"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v8"), new BuildProps(1));
		availableBinaries.put( new BuildKey("v9"), new BuildProps(1));
		
		MultiHashMap<RunKey, RunProps> runs = new MultiHashMap<RunKey, RunProps>();
		
		addRuns(runs, "v1", 1, 4);
		addRuns(runs, "v2", 1, 3);
		addRuns(runs, "v3", 1, 5);
		
		planner.plan(new HashSet<SourceKey> (), sources, plannedBuilds, availableBinaries,  runs);
		
		//System.out.println(planner.getBuildActivities());
		//System.out.println(planner.getRunActivities());
		
		Assert.assertEquals(1, planner.getBuildActivities().size());
		Assert.assertEquals("v9", planner.getBuildActivities().get(0).getVersion());
		
		Assert.assertEquals(7, planner.getRunActivities().size());
		Assert.assertEquals("v9", planner.getRunActivities().get(0).getVersion());
		Assert.assertEquals("v8", planner.getRunActivities().get(1).getVersion());
		Assert.assertEquals("v7", planner.getRunActivities().get(2).getVersion());
		Assert.assertEquals("v6", planner.getRunActivities().get(3).getVersion());
		Assert.assertEquals("v5", planner.getRunActivities().get(4).getVersion());
		Assert.assertEquals("v4", planner.getRunActivities().get(5).getVersion());
		Assert.assertEquals("v2", planner.getRunActivities().get(6).getVersion());
	}
	
	
	private void addRuns(MultiHashMap<RunKey, RunProps> dest, String version, int buildNumber, int runCount) {
		for(int i = 1; i <= runCount; i++) {
			dest.put(new RunKey(version, buildNumber), new RunProps(i, "DEFAULT"));
		}
	}
	
	private Set<SourceKey> getCvsSources1() {
		Set<SourceKey> sources = new HashSet<SourceKey>();
		
		sources.add( new SourceKey("121"));
		sources.add( new SourceKey("122"));
		sources.add( new SourceKey("123"));
		sources.add( new SourceKey("124"));
		sources.add( new SourceKey("125"));
		
		return sources;
	}
	
	private Set<SourceKey> getSources1() {
		Set<SourceKey> sources = new HashSet<SourceKey>();
		
		sources.add( new SourceKey("121"));
		sources.add( new SourceKey("122"));
		sources.add( new SourceKey("124"));
		
		return sources;
	}
	
	MultiMap<BuildKey, BuildProps> getBuilds1() {
		MultiMap<BuildKey, BuildProps> builds = new MultiHashMap<BuildKey, BuildProps>();
		
		builds.put( new BuildKey("121"), new BuildProps(1));
		builds.put( new BuildKey("121"), new BuildProps(2));
		
		
		return builds;
		
	}
	
	MultiMap<RunKey, RunProps> getRuns1() {
		return new MultiHashMap<RunKey, RunProps>();
	}
	
	private Set<SourceKey> getSources2() {
		Set<SourceKey> sources = new HashSet<SourceKey>();
		
		sources.add( new SourceKey("121"));
		sources.add( new SourceKey("122"));
		return sources;
	}
	
	MultiMap<BuildKey, BuildProps> getBuilds2() {
		MultiMap<BuildKey, BuildProps> builds = new MultiHashMap<BuildKey, BuildProps>();
		
		builds.put( new BuildKey("121"), new BuildProps(1));
		return builds;
	}
	
	MultiMap<RunKey, RunProps> getRuns2() {
		return new MultiHashMap<RunKey, RunProps>();
	}

}


