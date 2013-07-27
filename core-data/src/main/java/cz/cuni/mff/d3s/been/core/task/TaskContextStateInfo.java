package cz.cuni.mff.d3s.been.core.task;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public class TaskContextStateInfo {
	public static class Item {
		public String taskContextId;
		public TaskContextState state;
	}

	public Collection<Item> items = new ArrayList<>();
}
