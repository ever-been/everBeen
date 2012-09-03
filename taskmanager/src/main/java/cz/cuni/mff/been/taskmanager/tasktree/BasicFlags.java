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

import static cz.cuni.mff.been.taskmanager.tasktree.BasicValues.FALSE;
import static cz.cuni.mff.been.taskmanager.tasktree.BasicValues.TRUE;

/**
 * A list of basic tree flag implementations. These flags should be set by task wrappers
 * and used as basic information for status reports, progress bars and the like. The TaskTreeFlag
 * interface can be implemented to obtain more flags with their own values and inheritance rules.
 * 
 * @author Andrej Podzimek
 */
public enum BasicFlags implements TaskTreeFlag {
	
	/** All tasks finished in the given subtree. */
	COMPLETED( "completed", new TreeFlagValue( FALSE, "Initial value of COMPLETED" ) ) {
		
		@Override
		public boolean inherit(
			TreeFlagValue oldValue,
			TreeFlagValue newValue,
			TreeFlagInheritance inheritance
		) throws TreeFlagException {
			final int nFalse, oldOrdinal, newOrdinal;
			
			oldOrdinal = oldValue.getOrdinal().ordinal();
			newOrdinal = newValue.getOrdinal().ordinal();
			nFalse = inheritance.getValueCounter( FALSE );
			switch ( nFalse ) {
				case 0:
					if ( oldOrdinal == newOrdinal ) {												// Nothing to report.
						return false;
					} else {																		// nFalse was 1 before.
						inheritance.setFlag( TRUE, null );
						return true;
					}
				case 1:
					if ( oldOrdinal == TRUE.ordinal() && newOrdinal == FALSE.ordinal() ) {			// nFalse was 0 before.
						inheritance.setFlag( FALSE, null );
						return true;
					} else {																		// nFalse was 2 or 1 before.
						return false;
					}
				default:
					return false;
			}
		}
	},
	
	/** Errors exist in the subtree. */
	ERROR( "error", new TreeFlagValue( FALSE, "Initial value of ERROR" ) ) {
		
		@Override
		public boolean inherit(
			TreeFlagValue oldValue,
			TreeFlagValue newValue,
			TreeFlagInheritance inheritance
		) throws TreeFlagException {
			final int nTrue, oldOrdinal, newOrdinal;
			
			nTrue = inheritance.getValueCounter( TRUE );
			oldOrdinal = oldValue.getOrdinal().ordinal();
			newOrdinal = newValue.getOrdinal().ordinal();
			switch ( nTrue ) {
				case 0:
					if ( oldOrdinal == newOrdinal ) {												// Nothing to report.
						return false;
					} else {																		// nTrue was 1 before.
						inheritance.setFlag( FALSE, null );
						return true;
					}
				case 1:
					if ( oldOrdinal == FALSE.ordinal() && newOrdinal == TRUE.ordinal() ) {			// nTrue was 0 before.
						inheritance.setFlag( TRUE, null );
						return true;
					} else {																		// nTrue was 2 or 1 before.
						return false;
					}
				default:
					return false;
			}
		}
	},
	
	/** Warnings exist in the subtree. */
	WARNING( "warning", new TreeFlagValue( FALSE, "Initial value of WARNING" ) ) {
		
		@Override
		public boolean inherit(
			TreeFlagValue oldValue,
			TreeFlagValue newValue,
			TreeFlagInheritance inheritance
		) throws TreeFlagException {
			final int nTrue, oldOrdinal, newOrdinal;
			
			nTrue = inheritance.getValueCounter( TRUE );
			oldOrdinal = oldValue.getOrdinal().ordinal();
			newOrdinal = newValue.getOrdinal().ordinal();
			switch ( nTrue ) {
				case 0:
					if ( oldOrdinal == newOrdinal ) {												// Nothing to report.
						return false;
					} else {
						inheritance.setFlag( FALSE, null );
						return true;
					}
				case 1:
					if ( oldOrdinal == FALSE.ordinal() && newOrdinal == TRUE.ordinal() ) {
						inheritance.setFlag( TRUE, null );
						return true;
					} else {
						return false;
					}
				default:
					return false;
			}
		}
	};

	/** The String key of a flag. This is the one and only identifier of the flag. */
	private final String key;

	/** The default value of the flag. Returned by getters before the flag has been set. */
	private final TreeFlagValue implicit;
	
	/**
	 * A common constructor for the basic flags items.
	 * 
	 * @param key The unique identifier of the flag.
	 * @param implicit The flag value to return when asked for a default one.
	 */
	private BasicFlags( String key, TreeFlagValue implicit ) {
		this.key = key;
		this.implicit = implicit;
	}

	@Override
	public abstract boolean inherit(
		TreeFlagValue oldValue,
		TreeFlagValue newValue,
		TreeFlagInheritance inheritance
	) throws TreeFlagException;

	@Override
	public void validate( Enum< ? > ordinal ) throws TreeFlagException {
		if ( ordinal.ordinal() > 1 ) {
			throw new TreeFlagException( "Unknown ordinal used.", this );
		}		
	}

	@Override
	public TreeFlagValue getDefaultValue() {
		return implicit;
	}

	@Override
	public String toString() {
		return key;
	}
}
