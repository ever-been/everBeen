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
package cz.cuni.mff.been.webinterface.hosts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cz.cuni.mff.been.common.value.ValueBoolean;
import cz.cuni.mff.been.common.value.ValueCommonInterface;
import cz.cuni.mff.been.common.value.ValueDouble;
import cz.cuni.mff.been.common.value.ValueInteger;
import cz.cuni.mff.been.common.value.ValueList;
import cz.cuni.mff.been.common.value.ValueRange;
import cz.cuni.mff.been.common.value.ValueRegexp;
import cz.cuni.mff.been.common.value.ValueString;
import cz.cuni.mff.been.common.value.ValueVersion;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Params;

/**
 * Processes user property settings in the host details form.
 * 
 * 1. Ensures all parameters were sent in the HTTP request (<code>ensure</code>
 *    method).
 * 2. Checks all parametsrs' values (<code>check</code> method).
 * 3. Creates <code>ValueCommonInterface</code> instances from the parameters sent
 *    in the HTTP request, which will be supplied to the Host Manager
 *    (<code>getValue</code>) method.
 *    
 * For each type of property value, there exists an subclass of
 * <code>ValueHandler</code> class. This subclass processes parameters for items
 * of its type - the <code>UserPropertiesHandler</code> delegates its work using
 * a Class->ValueHandler hashmap.  
 *   
 * @author David Majda
 */
public class UserPropertiesHandler {
	/**
	 * Subclasses of this abstract process HTTP parameters for items of given
	 * type. 
	 * 
	 * @author David Majda
	 */
	private abstract static class ValueHandler {
		/**
		 * Checks if required parameters were sent and they have valid values. If
		 * the parameters are OK, the method doesn't do anything, otherwise it
		 * throws <code>MissingParamException</code> or
		 * <code>InvalidParamValueException</code> respectively.
		 *  
		 * @param name processed user property name
		 * @throws MissingParamException if some required parameter is missing
		 * @throws InvalidParamValueException if required parameter contains
		 *          invalid value
		 */
		public abstract void ensure(String name)
			throws MissingParamException, InvalidParamValueException;
		
		/**
		 * Checks if required parameters have valid values and writes error message
		 * if some parameters do not.
		 * 
		 * This method is used when validating free-text fields (e.g. when user
		 * should enter only integer value) and in similar situations.  
		 * 
		 * @param name processed user property name
		 */
		public abstract void check(String name);
		
		/**
		 * Creates the <code>ValueCommonInterface</code> instance from the
		 * parameters sent in the HTTP request.
		 * 
		 * @param property processed user property
		 */
		public abstract ValueCommonInterface getValue(NameValuePair property);
	}
	
	private Params params = Params.getInstance();
	private HttpServletRequest request;
	private String prefix;
	private Map<Class< ? >, ValueHandler> valueHandlers;
	private boolean editing;
	
	private final ValueHandler valueBooleanHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
			params.ensureCondition(paramName, params.isBoolean(paramName));
		}

		@Override
		public void check(String name) {
			/* We don't need to do anything here. */
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueBoolean(request.getParameter(paramName).equals("true"));
		}
	};

	private final ValueHandler valueIntegerHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
		}

		@Override
		public void check(String name) {
			String paramName = prefix + "-" + name;
			params.checkCondition(params.isInteger(paramName),
				editing
					? "Property \"" + name + "\" must be an integer."
					: "Property must be an integer.");
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueInteger(Integer.valueOf(request.getParameter(paramName)));
		}
	};
	
	private final ValueHandler valueDoubleHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
		}

		@Override
		public void check(String name) {
			String paramName = prefix + "-" + name;
			params.checkCondition(params.isDouble(paramName),
				"Property \"" + name + "\" must be an double.");
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueDouble(Double.valueOf(request.getParameter(paramName)));
		}
	};

	private final ValueHandler valueStringHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
		}

		@Override
		public void check(String name) {
			/* We don't need to do anything here. */
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueString(request.getParameter(paramName));
		}
	};

	private final ValueHandler valueRegexpHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
		}

		@Override
		public void check(String name) {
			String paramName = prefix + "-" + name;
			params.checkCondition(params.isRegexp(paramName), 				
				editing 
					? "Property \"" + name + "\" must be an regular expression."
					: "Property must be an regular expression.");
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueRegexp(request.getParameter(paramName));
		}
	};

	private final ValueHandler valueVersionHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			String paramName = prefix + "-" + name;
			params.ensureExists(paramName);
		}

		@Override
		public void check(String name) {
			/* We don't need to do anything here. */
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			String paramName = prefix + "-" + property.getName();
			return new ValueVersion(request.getParameter(paramName));
		}
	};

	private final ValueHandler valueListHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			/* We don't need to do anything here. */
		}

		@Override
		public void check(String name) {
			/* We don't need to do anything here. */
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			return property.getValue(); // We return old value.
		}
	};
	
	private final ValueHandler valueRangeHandler = new ValueHandler() {
		@Override
		public void ensure(String name) throws MissingParamException, InvalidParamValueException {
			/* We don't need to do anything here. */
		}

		@Override
		public void check(String name) {
			/* We don't need to do anything here. */
		}

		@Override
		public ValueCommonInterface getValue(NameValuePair property) {
			return property.getValue(); // We return old value.
		}
	};
		
	/**
	 * Allocates a new <code>UserPropertiesHandler</code> object.
	 * 
	 * @param aRequest HTTP request in which the parameters will be processed
	 * @param prefix prefix of HTTP request parameters
	 * @param editing flag indicating if we are adding or editing the properties
	 */
	public UserPropertiesHandler(HttpServletRequest aRequest, String prefix,
			boolean editing) {
		this.request = aRequest;
		this.prefix = prefix;
		this.editing = editing;
		
		valueHandlers = new HashMap<Class< ? >, ValueHandler>();
		valueHandlers.put(ValueBoolean.class, valueBooleanHandler); 		
		valueHandlers.put(ValueInteger.class, valueIntegerHandler); 		
		valueHandlers.put(ValueDouble.class, valueDoubleHandler); 		
		valueHandlers.put(ValueString.class, valueStringHandler); 		
		valueHandlers.put(ValueRegexp.class, valueRegexpHandler); 		
		valueHandlers.put(ValueVersion.class, valueVersionHandler); 		
		valueHandlers.put(ValueList.class, valueListHandler); 		
		valueHandlers.put(ValueRange.class, valueRangeHandler); 		
	}
	
	/**
	 * Checks if required parameters were sent for all items and they have valid
	 * values. If the parameters are OK, the method doesn't do anything, otherwise
	 * it throws <code>MissingParamException</code> or
	 * <code>InvalidParamValueException</code> respectively.
	 *  
	 * @param properties current user properties of the host
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 */
	public void ensure(NameValuePair[] properties) throws MissingParamException,
			InvalidParamValueException {
		for (NameValuePair property: properties) {
			valueHandlers.get(property.getValue().getClass()).ensure(property.getName());
		}
	}

	/**
	 * Checks if required parameters for all items have valid values and writes
	 * error message if some parameters do not.
	 * 
	 * @param properties current user properties of the host
	 */
	public void check(NameValuePair[] properties) {
		for (NameValuePair property: properties) {
			valueHandlers.get(property.getValue().getClass()).check(property.getName());
		}
	}

	/**
	 * Updates all items' values according to the parameters sent in the HTTP
	 * request.
	 * 
	 * @param properties current user properties of the host
	 */
	public NameValuePair[] getValues(NameValuePair[] properties) {
		List<NameValuePair> result = new LinkedList<NameValuePair>();
		for (NameValuePair property: properties) {
			result.add(new NameValuePair(
				property.getName(),
				valueHandlers.get(property.getValue().getClass()).getValue(property)
			));
		}
		return result.toArray(new NameValuePair[result.size()]);
	}
}
