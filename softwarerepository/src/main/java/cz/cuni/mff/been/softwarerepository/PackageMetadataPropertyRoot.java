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
package cz.cuni.mff.been.softwarerepository;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.been.common.rsl.ContainerProperty;
import cz.cuni.mff.been.common.rsl.Property;
import cz.cuni.mff.been.common.rsl.SimpleProperty;

/**
 * Impementation of the property root for the Software Repository RSL query
 * interface.
 * 
 * All other necessary interfaces are implemented as inner classes.
 * 
 * @author David Majda
 */
public class PackageMetadataPropertyRoot implements ContainerProperty {
	/** Package metadata we are querying. */
	private PackageMetadata metadata;
	/**
	 * Map of <code>SimpleProperty</code> objects corresponding to the package
	 * metadata attributes. The map is filled lazily, as there won't be namy
	 * attributes checked in the typical RSL query.
	 */
	private Map<String, SimpleProperty> properties = new HashMap<String, SimpleProperty>();
	
	/**
	 * Implementation of the <code>SimpleProperty</code> inteface. Each instance
	 * corresponds to one package metadata attribute. 
	 * 
	 * @author David Majda
	 */
	private class PackageMetadataProperty implements SimpleProperty {
		/** Information about corresponding attribute. */
		private AttributeInfo info;
		
		/**
		 * Allocates a new <code>PackageMetadataProperty</code> object.
		 * 
		 * @param info	information about corresponding attribute
		 */
		public PackageMetadataProperty(AttributeInfo info) {
			this.info = info;
		}
		
		/**
		 * @see cz.cuni.mff.been.common.rsl.SimpleProperty#getValue()
		 */
		public Object getValue() {
			try {
				return info.getGetter().invoke(metadata, (Object[]) null);
			} catch (IllegalArgumentException e) {
				assert false: "If you end up here, you are doomed.";
			} catch (IllegalAccessException e) {
				assert false: "If you end up here, you are doomed.";
			} catch (InvocationTargetException e) {
				assert false: "If you end up here, you are doomed.";
			}
			return null;
		}

		/**
		 * @see cz.cuni.mff.been.common.rsl.SimpleProperty#getValueClass()
		 */
		public Class< ? > getValueClass() {
			return info.getKlass();
		}
	}

	/**
	 * Allocates a new <code>PackageMetadataPropertyRoot</code> object.
	 * 
	 * @param metadata package metadata we are querying
	 */
	public PackageMetadataPropertyRoot(PackageMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * @see cz.cuni.mff.been.common.rsl.ContainerProperty#getProperty(java.lang.String)
	 */
	public Property getProperty(String propertyName) {
		for (AttributeInfo info: PackageMetadata.ATTRIBUTE_INFO) {
			if (info.getName().equals(propertyName)) {
				if (!properties.containsKey(propertyName)) {
					properties.put(propertyName, new PackageMetadataProperty(info));
				}
				return properties.get(propertyName);
			}
		}
		throw new IllegalArgumentException("Invalid property name \""
			+ propertyName + "\". Check with hasProperty method before calling.");
	}

	/**
	 * @see cz.cuni.mff.been.common.rsl.ContainerProperty#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String propertyName) {
		for (AttributeInfo info: PackageMetadata.ATTRIBUTE_INFO) {
			if (info.getName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

}
