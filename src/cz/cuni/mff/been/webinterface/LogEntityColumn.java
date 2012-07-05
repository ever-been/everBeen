/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.webinterface;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.been.common.id.ID;

/**
 * Class representing a column in log view with links to the entities. Class
 * contains column name, template for URLs, which the cells link to, and a map,
 * which associates pairs (context, task) with entities. Cell content is
 * generated from this map when writing the log in the web interface.
 * 
 * The information about associated entity is not stored directly in the
 * <code>LogRecord</code> class, because types of the associated entities vary.
 * Also, it would be ugly (Log Service should not know anything about entities).
 * 
 * Other possible approach - converting every <code>LogRecord</code> to internal
 * web interface class - was not chosen again because types of the associated
 * entities vary and it probably would be a performance-sensitive approach.  
 * 
 * @author David Majda
 *
 * @param <T> identifier of entity in the column
 */
public class LogEntityColumn<T extends ID> {
	/**
	 * Little utility class storing data about entities displayed the cells. 
	 * 
	 * @author David Majda
	 *
	 * @param <T> identifier of entity in the column
	 */
	public static class Value<T> {
		/** Entity identifier. */
		private T id; 
		/** Entity name. */
		private String name;
		
		/** @return entity identifier */
		public T getId() {
			return id;
		}

		/** @return entity name */
		public String getName() {
			return name;
		}

		/**
		 * Allocates a new <code>LogEntityColumn.Value</code> object.
		 * 
		 * @param id entity identifier
		 * @param name entity name
		 */
		public Value(T id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	/** Column title. */
	private String title;
	/**
	 * Column URL template. It must contain exactly one <code>"%s"</code>
	 * formatting sequence, which will be replaced by the ID of the entity linked
	 * from specific cell. 
	 */
	private String urlTemplate;
	
	/**
	 * Nicely templated map of maps :-) Outer map maps context ID to another map,
	 * which maps the task ID (from previously mapped context) to entity the task
	 * belongs to. 
	 */
	private Map<String, Map<String, Value<T>>> values
		= new HashMap<String, Map<String, Value<T>>>();
	
	/** @return column title */
	public String getTitle() {
		return title;
	}

	/** @return column URL template */
	public String getUrlTemplate() {
		return urlTemplate;
	}

	/**
	 * Allocates a new <code>LogEntityColumn</code> object.
	 * 
	 * @param title column title
	 * @param urlTemplate Column URL template. It must contain exactly one
	 *         <code>"%s"</code> formatting sequence, which will be replaced by
	 *         the ID of the entity linked from specific cell
	 */
	public LogEntityColumn(String title, String urlTemplate) {
		this.title = title;
		this.urlTemplate = urlTemplate;
	}

	/**
	 * Gets a value representing entity associated with given tasks and context or
	 * <code>null</code> if there is no such entity. 
	 * 
	 * @param contextID context identifier
	 * @param taskID task identifier
	 * @return value representing entity associated with given task and context
	 */
	public Value< T > getValue(String contextID, String taskID) {
		Map<String, Value<T>> valuesForContext = values.get(contextID);
		if (valuesForContext != null) {
			return valuesForContext.get(taskID);
		} else {
			return null;
		}
	}
	
	/**
	 * Associates an entity with given context and task. 
	 * 
	 * @param contextID context identifier
	 * @param taskID task identifier
	 * @param value value representing associated entity
	 */
	public void addValue(String contextID, String taskID, Value<T> value) {
		Map<String, Value<T>> valuesForContext;
		if (values.containsKey(contextID)) {
			valuesForContext = values.get(contextID);
		} else {
			valuesForContext = new HashMap<String, Value<T>>();
			values.put(contextID, valuesForContext); 
		}
		valuesForContext.put(taskID, value);
	}
}
