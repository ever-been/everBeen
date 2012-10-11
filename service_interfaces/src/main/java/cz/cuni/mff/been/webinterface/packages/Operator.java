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
package cz.cuni.mff.been.webinterface.packages;

import cz.cuni.mff.been.common.Version;
import cz.cuni.mff.been.softwarerepository.PackageType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

/**Enum representing an operator used in the package query form (and maybe
 * in other forms later). Should be specialized for each operator used. 
 * 
 * Operator can be viewed as a relation between tho objects. It contains
 * abstract method <code>apply</code>, which is executed when the operator 
 * needs to be applied on some operads during the queries. This method shoud tell,
 * whether two objects passed as arguments are in relation represended by this 
 * operator, or not. 
 *  
 * @author David Majda
 */
public enum Operator implements Serializable {

	/* String operators */

	STRING_IS( "is", "is" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (String) metadataValue ).equals( formValue );
		}
	},

	STRING_IS_NOT( "is-not", "is not" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return !( (String) metadataValue ).equals( formValue );
		}
	},

	STRING_CONTAINS( "contains", "contains" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (String) metadataValue ).indexOf( (String) formValue ) != -1;
		}
	},

	STRING_DOES_NOT_CONTAIN( "does-not-contain", "does not contain" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (String) metadataValue ).indexOf( (String) formValue ) == -1;
		}
	},

	/* Date operators */

	DATE_EQUAL( "eq", "equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Date) metadataValue ).equals( formValue );
		}
	},

	DATE_NOT_EQUAL( "ne", "not equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return !( (Date) metadataValue ).equals( formValue );
		}
	},

	DATE_GREATER_THAN( "gt", "greater than" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Date) metadataValue ).compareTo( (Date) formValue ) > 0;
		}
	},

	DATE_GREATER_OR_EQUAL_TO( "ge", "greater or equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Date) metadataValue ).compareTo( (Date) formValue ) >= 0;
		}
	},

	DATE_SMALLER_THAN( "lt", "smaller than" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Date) metadataValue ).compareTo( (Date) formValue ) < 0;
		}
	},

	DATE_SMALLER_OR_EQUAL_TO( "le", "smaller or equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Date) metadataValue ).compareTo( (Date) formValue ) <= 0;
		}
	},

	/* ArrayList operators */

	LIST_CONTAINS( "contains", "contains" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (ArrayList< ? >) metadataValue ).contains( formValue );
		}
	},

	LIST_DOES_NO_CONTAIN( "does-not-contain", "does not contain" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return !( (ArrayList< ? >) metadataValue ).contains( formValue );
		}
	},

	/* Version operators */

	VERSION_EQUAL( "eq", "equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Version) metadataValue ).equals( formValue );
		}
	},

	VERSION_NOT_EQUAL( "ne", "not equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return !( (Version) metadataValue ).equals( formValue );
		}
	},

	VERSION_GREATER_THAN( "gt", "greater than" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Version) metadataValue ).compareTo( (Version) formValue ) > 0;
		}
	},

	VERSION_GREATER_OR_EQUAL_TO( "ge", "greater or equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Version) metadataValue ).compareTo( (Version) formValue ) >= 0;
		}
	},

	VERSION_SMALLER_THAN( "lt", "smaller than" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Version) metadataValue ).compareTo( (Version) formValue ) < 0;
		}
	},

	VERSION_SMALLER_OR_EQUAL_TO( "le", "smaller or equal to" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (Version) metadataValue ).compareTo( (Version) formValue ) <= 0;
		}
	},

	PACKAGE_IS( "is", "is" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return ( (PackageType) metadataValue ).equals( formValue );
		}
	},

	PACKAGE_IS_NOT( "is-not", "is not" ) {
		@Override
		public boolean apply( Object metadataValue, Object formValue ) {
			if ( metadataValue == null ) {
				return false;
			}
			return !( (PackageType) metadataValue ).equals( formValue );
		}
	};


	private static enum OperatorTypes {
		STRING_OPERATORS(
			STRING_IS,
			STRING_IS_NOT,
			STRING_CONTAINS,	
			STRING_DOES_NOT_CONTAIN
		),

		DATE_OPERATORS(
			DATE_EQUAL,
			DATE_NOT_EQUAL,
			DATE_GREATER_THAN,
			DATE_GREATER_OR_EQUAL_TO,
			DATE_SMALLER_THAN,
			DATE_SMALLER_OR_EQUAL_TO
		),

		ARRAY_LIST_OPERATORS(
			LIST_CONTAINS,
			LIST_DOES_NO_CONTAIN
		),

		VERSION_OPERATORS(
			VERSION_EQUAL,
			VERSION_NOT_EQUAL,
			VERSION_GREATER_THAN,
			VERSION_GREATER_OR_EQUAL_TO,
			VERSION_SMALLER_THAN,
			VERSION_SMALLER_OR_EQUAL_TO
		),

		PACKAGE_TYPE_OPERATORS(
			PACKAGE_IS,
			PACKAGE_IS_NOT
		);
		
		/**Mapping operator names to enum elements.*/
		private final TreeMap< String, Operator >	nameMap;
		
		/**Initializer that fills the map for each item.
		 * 
		 * @param operators Operators that belong to this array. Should be ordered correctly,
		 * for the sake of compatibility.
		 */
		private OperatorTypes( Operator ... operators ) {
			nameMap = new TreeMap< String, Operator >();
			for ( Operator operator : operators ) {
				nameMap.put( operator.name, operator );
			}
		}
		
		/**Ready for log time access? This will be much better than returning the whole
		 * arrays and reading them outside this class.
		 * 
		 * @param name Name of the requested operator.
		 * @return Enum item corresponding to the operator.
		 */
		public Operator forName( String name ) {
			return nameMap.get( name );
		}
		
		/**This method only exists for backward compatibility with some weird web interface
		 * templates. It should be removed in the future.
		 * 
		 * @return An array of all operators this member maps.
		 */
		@Deprecated
		public Operator[] getAllOperators() {
			return nameMap.values().toArray( new Operator[ nameMap.size() ] );
		}
	}

	/**Mapping classes to operator types.*/
	private static final TreeMap< Class< ? >, OperatorTypes > operatorMap;

	static {
		operatorMap = new TreeMap< Class< ? >, OperatorTypes >();
		
		operatorMap.put(String.class, OperatorTypes.STRING_OPERATORS );
		operatorMap.put(Date.class, OperatorTypes.DATE_OPERATORS );
		operatorMap.put(ArrayList.class, OperatorTypes.ARRAY_LIST_OPERATORS );
		operatorMap.put(Version.class, OperatorTypes.VERSION_OPERATORS );
		operatorMap.put(PackageType.class, OperatorTypes.PACKAGE_TYPE_OPERATORS );
	}

	/**Operator name (identification).*/
	private String name;
	
	/**Operator title (intended to be displayed in the UI).*/
	private String title;

	/**Name getter
	 * 
	 * @return Machine-readable name of the operator.
	 */
	public String getName() {
		return name;
	}

	/**Title getter
	 * 
	 * @return Human readable title of the operator.
	 */
	public String getTitle() {
		return title;
	}

	/**The new generation operator getter that simply doesn't give out the whole array.
	 * 
	 * @param klass Class name to be found.
	 * @param name Operator name to be found.
	 * @return the corresponding operator instance.
	 */
	public static Operator forClassAndName( Class< ? > klass, String name ) {
		OperatorTypes	types;
		
		if ( ( types = operatorMap.get( klass ) ) == null ) {
			return null;
		} else {
			return types.forName( name );
		}
	}
	
	/**Returns a set of classes for which we have operators.
	 * 
	 * @return set of classes
	 */
	public static Set< Class< ? > > getKlasses() {
		return operatorMap.keySet();
	}
	
	/**A deprecated and inefficient method for operator access. Use {@code forClassAndName()}
	 * instead. There are some ugly old web interface templates that require this. Once they
	 * are removed, this can go away, too.
	 * 
	 * @param klass The data class to retrieve.
	 * @return An array of operators available for that class.
	 */
	@Deprecated
	public static Operator[] forKlass( Class< ? > klass ) {
		OperatorTypes types;
		
		if ( ( types = operatorMap.get( klass ) ) == null ) {
			return new Operator[ 0 ];
		} else {
			return types.getAllOperators();
		}
	}

	/**Allocates a new <code>Operator</code> object.
	 * 
	 * @param name operator name (identification)
	 * @param title operator title (intended to be displayed in the UI)
	 */
	private Operator(String name, String title) {
		this.name = name;
		this.title = title;
	}

	/**Function executed during the queries. It tells whether two objects
	 * are in relation according to this oeprator.
	 * 
	 * Note that descendats can constrain allowed argument types.
	 * 
	 * <code>metadataValue</code> parameter can be null, because packages do not
	 * necessairly have all metadata attributes specified. On the contrary,
	 * <code>formvalue</code> can't be null, as there is no way for the web
	 * interface user to specify <code>null</code> value.
	 * 
	 * All operators should return <code>false</code> if the
	 * <code>metadataValue</code> parameter is <code>null</code>, because it is
	 * the only consistent behaviour.
	 * 
	 * @param metadataValue value of the package metadata attribute, can be <code>null</code> 
	 * @param formValue value of the form field, can't be <code>null</code>
	 * @return <code>true</code> if both arguments are in the relation
	 *          represented by this operator;
	 *          <code>false</code> otherwise
	 */
	public abstract boolean apply(Object metadataValue, Object formValue);
}
