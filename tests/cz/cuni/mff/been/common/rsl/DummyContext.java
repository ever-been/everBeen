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
package cz.cuni.mff.been.common.rsl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.softwarerepository.PackageType;

/**
 * ContainerProperty implementation for RSL testing purposes.
 * 
 * @author David Majda
 */
public class DummyContext implements ContainerProperty {
	private HashMap<String, Class< ? >> simplePropertyClasses = new HashMap<String, Class< ? >>();
	private HashMap<String, Object> simplePropertyValues = new HashMap<String, Object>();
	
	private class TestingSimpleProperty implements SimpleProperty {
		private String propertyName;
		
		public TestingSimpleProperty(String propertyName) {
			this.propertyName = propertyName;
		}

		public Object getValue() {
			return simplePropertyValues.get(propertyName);
		}

		public Class< ? > getValueClass() {
			return simplePropertyClasses.get(propertyName);
		}
	}
	
	private static class TestingContainerProperty implements ContainerProperty {
		private String name; 
		private long age;
		
		public TestingContainerProperty(String name, long age) {
			this.name = name;
			this.age = age;
		}
		
		private class NameSimpleProperty implements SimpleProperty {
			public Object getValue()      { return name;        }
			public Class< String >  getValueClass() { return String.class; }
		}
		
		private class AgeSimpleProperty implements SimpleProperty {
			public Object getValue()      { return new LongWithUnit(age, LongWithUnit.NO_UNIT_PREFIX, null); }
			public Class< LongWithUnit >  getValueClass() { return LongWithUnit.class; }
		}

		public boolean hasProperty(String propertyName) {
			return propertyName.equals("name") || propertyName.equals("age");
		}

		public Property getProperty(String propertyName) {
			if (propertyName.equals("name")) { return new NameSimpleProperty(); }
			if (propertyName.equals("age"))  { return new AgeSimpleProperty();  }
			assert false: "Trying to retrieve non-existant property.";
			return null;
		}
	}
	
	private static class TestingArrayProperty implements ArrayProperty {
		public ContainerProperty[] getItems() {
			return new ContainerProperty[] {
				new TestingContainerProperty("name1", 12),	
				new TestingContainerProperty("name2", 13),	
			};
		}
	}
	
	{
		simplePropertyClasses.put("longProperty", LongWithUnit.class);
		simplePropertyClasses.put("longPropertyMHz", LongWithUnit.class);
		simplePropertyClasses.put("stringProperty", String.class);
		simplePropertyClasses.put("stringForPatternProperty", String.class);
		simplePropertyClasses.put("versionProperty", Version.class);
		simplePropertyClasses.put("dateProperty", Date.class);
		simplePropertyClasses.put("packageTypeProperty", PackageType.class);
		simplePropertyClasses.put("listProperty", List.class);

		simplePropertyValues.put("longProperty", new LongWithUnit("42"));
		simplePropertyValues.put("longPropertyMHz", new LongWithUnit("42MHz"));
		simplePropertyValues.put("stringProperty", "42");
		simplePropertyValues.put("stringForPatternProperty", "StRiNg");
		simplePropertyValues.put("versionProperty", new Version("42"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(2006, 4, 17, 23, 30, 42);
		calendar.setTimeInMillis((calendar.getTimeInMillis() / 1000) * 1000);
		simplePropertyValues.put("dateProperty", calendar.getTime());
		simplePropertyValues.put("packageTypeProperty", PackageType.realValueOf("source"));
		List<String> list = new ArrayList<String>();
		list.add("42");
		simplePropertyValues.put("listProperty", list);
	}
	
	public boolean hasProperty(String propertyName) {
		return
		  propertyName.equals("mother")
		  || propertyName.equals("father")
		  || propertyName.equals("children")
		  || simplePropertyClasses.containsKey(propertyName);
	}

	public Property getProperty(String propertyName) {
		if (propertyName.equals("mother")) {
			return new TestingContainerProperty("mother", 30);
		}
		if (propertyName.equals("father")) {
			return new TestingContainerProperty("father", 40);
		}
		if (propertyName.equals("children")) { 
            return new TestingArrayProperty();
        }
		return new TestingSimpleProperty(propertyName);
	}
}
