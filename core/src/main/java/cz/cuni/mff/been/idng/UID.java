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
package cz.cuni.mff.been.idng;

import java.io.Serializable;

/**
 * Identifier of one single data item available in an internal tree node. The identifier
 * class hierarchy in the common package is designed so badly that a separate system of identifiers
 * will be used here for the time being...
 * 
 * IMHO, there is no reason to count identifiers for different classes separately. The bottleneck
 * this approach is trying to avoid will persist anyway, whenever one of those classes is created
 * too often. The only proper solution would be a round-robin set of multiple counters, each of
 * them generated a mutually disjoint series of numbers. However, we are not designing a mainframe
 * here, are we?
 * 
 * For now, all the data handles will have just one counter source. Presumably, this will be
 * redesigned later on.
 * 
 * @author Andrej Podzimek
 */
public abstract class UID implements Comparable< UID >, Serializable {
	
	private static final long	serialVersionUID	= 5348466785479099465L;

	/** The unique value this identifier contains. */
	private final long value;
	
	/**
	 * A prohibited constructor. These classes cannot be constructed from outside.
	 */
	protected UID( Long value ) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo( UID other ) {
		if ( this.value == other.value ) {
			return 0;
		} else {
			if ( this.value < other.value ) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
