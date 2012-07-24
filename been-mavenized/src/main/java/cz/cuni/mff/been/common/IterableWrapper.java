/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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

import java.util.Iterator;

/**
 * A decorator that can be used as (somewhat inefficient) eye-candy for descending iterators
 * and other special things of that kind.
 * 
 * @author Andrej Podzimek
 */
public class IterableWrapper< T > implements Iterable< T > {

	/** The inner iterator to read from. */
	private final Iterator< T > iterator;
	
	/**
	 * Initializes the Iterator Wrapper using an Iterator.
	 * 
	 * @param iterable The Iterable to get the Iterator from.
	 */
	public IterableWrapper( Iterable< T > iterable ) {
		this.iterator = iterable.iterator();
	}
	
	/**
	 * Initializes the Iterator Wrappe using an Iterator.
	 * 
	 * @param iterator The Iterator to use.
	 */
	public IterableWrapper( Iterator< T > iterator ) {
		this.iterator = iterator;
	}
	
	@Override
	public Iterator< T > iterator() {
		return iterator;
	}
}

