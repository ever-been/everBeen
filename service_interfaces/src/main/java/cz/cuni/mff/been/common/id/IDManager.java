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
package cz.cuni.mff.been.common.id;

import java.util.HashMap;

public class IDManager implements IDManagerInterface {

	private static class Counter {
		private long count = 0;
		
		public long getNext() {
			return count++;
		}
	}
	
	private static final IDManager MGR = new IDManager();
	
	private final HashMap<Class< ? >, Counter> idMap = new HashMap<Class< ? >, Counter>();
	
	/* This should never be instantiated by accident. ;-) */
	private IDManager() { }
	
	public static IDManager getInstance() {
		return MGR;
	}
	
	public <T extends OID> T getNext(Class<T> clazz) {
		Counter ctr = idMap.get(clazz);
		if (ctr == null) {
			ctr = new Counter();
			idMap.put(clazz, ctr);
		}
		
		try {
			T instance = clazz.newInstance();
			instance.setValue(ctr.getNext());
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assert false : "Could not create new instance for: " + clazz.getName();
		return null;
	}
		
}
