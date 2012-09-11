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
package cz.cuni.mff.been.common;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;

public final class BeenUtils {
	
	private BeenUtils() {
		// not available
	}
	/**
	 * Comparator for TaskDescriptors according to TIDs
	 */
	public static final Comparator<TaskDescriptor> TID_COMPARATOR = new TidComparator();
	
	private static final class TidComparator implements Comparator<TaskDescriptor> {

		public int compare(TaskDescriptor o1, TaskDescriptor o2) {
			Long l1 = Long.valueOf(o1.getTaskId());
			Long l2 = Long.valueOf(o2.getTaskId());
			return l1.compareTo(l2);
		}
		
	}
	
	/**
	 * Sorts tasks by TIDs
	 * @param tasksToSort	tasks to sort
	 * @return taskToSort sorted by TIDs
	 */
	public static final TaskDescriptor[] sortTasks(TaskDescriptor[] tasksToSort) {
		// FIXME by Tadeas Palusga -> this is inconsistent method -> possible point of failure -> should be reimplemented as void method or should return NEW array !!
		Arrays.sort(tasksToSort,TID_COMPARATOR);
		return tasksToSort;
	}
	
	/**
	 * Extract task identifiers from task descriptors
	 * @param descriptors	descriptors to extract from
	 * @return	list of extracted task identifiers
	 */
	public static String[] extractTaskIDs(TaskDescriptor[] descriptors) {
		String[] tids = new String[descriptors.length];
		int i=0;
		for (TaskDescriptor td : descriptors) {
			tids[i++] = td.getTaskId();
		}
		
		return tids;
	}
	
	/**
	 * Searches for directory containing specified "nameToSearch" file or directory
	 * by BFS algorithm 
	 * 
	 * @param rootDir directory where to begin search
	 * @param nameToSearch directory/file name which 'root' should contain
	 * @return root or null when no-match
	 */
	public static File findRootDir(String rootDir, String nameToSearch) {
		File root = new File(rootDir);
		
		// run BFS on root
		LinkedList<File> dirsToSearch = new LinkedList<File>();
		dirsToSearch.add(root);
		
		while (!dirsToSearch.isEmpty()) {
			root = dirsToSearch.removeFirst();
			for (File f : root.listFiles()) {
				if (nameToSearch.equals(f.getName())) {
					return root;
				} else  if (f.isDirectory()) {
					dirsToSearch.addLast(f);
				}
			}
		}
		
		return null;
	}
}
