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
package cz.cuni.mff.been.common;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskProperties;
import cz.cuni.mff.been.jaxb.td.TaskProperty;

/**
 * Utility class for replacing variables with their values in strings. Variables
 * are written in format ${<em>variableName</em>}, where <em>variableName</em>
 * can contain letters, digits, "-", "_", "." and ":".
 * 
 * Values of the variables are provided by implementation of the
 * <code>ValueProvider</code> interface, which is supplied by the caller of
 * class methods.
 * 
 * @author David Majda
 */
public class VariableReplacer {
	private static final Pattern VAR_PATTERN = Pattern.compile(
		"\\$\\{([a-zA-Z0-9\\-_.][a-zA-Z0-9\\-_.:]*)\\}"
	);
	
	/**
	 * Provider of the variable values.
	 * 
	 * @author David Majda
	 */
	public interface ValueProvider {
		/**
		 * Returns value of given variable.
		 * 
		 * @param variableName name of the variable
		 * @return value of the given variable or <code>null</code> if the variable
		 *          does not exist
		 */
		String getValue(String variableName);
	}
	
	/**
	 * Replaces variables in string.
	 * 
	 * @param s string to replace variables in
	 * @param valueProvider provider of the variable values
	 * @return string with replaced variables
	 * 
	 * @throws IllegalArgumentException if the string syntax is invalid or any
	 *          vaiable is not defined
	 */
	public static String replace(String s, ValueProvider valueProvider) {
		// The following test was commented out in one of the revisions...
		//if (!STRING_PATTERN.matcher(s).matches()) {
		//	throw new IllegalArgumentException("Invalid input syntax.");
		//}
		
		Matcher m = VAR_PATTERN.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String value = valueProvider.getValue(m.group(1));
			if (value == null) {
				throw new IllegalArgumentException("Undefined variable: " + m.group(1));
			}
			m.appendReplacement(sb, Matcher.quoteReplacement(value));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * Replaces variables in properties and returns new <code>Properties</code>
	 * object with all variables in properties replaced. Assumes all property
	 * values are <code>String</code>s.
	 * 
	 * @param taskDescriptor The task descriptor to read (and replace) properties from.
	 * @param valueProvider provider of the variable values
	 * @return new <code>Properties</code> object with replaced variables
	 * 
	 * @throws IllegalArgumentException if the string syntax is invalid or any
	 *          vaiable is not defined
	 * @throws ClassCastException if any property value is not <code>String</code>
	 */
	public static Properties replace(TaskDescriptor taskDescriptor, ValueProvider valueProvider) {
		Properties result = new Properties();
		
		if (taskDescriptor.isSetTaskProperties()) {
			TaskProperties properties = taskDescriptor.getTaskProperties();
			if (properties.isSetTaskProperty()) {
				for (TaskProperty property : properties.getTaskProperty()) {
					result.setProperty(
						property.getKey(),
						replace(
							property.isSetLongValue() ?
								property.getValue() + property.getLongValue() :
								property.getValue(),
							valueProvider
						)
					);
				}
			}
		}
		return result;
	}

	/**
	 * Private construcor is so no instances can be created.
	 */
	private VariableReplacer() {
	}
}
