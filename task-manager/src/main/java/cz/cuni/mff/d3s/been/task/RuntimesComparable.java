package cz.cuni.mff.d3s.been.task;

import java.util.Comparator;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

/**
 * 
 * Compares Runtimes according scheduling needs.
 * 
 * This version takes into account only number of running tasks.
 * 
 * @author Martin Sixta
 */
public class RuntimesComparable implements Comparator<RuntimeInfo> {
	@Override
	public int compare(RuntimeInfo o1, RuntimeInfo o2) {
		int tasks1 = o1.getTaskCount();
		int tasks2 = o2.getTaskCount();
		return (tasks1 < tasks2 ? -1 : (tasks1 == tasks2 ? 0 : 1));
	}
}
