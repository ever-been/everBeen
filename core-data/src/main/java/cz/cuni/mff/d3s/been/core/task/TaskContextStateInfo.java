package cz.cuni.mff.d3s.been.core.task;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public class TaskContextStateInfo {
	/** Item of the context state */
	public static class Item {
		/** context ID */
		public String taskContextId;

		/** context state */
		public TaskContextState state;
	}

	/** Collection of context states. */
	public Collection<Item> items = new ArrayList<>();
}
