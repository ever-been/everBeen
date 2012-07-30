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

import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class for manipulation and checking of the query parameters.
 * TODO: This singleton should be converted to a static class.
 * 
 * @author David Majda
 *
 */
public class Params {
	private static final int DAY_MIN    = 1;
	private static final int DAY_MAX    = 31;
	private static final int MONTH_MIN  = 1;
	private static final int MONTH_MAX  = 12;
	private static final int HOUR_MIN   = 0;
	private static final int HOUR_MAX   = 23;
	private static final int MINUTE_MIN = 0;
	private static final int MINUTE_MAX = 59;
	private static final int SECOND_MIN = 0;
	private static final int SECOND_MAX = 59;
	
	/** Class instance (singleton pattern). */
	private static Params instance;
	
	private Page page = Page.getInstance();
    
	/**
	 * Allocates a new <code>Params</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private Params() {
		super();
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static Params getInstance() {
		if (instance == null) {
			instance = new Params();
		}
		return instance;
	}
	
	/**
	 * Checks if the request method is "GET".
	 * 
	 * @return <code>true</code> if the request method is "GET";
	 *          <code>false</code> otherwise
	 */
	public boolean requestMethodIsGet() {
		return page.getRequest().getMethod().equals("GET");   
	}
	
	/**
	 * Checks if the request method is "POST".
	 * 
	 * @return <code>true</code> if the request method is "POST";
	 *          <code>false</code> otherwise
	 */
	public boolean requestMethodIsPost() {
		return page.getRequest().getMethod().equals("POST");   
	}
    
	/**
	 * Checks if the query parameter exists.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter exists;
	 *          <code>false</code> otherwise
	 */
	public boolean exists(String param) {
		return page.getRequest().getParameter(param) != null;
	}

	/**
	 * Checks if indexed query parameter exists.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if indexed query parameter exists;
	 *          <code>false</code> otherwise
	 */
	public boolean existsIndexed(String param) {		
		for (Enumeration< ? > e = page.getRequest().getParameterNames(); e.hasMoreElements(); ) {
			String paramName = (String) e.nextElement(); 
			if (paramName.startsWith(param + "[") && paramName.endsWith("]")) {
				return true;
			}
		}
		return false;
	}
    
	/**
	 * Returns name of the indexed parameter, given the parameter name and
	 * index. For generality, method is templated by the type of index.
	 * 
	 * @param <T> index type
	 * @param param parameter name
	 * @param index parameter index
	 * @return name of the indexed parameter
	 */
	public <T> String makeIndexed(String param, T index) {
		return param + "[" + index.toString() + "]";
	}
	
	/**
	 * Checks if at least one of the query parameters exists.
	 * 
	 * @param params parameter names
	 * @return <code>true</code> if at least one of the query parameters exists;
	 *          <code>false</code> otherwise
	 */
	public boolean existsOneOf(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (exists(params[i])) {
				return true;
			}
		}
		return false;
	}
    
	/**
	 * Checks if the query parameter is empty.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is empty;
	 *          <code>false</code> otherwise
	 */
	public boolean isEmpty(String param) {
		return page.getRequest().getParameter(param).equals("");
	}
    
	/**
	 * Checks if the query parameter is not empty.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is not empty;
	 *          <code>false</code> otherwise
	 */
	public boolean notEmpty(String param) {
		return !page.getRequest().getParameter(param).equals("");
	}
    
	/**
	 * Checks if the query parameter is integer in the specified interval.
	 * 
	 * @param param parameter name
	 * @param min minimum parameter value
	 * @param max maximum parameter value
	 * @return <code>true</code> if the query parameter is integer in the interval
	 *          &lt;<code>min</code>, <code>max</code>&gt;;
	 *          <code>false</code> otherwise
	 */
	public boolean isBetween(String param, int min, int max) {
		int value;
		try {
			value = Integer.parseInt(page.getRequest().getParameter(param));
		} catch (NumberFormatException e) {
			return false;
		}
		return value >= min && value <= max;
	}

	/**
	 * Checks if the query parameter in contained in the specified set. The
	 * containment is checked using <code>Set.contains</code> method.
	 * 
	 * @param param parameter name
	 * @param set set to check
	 * @return <code>true</code> if the query parameter is contained in the
	 *          specified set;
	 *          <code>false</code> otherwise
	 */
	public boolean isInSet(String param, Set< ? > set) {
		return set.contains(page.getRequest().getParameter(param));
	}
    
	/**
	 * Checks if the query parameter in not contained in the specified set. The
	 * containment is checked using <code>Set.contains</code> method.
	 * 
	 * @param param parameter name
	 * @param set set to check
	 * @return <code>true</code> if the query parameter is not contained in the
	 *          specified set;
	 *          <code>false</code> otherwise
	 */
	public boolean notInSet(String param, Set< ? > set) {
		return !set.contains(page.getRequest().getParameter(param));
	}
  
	/**
	 * Checks if the query parameter is integer.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is integer;
	 *          <code>false</code> otherwise
	 */
	public boolean isInteger(String param) {
		return Routines.isInteger(page.getRequest().getParameter(param));
	}
    
	/**
	 * Checks if the query parameter is long integer.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is long integer;
	 *          <code>false</code> otherwise
	 */
	public boolean isLong(String param) {
		return Routines.isLong(page.getRequest().getParameter(param));
	}

	/**
	 * Checks if the query parameter is float. The check is performed using
	 * <code>Float.parseFloat</code> method.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is float;
	 *          <code>false</code> otherwise
	 */
	public boolean isFloat(String param) {
		try {
			Float.parseFloat(page.getRequest().getParameter(param));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if the query parameter is double. The check is performed using
	 * <code>Double.parseDouble</code> method.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is double;
	 *          <code>false</code> otherwise
	 */
	public boolean isDouble(String param) {
		try {
			Double.parseDouble(page.getRequest().getParameter(param));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if the query parameter is regular expression. The check is performed
	 * using <code>Pattern.compile</code> method.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is double;
	 *          <code>false</code> otherwise
	 */
	public boolean isRegexp(String param) {
		try {
			Pattern.compile(page.getRequest().getParameter(param));
			return true;
		} catch (PatternSyntaxException e) {
			return false;
		}
	}

	/**
	 * Checks if the query parameter is day, e.g. integer in
	 * the interval <1, 31>.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is day;
	 *          <code>false</code> otherwise
	 */
	public boolean isDay(String param) {
		return isInteger(param) && isBetween(param, DAY_MIN, DAY_MAX);
	}
    
	/**
	 * Checks if the query parameter is month, e.g. integer in
	 * the interval <1, 12>.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is month;
	 *          <code>false</code> otherwise
	 */
	public boolean isMonth(String param) {
		return isInteger(param) && isBetween(param, MONTH_MIN, MONTH_MAX);
	}

	/**
	 * Checks if the query parameter is year, e.g. just integer.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is year;
	 *          <code>false</code> otherwise
	 */
	public boolean isYear(String param) {
		return isInteger(param);
	}
    
	/**
	 * Checks if the query parameter is hour, e.g. integer in
	 * the interval <0, 23>.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is hour;
	 *          <code>false</code> otherwise
	 */
	public boolean isHour(String param) {
		return isInteger(param) && isBetween(param, HOUR_MIN, HOUR_MAX);
	}
    
	/**
	 * Checks if the query parameter is minute, e.g. integer in
	 * the interval <0, 59>.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is minute;
	 *          <code>false</code> otherwise
	 */
	public boolean isMinute(String param) {
		return isInteger(param) && isBetween(param, MINUTE_MIN, MINUTE_MAX);
	}
    
	/**
	 * Checks if the query parameter is second, e.g. integer in
	 * the interval <0, 59>.
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is second;
	 *          <code>false</code> otherwise
	 */
	public boolean isSecond(String param) {
		return isInteger(param) && isBetween(param, SECOND_MIN, SECOND_MAX);
	}

	/**
	 * Checks if the query parameter is boolean - i.e. string <code>"true"</code> 
	 * or <code>"false"</code>. 
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is boolean;
	 *          <code>false</code> otherwise
	 */
	public boolean isBoolean(String param) {
		return page.getRequest().getParameter(param).equals("true")
			|| page.getRequest().getParameter(param).equals("false");
	}

	/**
	 * Checks if the query parameter is boolean sent from &lt;input type="checkbox"&gt;.
	 * It can have only one value - "on" - meaning <code>true</code>. Value
	 * for <code>false</code> is never sent, parameter is simply omitted. 
	 * 
	 * @param param parameter name
	 * @return <code>true</code> if the query parameter is checkbox boolean;
	 *          <code>false</code> otherwise
	 */
	public boolean isCheckboxBool(String param) {
		return page.getRequest().getParameter(param).equals("on");
	}

	/**
	 * Throws a <code>MissingParamException</code> if the query parameter
	 * doesn't exist.
	 * 
	 * @param param parameter name
	 * @throws MissingParamException if the query parameter doesn't exist
	 */
	public void ensureExists(String param) throws MissingParamException {
		if (!exists(param)) {
			throw new MissingParamException("Parameter \"" 
					+ param + "\" is missing.");   
		}
	}
    
	/**
	 * Throws a <code>MissingParamException</code> if any query parameter
	 * doesn't exist.
	 * 
	 * @param params parameter names
	 * @throws MissingParamException if any query parameter doesn't exist
	 */
	public void ensureExist(String... params) throws MissingParamException {
		for (int i = 0; i < params.length; i++) {
			if (!exists(params[i])) {
				throw new MissingParamException("Parameter \"" 
						+ params[i] + "\" is missing.");   
			}
		}
	}

	/**
	 * Throws a <code>MissingParamException</code> if all query parameters
	 * don't exist.
	 * 
	 * @param params parameter names
	 * @throws MissingParamException if all query parameters don't exist
	 */
	public void ensureExistsOneOf(String... params) throws MissingParamException {
		for (int i = 0; i < params.length; i++) {
			if (exists(params[i])) {
				return;   
			}
		}
		throw new MissingParamException("One of parameters \"" 
				+ Routines.join(", ", params) + "\" is missing.");
	}
    
	/**
	 * If given condition is <code>false</code>, add specified error message to the
	 * list of error messages.
	 * 
	 * @param condition tested condition
	 * @param message error message text
	 */
	public void checkCondition(boolean condition, String message) {
		if (!condition) {
			page.getErrorMessages().addTextMessage(message);
		}
	}
    
	/**
	 * Throws a <code>InvalidParamValueException</code> if given condition
	 * for some query parameter is <code>false</code>. 
	 * 
	 * @param param parameter name
	 * @param condition tested condition 
	 * @throws InvalidParamValueException if given condition if <code>false</code>
	 */
	public void ensureCondition(String param, boolean condition) throws InvalidParamValueException {
		if (!condition) {
			throw new InvalidParamValueException("Parameter \"" 
					+ param + "\" has invalid value.");
		}
	}
    
	/**
	 * Throws a <code>InvalidRequestMethodException</code> if the request method
	 * is not "GET".
	 * 
	 * @throws InvalidRequestMethodException if the request method is not "GET"
	 */
	public void ensureRequestMethodIsGet() throws InvalidRequestMethodException {
		if (!requestMethodIsGet()) {
			throw new InvalidRequestMethodException("Invalid request method(\""
					+ page.getRequest().getMethod() + "\").");
		}
	}

	/**
	 * Throws a <code>InvalidRequestMethodException</code> if the request method
	 * is not "POST".
	 * 
	 * @throws InvalidRequestMethodException if the request method is not "POST"
	 */
	public void ensureRequestMethodIsPost() throws InvalidRequestMethodException {
		if (!requestMethodIsPost()) {
			throw new InvalidRequestMethodException("Invalid request method(\""
					+ page.getRequest().getMethod() + "\").");
		}
	}
	
	/**
	 * Returns index of indexed query parameter.
	 * 
	 * @param param parameter name
	 * @return index of the indexed query parameter
	 * 
	 * @throws IllegalArgumentException if specified parameter doesn't exist or
	 *          it isn't indexed
	 */
	public String getIndex(String param) {
		for (Enumeration< ? > e = page.getRequest().getParameterNames(); e.hasMoreElements(); ) {
			String paramName = (String) e.nextElement(); 
			if (paramName.startsWith(param + "[") && paramName.endsWith("]")) {
				return paramName.substring(paramName.indexOf('[') + 1, paramName.indexOf(']'));
			}
		}
		throw new IllegalArgumentException("Parameter \"" + param
			+ "\" doesn't exist or isn't indexed.");
	}
}
