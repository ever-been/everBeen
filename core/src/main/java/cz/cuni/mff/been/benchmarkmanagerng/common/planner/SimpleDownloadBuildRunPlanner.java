/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jan Tattermusch
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.benchmarkmanagerng.common.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * Simple generic implementation of DRBPlanner. Provides planning that can be
 * suitable for most benchmarks that have download-build-run workflow.
 * 
 * SimpleDownloadBuildRunPlanner behavior can but customized by setting some of
 * its properties.
 * 
 * @author Jan Tattermusch
 * 
 */
public class SimpleDownloadBuildRunPlanner implements DBRPlanner {

	/**
	 * list of download activities in computed plan
	 */
	private List<DownloadActivity> downloadActivities;

	/**
	 * list of build activities in computed plan
	 */
	private List<BuildActivity> buildActivities;

	/**
	 * list of run activities in computed plan
	 */
	private List<RunActivity> runActivities;

	/**
	 * set of suites that are suppoted by given benchmark
	 */
	private final Set<String> suites;

	/**
	 * current number of computation units to spend
	 */
	private int units;

	/**
	 * number of computation units that planner has for every plan() invocation
	 */
	private final int workloadUnits;

	/**
	 * Unit cost of download activity.
	 */
	private final int downloadCost;

	/**
	 * Unit cost of build activity.
	 */
	private final int buildCost;

	/**
	 * Unit cost of run activity.
	 */
	private final int runCost;

	/**
	 * run / build ratio. Planner will try to keep this ratio when generating
	 * new activities
	 */
	private final float runBuildRatio;

	/**
	 * build count limit (not more than this number of builds will be performed)
	 */
	private final int buildLimit;

	/**
	 * run count limit (not more than this number of runs will be performed for
	 * every build)
	 */
	private final int runLimit;

	/**
	 * set of blacklisted versions. No activities with these versions will be
	 * generated.
	 */
	private final Set<SourceKey> versionBlacklist;

	private int buildCountSum;

	private int runCountSum;

	/**
	 * Contructs new instance of simple download-build-run planner
	 * 
	 * @param suites
	 *            set of suites
	 * @param workloadUnits
	 *            number of computation units that planner can spend
	 * @param buildLimit
	 *            build count limit
	 * @param runLimit
	 *            run count limit (for every build)
	 */
	public SimpleDownloadBuildRunPlanner(Set<String> suites, int workloadUnits,
			int downloadCost, int buildCost, int runCost, float runBuildRatio,
			int buildLimit, int runLimit, Set<SourceKey> versionBlacklist) {
		this.suites = suites;
		this.workloadUnits = workloadUnits;
		this.downloadCost = downloadCost;
		this.buildCost = buildCost;
		this.runCost = runCost;
		this.runBuildRatio = runBuildRatio;
		this.buildLimit = buildLimit;
		this.runLimit = runLimit;
		this.versionBlacklist = versionBlacklist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler.planner.
	 * DBRPlanner#getDownloadActivities()
	 */
	@Override
	public List<DownloadActivity> getDownloadActivities() {
		return downloadActivities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler.planner.
	 * DBRPlanner#getBuildActivities()
	 */
	@Override
	public List<BuildActivity> getBuildActivities() {
		return buildActivities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler.planner.
	 * DBRPlanner#getRunActivities()
	 */
	@Override
	public List<RunActivity> getRunActivities() {
		return runActivities;
	}

	/**
	 * Creates an execution plan (download, build and run activities) and stores
	 * it in this object's properties.
	 * 
	 * This planner generates activities according to this rules: 1. if there
	 * are some not-yet-downloaded external sources they'll be downloaded first
	 * 2. if there are some binaries that can be run (according to run count
	 * limit), run them. 3. if there are some downloaded sources that can be run
	 * (according to build count limit), build them.
	 * 
	 * If build count limit and run count limit for every build are reached no
	 * more activities will be generated.
	 * 
	 * Planner only makes plan with specified unit cost. If unit number becomes
	 * zero or less, no more activities will be added into plan.
	 * 
	 * @param externalSources
	 *            set of sources available in external source (version control)
	 * @param sources
	 *            set of downloaded sources
	 * @param buildLog
	 *            set of already planned builds
	 * @param availableBinaries
	 *            set of available binaries
	 * @param runLog
	 *            set of already planned runs
	 */
	@Override
	public void plan(
			Set<SourceKey> externalSources,
			Set<SourceKey> sources,
			MultiMap<BuildKey, BuildProps> buildLog,
			MultiMap<BuildKey, BuildProps> availableBinaries,
			MultiMap<RunKey, RunProps> runLog) {

		units = workloadUnits;

		downloadActivities = new ArrayList<DownloadActivity>();
		buildActivities = new ArrayList<BuildActivity>();
		runActivities = new ArrayList<RunActivity>();

		List<SourceKey> downloadQueue = downloadQueue(externalSources, sources);

		/* planned builds and available binaries are merged together */
		MultiMap<BuildKey, BuildProps> allBuilds = mergeBuilds(
				buildLog,
				availableBinaries);

		/* count how many already planned builds we have */
		buildCountSum = 0;
		for (BuildKey k : allBuilds.keySet()) {
			buildCountSum += allBuilds.get(k).size();
		}

		/* count how many already planned runs we have */
		runCountSum = 0;
		for (RunKey k : runLog.keySet()) {
			runCountSum += runLog.get(k).size();
		}

		/*
		 * builds are planned according to already planned builds, available
		 * binaries and available sources
		 */
		LinkedList<BuildKey> buildQueue = buildQueue(sources, allBuilds);

		/*
		 * run queue is made based on already planned runs and available
		 * binaries
		 */
		LinkedList<RunKey> runQueue = runQueue(availableBinaries, runLog);

		logInfo("Workload units to distribute: " + units);

		for (SourceKey k : versionBlacklist) {
			logInfo(k.getVersion() + " blacklisted");
		}

		for (SourceKey sourceKey : downloadQueue) {
			if (!versionBlacklist.contains(sourceKey)) {
				DownloadActivity activity = createDownloadActivity(sourceKey);
				addActivity(activity);

			} else {
				logInfo("New download-build-run workflow for version "
						+ sourceKey.getVersion()
						+ " would have been scheduled but the version is BLACKLISTED.");
			}
			logInfo("Remaining workload units: " + units);

			if (units <= 0) {
				break;
			}
		}

		while (units > 0) {
			logInfo("Currently having " + buildCountSum + " builds and "
					+ runCountSum + " runs, desired run-build ratio is "
					+ runBuildRatio);
			boolean tryScheduleRun = buildCountSum * runBuildRatio >= runCountSum;

			if (tryScheduleRun) {
				logInfo("New run should be scheduled to adjust current Run/Build ratio.");
				if (runQueue.isEmpty()) {
					logInfo("No runs can be performed (no ready builds or run count limit reached?)");
				}
			} else {
				logInfo("No need to schedule new runs to adjust current Run/Build ratio.");
			}

			if ((tryScheduleRun || buildQueue.isEmpty()) && !runQueue.isEmpty()) {
				if (buildQueue.isEmpty()) {
					logInfo("Build queue is empty, will generate new run.");
				}

				RunKey runKey = runQueue.removeFirst();

				if (versionBlacklist.contains(runKey.getVersion())) {
					logInfo("New run workflow for build "
							+ runKey.getVersion()
							+ "/"
							+ runKey.getBuildNumber()
							+ " would have been scheduled but the version is BLACKLISTED.");
					logInfo("Remaining workload units: " + units);
					continue;
				}

				RunActivity activity = createRunActivity(
						runKey,
						runLog.get(runKey));
				addActivity(activity);

				logInfo("Remaining workload units: " + units);
				continue;
			}

			if (!buildQueue.isEmpty()) {
				BuildKey buildKey = buildQueue.removeFirst();

				if (versionBlacklist.contains(buildKey.getVersion())) {
					logInfo("New build-run workflow for version "
							+ buildKey.getVersion()
							+ " would have been scheduled but the version is BLACKLISTED.");
					logInfo("Remaining workload units: " + units);
					continue;
				}

				BuildActivity activity = createBuildActivity(
						buildKey,
						allBuilds.get(buildKey));
				addActivity(activity);

				logInfo("Remaining workload units: " + units);
				continue;
			} else {
				logInfo("No more builds can be performed (no finished downloads or build count limit reached?)");
			}

			if (buildQueue.isEmpty() && runQueue.isEmpty()) {
				logInfo("No more runs and builds could be scheduled. " + units
						+ " workload units not used.");
				break;
			}
		}

	}

	/**
	 * Merges two build multi maps together so that every key's values are
	 * merged. One key will contain only distinct values.
	 * 
	 * @param buildLog
	 * @param availableBinaries
	 * @return merged multi map
	 */
	private MultiMap<BuildKey, BuildProps> mergeBuilds(
			MultiMap<BuildKey, BuildProps> buildLog,
			MultiMap<BuildKey, BuildProps> availableBinaries) {

		MultiMap<BuildKey, BuildProps> result = new MultiHashMap<BuildKey, BuildProps>();

		Set<BuildKey> union = new HashSet<BuildKey>();

		union.addAll(buildLog.keySet());
		union.addAll(availableBinaries.keySet());

		for (BuildKey key : union) {
			Collection<BuildProps> availableBinariesProps = availableBinaries
					.get(key);
			Collection<BuildProps> buildLogProps = buildLog.get(key);

			Set<BuildProps> propSet = new HashSet<BuildProps>();

			if (availableBinariesProps != null) {
				propSet.addAll(availableBinariesProps);
			}

			if (buildLogProps != null) {
				propSet.addAll(buildLogProps);
			}

			result.putAll(key, propSet);
		}

		return result;
	}

	/**
	 * Adds a new activity to execution plan
	 * 
	 * @param activity
	 *            activity to add
	 */
	private void addActivity(RunActivity activity) {
		runActivities.add(activity);
		runCountSum += 1;
		units -= calculateActivityCost(activity);
		logInfo("New run workflow for build " + activity.getVersion() + "/"
				+ activity.getBuildNumber() + " scheduled (new run added).");
	}

	/**
	 * Adds a new activity to execution plan
	 * 
	 * @param activity
	 *            activity to add
	 */
	private void addActivity(BuildActivity activity) {
		buildActivities.add(activity);
		buildCountSum += 1;
		runCountSum += activity.getRunActivities().size();
		units -= calculateActivityCost(activity);
		logInfo("New build-run workflow for version " + activity.getVersion()
				+ " scheduled (new build and run added).");
	}

	/**
	 * Adds a new activity to execution plan
	 * 
	 * @param activity
	 *            activity to add
	 */
	private void addActivity(DownloadActivity activity) {
		downloadActivities.add(activity);

		buildCountSum += activity.getBuildActivities().size();
		for (BuildActivity a : activity.getBuildActivities()) {
			runCountSum += a.getRunActivities().size();
		}
		units -= calculateActivityCost(activity);
		logInfo("New download-build-run for version " + activity.getVersion()
				+ " scheduled (new dowload, build and run added).");
	}

	/**
	 * 
	 * @param activity
	 *            activity for cost evaluation
	 * @return unit cost of given activity
	 */
	private int calculateActivityCost(RunActivity activity) {
		return runCost;
	}

	/**
	 * 
	 * @param activity
	 *            activity for cost evaluation
	 * @return unit cost of given activity
	 */
	private int calculateActivityCost(BuildActivity activity) {
		int cost = 0;
		for (RunActivity executeActivity : activity.getRunActivities()) {
			cost += calculateActivityCost(executeActivity);
		}
		cost += buildCost;
		return cost;
	}

	/**
	 * 
	 * @param activity
	 *            activity for cost evaluation
	 * @return unit cost of given activity
	 */
	private int calculateActivityCost(DownloadActivity activity) {
		int cost = 0;
		for (BuildActivity buildActivity : activity.getBuildActivities()) {
			cost += calculateActivityCost(buildActivity);
		}
		cost += downloadCost;
		return cost;
	}

	/**
	 * Creates run activity and sets its run number to MAX(run number) + 1
	 * 
	 * @param runKey
	 *            run key
	 * @param finishedRuns
	 *            list of finished runs
	 * @return created run activity
	 */
	private RunActivity createRunActivity(
			RunKey runKey,
			Collection<RunProps> finishedRuns) {

		String version = runKey.getVersion();
		int buildNumber = runKey.getBuildNumber();

		int runNumber;

		/* set run number */
		if (finishedRuns == null) {
			runNumber = 1;
		} else {
			runNumber = 1;
			for (RunProps props : finishedRuns) {
				if (props.getRunNumber() >= runNumber) {
					runNumber = props.getRunNumber() + 1;
				}
			}
		}
		RunActivity result = new RunActivity(
				version,
				buildNumber,
				runNumber,
				new TreeSet<String>(suites));
		return result;
	}

	/**
	 * Creates build activity with dependent run activities and sets it's build
	 * number to MAX(build number) + 1
	 * 
	 * @param buildKey
	 *            build key
	 * @param finishedBuilds
	 *            list of finished builds
	 * @return created build activity
	 */
	private BuildActivity createBuildActivity(
			BuildKey buildKey,
			Collection<BuildProps> finishedBuilds) {

		String version = buildKey.getVersion();
		int buildNumber;

		/* set build number */
		if (finishedBuilds == null) {
			buildNumber = 1;
		} else {
			buildNumber = 1;
			for (BuildProps props : finishedBuilds) {
				if (props.getBuildNumber() >= buildNumber) {
					buildNumber = props.getBuildNumber() + 1;
				}
			}
		}

		BuildActivity result = new BuildActivity(version, buildNumber);
		result.getRunActivities().add(
				createRunActivity(new RunKey(version, buildNumber), null));

		return result;
	}

	/**
	 * Creates download activity with dependent download activities
	 * 
	 * @param sourceKey
	 *            build key
	 * @return download activity
	 */
	private DownloadActivity createDownloadActivity(SourceKey sourceKey) {

		String version = sourceKey.getVersion();

		DownloadActivity result = new DownloadActivity(version);
		result.getBuildActivities().add(
				createBuildActivity(new BuildKey(version), null));
		return result;
	}

	/**
	 * Creates download queue as a difference between set of externally
	 * available sources and already downloaded sources.
	 * 
	 * @param cvsSources
	 *            external sources
	 * @param sources
	 *            already downloaded sources
	 * @return queue of download activities sorted by priority
	 */
	private LinkedList<SourceKey> downloadQueue(
			Set<SourceKey> cvsSources,
			Set<SourceKey> sources) {
		SortedSet<SourceKey> toDownload = new TreeSet<SourceKey>(cvsSources);
		toDownload.removeAll(sources);

		/* sort in descending order */
		LinkedList<SourceKey> result = new LinkedList<SourceKey>();

		/* make free space for result */
		for (int j = 0; j < toDownload.size(); j++) {
			result.add(null);
		}

		int i = toDownload.size() - 1;
		for (SourceKey key : toDownload) {
			result.set(i, key);
			i--;
		}
		return result;
	}

	/**
	 * Creates build queue based on trying to build sources that were built
	 * least times and number of builds does not exceed build limit.
	 * 
	 * @param sources
	 *            available sources
	 * @param allBuilds
	 *            already planned builds and already available binaries
	 * 
	 * @return build queue
	 */
	private LinkedList<BuildKey> buildQueue(
			Set<SourceKey> sources,
			MultiMap<BuildKey, BuildProps> allBuilds) {

		MultiMap<Integer, BuildKey> buildCountIndex = new MultiHashMap<Integer, BuildKey>();

		/* to all sources that were not built at all assign build count 0 */
		for (SourceKey key : sources) {
			BuildKey buildKey = new BuildKey(key.getVersion());

			Collection<BuildProps> b = allBuilds.get(buildKey);
			if (b == null || b.size() == 0) {
				buildCountIndex.put(0, buildKey);
			}
		}

		int maxBuildCount = 0;

		/*
		 * assign appropriate number of finished builds to existing source
		 * versions
		 */
		for (BuildKey key : allBuilds.keySet()) {
			Collection<BuildProps> b = allBuilds.get(key);
			int length = b.size();
			buildCountIndex.put(length, key);

			if (maxBuildCount < length)
				maxBuildCount = length;
		}

		/* construct the resulting queue based on number of builds */
		LinkedList<BuildKey> result = new LinkedList<BuildKey>();

		for (int i = 0; i <= maxBuildCount; i++) {

			List<BuildKey> toSort = new ArrayList<BuildKey>();

			// builds with i builds
			Collection<BuildKey> builds = buildCountIndex.get(i);
			if (builds != null) {
				for (BuildKey buildKey : builds) {
					Collection<BuildProps> buildsOfVersion = allBuilds
							.get(buildKey);
					if (buildsOfVersion == null) {
						toSort.add(buildKey);
					} else if (buildsOfVersion.size() < buildLimit) {
						toSort.add(buildKey);
					}
				}
			}

			sortBuildKeys(toSort);
			result.addAll(toSort);
		}

		return result;
	}

	/**
	 * Sorts build keys by version in descending order
	 * 
	 * @param toSort
	 *            sort
	 */
	private void sortBuildKeys(List<BuildKey> toSort) {

		Collections.sort(toSort, new Comparator<BuildKey>() {

			@Override
			public int compare(BuildKey o1, BuildKey o2) {
				return -1 * (o1.getVersion().compareTo(o2.getVersion()));

			}

		});
	}

	/**
	 * Creates run queue based on available binaries and already planned runs.
	 * 
	 * @param availableBinaries
	 *            available binaries
	 * @param runLog
	 *            already planned runs
	 * @return index that shows which source version was run how many times
	 */
	private LinkedList<RunKey> runQueue(
			MultiMap<BuildKey, BuildProps> availableBinaries,
			MultiMap<RunKey, RunProps> runLog) {
		MultiMap<Integer, RunKey> runCountIndex = new MultiHashMap<Integer, RunKey>();

		/* to all builds that were not run at all assign run count 0 */
		for (BuildKey buildKey : availableBinaries.keySet()) {

			for (BuildProps buildProps : availableBinaries.get(buildKey)) {
				RunKey runKey = new RunKey(
						buildKey.getVersion(),
						buildProps.getBuildNumber());

				Collection<RunProps> r = runLog.get(runKey);
				if (r == null || r.size() == 0) {
					runCountIndex.put(0, runKey);
				}
			}
		}

		int maxRunCount = 0;
		/*
		 * assign appropriate number of finished builds to existing source
		 * versions
		 */
		for (RunKey key : runLog.keySet()) {
			Collection<RunProps> r = runLog.get(key);
			int length = r.size();

			runCountIndex.put(length, key);

			if (maxRunCount < length)
				maxRunCount = length;
		}

		/* construct the resulting queue based on number of runs */
		LinkedList<RunKey> result = new LinkedList<RunKey>();

		for (int i = 0; i <= maxRunCount; i++) {
			List<RunKey> toSort = new ArrayList<RunKey>();

			Collection<RunKey> runs = runCountIndex.get(i);
			if (runs != null) {
				for (RunKey runKey : runs) {
					Collection<RunProps> runsOfBinary = runLog.get(runKey);

					if (runsOfBinary == null) {
						toSort.add(runKey);
					} else if (runsOfBinary.size() < runLimit) {
						toSort.add(runKey);
					}
				}
			}

			sortRunKeys(toSort);

			// System.out.println(i);
			// System.out.println(toSort);

			result.addAll(toSort);
		}

		return result;

	}

	/**
	 * Sorts run keys by version in descending order and secondarily by build
	 * number in ascending order
	 * 
	 * @param toSort
	 *            sort
	 */
	private void sortRunKeys(List<RunKey> toSort) {

		Collections.sort(toSort, new Comparator<RunKey>() {

			@Override
			public int compare(RunKey o1, RunKey o2) {

				int res = -1 * (o1.getVersion().compareTo(o2.getVersion()));

				if (res == 0) {
					res = Integer.valueOf(o1.getBuildNumber()).compareTo(
							o2.getBuildNumber());
				}
				return res;

			}

		});
	}

	/**
	 * Logs an info message
	 * 
	 * @param message
	 *            message to log
	 */
	private void logInfo(String message) {
		if (CurrentTaskSingleton.getTaskHandle() == null) {
			System.err.println(message);
		} else {
			CurrentTaskSingleton.getTaskHandle().logInfo(message);
		}
	}

}
