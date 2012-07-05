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
package cz.cuni.mff.been.taskmanager.tasktree;

/**
 * A list of the most common values basic flags can have. Any other enum can be used to provide
 * flag values. However, the behavior of the tree is <b>undefined</b> when two different enum values
 * with the same ordinal number are used at a time.
 * 
 * @author Andrej Podzimek
 */
public enum BasicValues {
	
	/** Nope. */
	FALSE,
	
	/** Yeah. */
	TRUE,
	
	/** A piece of three-state logic, if desired. Implement TaskTreeFlag and you get it. */
	UNKNOWN;
}
