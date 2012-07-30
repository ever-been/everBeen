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
package cz.cuni.mff.been.clinterface.adapters;

import java.util.List;
import java.util.TreeMap;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.HostsModule.Errors;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.value.ValueBoolean;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueDouble;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueList;
import cz.cuni.mff.been.hostmanager.value.ValueRange;
import cz.cuni.mff.been.hostmanager.value.ValueRegexp;
import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueType;
import cz.cuni.mff.been.hostmanager.value.ValueVersion;
import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.properties.NamedBoolean;
import cz.cuni.mff.been.jaxb.properties.NamedDouble;
import cz.cuni.mff.been.jaxb.properties.NamedInt;
import cz.cuni.mff.been.jaxb.properties.NamedListBoolean;
import cz.cuni.mff.been.jaxb.properties.NamedListDouble;
import cz.cuni.mff.been.jaxb.properties.NamedListInt;
import cz.cuni.mff.been.jaxb.properties.NamedListRegexp;
import cz.cuni.mff.been.jaxb.properties.NamedListString;
import cz.cuni.mff.been.jaxb.properties.NamedListVersion;
import cz.cuni.mff.been.jaxb.properties.NamedRangeBoolean;
import cz.cuni.mff.been.jaxb.properties.NamedRangeDouble;
import cz.cuni.mff.been.jaxb.properties.NamedRangeInt;
import cz.cuni.mff.been.jaxb.properties.NamedRangeRegexp;
import cz.cuni.mff.been.jaxb.properties.NamedRangeString;
import cz.cuni.mff.been.jaxb.properties.NamedRangeVersion;
import cz.cuni.mff.been.jaxb.properties.NamedRegexp;
import cz.cuni.mff.been.jaxb.properties.NamedString;
import cz.cuni.mff.been.jaxb.properties.NamedVersion;

/**
 * Converts elementary name-value pairs to and from Namedir JAXB representation. When modifying
 * this class, don't forget to merge all Named changes to {@code PropertiesNVPConvertor} as well.
 * Unfortunately, neiNamedr Java nor JAXB offer better means to do this.
 * 
 * @author Andrej Podzimek
 */
final class HostNVPConvertor {

	/**
	 * A common interface for data convertors mapped by class name.
	 * 
	 * @author Andrej Podzimek
	 */
	private interface ValueInterface {
		
		/**
		 * Converts an AbstractSerializable instance (which is expected to be of the same class
		 * name as the corresponding map item) to a NameValuePair.
		 * 
		 * @param something One of the elements bool, double, int, string, value, regexp, ... 
		 * @return A NameValue pair.
		 */
		NameValuePair convert( AbstractSerializable something );
	}

	/** A map of value convertors. */
	private static final TreeMap< String, ValueInterface > convertors;

	static {
		convertors = new TreeMap< String, ValueInterface >();
		convertors.put(
			NamedBoolean.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedBoolean namedBoolean;
					
					namedBoolean = (NamedBoolean) something;
					return new NameValuePair(
						namedBoolean.getName(),
						new ValueBoolean( namedBoolean.getValue() )
					);
				}
			}
		);
		convertors.put(
			NamedDouble.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedDouble namedDouble;
					
					namedDouble = (NamedDouble) something;
					return new NameValuePair(
						namedDouble.getName(),
						new ValueDouble( namedDouble.getValue() )
					);
				}
			}
		);
		convertors.put(
			NamedInt.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedInt namedInt;
					
					namedInt = (NamedInt) something;
					return new NameValuePair(
						namedInt.getName(),
						new ValueInteger( namedInt.getValue() )
					);
				}
			}
		);
		convertors.put(
			NamedString.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedString namedString;
					
					namedString = (NamedString) something;
					return new NameValuePair(
						namedString.getName(),
						new ValueString( namedString.getValue() )
					);
				}
			}
		);
		convertors.put(
			NamedRegexp.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRegexp namedRegexp;
					
					namedRegexp = (NamedRegexp) something;
					return new NameValuePair(
						namedRegexp.getName(),
						new ValueRegexp( namedRegexp.getValue() )
					);
				}
			}
		);
		convertors.put(
			NamedVersion.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedVersion namedVersion;
					
					namedVersion = (NamedVersion) something;
					return new NameValuePair(
						namedVersion.getName(),
						namedVersion.getValue()
					);
				}
			}
		);
		
		convertors.put(
			NamedRangeBoolean.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeBoolean rangeBoolean;
					ValueBoolean lb;
					ValueBoolean ub;
					boolean ol;
					boolean or;

					rangeBoolean = (NamedRangeBoolean) something;
					if ( rangeBoolean.isSetLBound() ) {
						NamedRangeBoolean.LBound lBound;

						lBound = rangeBoolean.getLBound();
						lb = new ValueBoolean( lBound.getValue() );									// Unit ignored!!!
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeBoolean.isSetUBound() ) {
						NamedRangeBoolean.UBound uBound;
						
						uBound = rangeBoolean.getUBound();
						ub = new ValueBoolean( uBound.getValue() );									// Unit ignored!!!
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeBoolean.getName(),
						new ValueRange< ValueBoolean >( lb, ub, ol, or, ValueType.BOOLEAN )
					);
				}
			}
		);
		convertors.put(
			NamedRangeDouble.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeDouble rangeDouble;
					ValueDouble lb;
					ValueDouble ub;
					boolean ol;
					boolean or;

					rangeDouble = (NamedRangeDouble) something;
					if ( rangeDouble.isSetLBound() ) {
						NamedRangeDouble.LBound lBound;

						lBound = rangeDouble.getLBound();
						lb = lBound.isSetUnit() ?
							new ValueDouble( lBound.getValue(), lBound.getUnit() ) :
							new ValueDouble( lBound.getValue() );
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeDouble.isSetUBound() ) {
						NamedRangeDouble.UBound uBound;
						
						uBound = rangeDouble.getUBound();
						ub = uBound.isSetUnit() ?
							new ValueDouble( uBound.getValue(), uBound.getUnit() ) :
							new ValueDouble( uBound.getValue() );
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeDouble.getName(),
						new ValueRange< ValueDouble >( lb, ub, ol, or, ValueType.DOUBLE )
					);
				}
			}
		);
		convertors.put(
			NamedRangeInt.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeInt rangeInt;
					ValueInteger lb;
					ValueInteger ub;
					boolean ol;
					boolean or;

					rangeInt = (NamedRangeInt) something;
					if ( rangeInt.isSetLBound() ) {
						NamedRangeInt.LBound lBound;

						lBound = rangeInt.getLBound();
						lb = lBound.isSetUnit() ?
							new ValueInteger( lBound.getValue(), lBound.getUnit() ) :
							new ValueInteger( lBound.getValue() );
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeInt.isSetUBound() ) {
						NamedRangeInt.UBound uBound;
						
						uBound = rangeInt.getUBound();
						ub = uBound.isSetUnit() ?
							new ValueInteger( uBound.getValue(), uBound.getUnit() ) :
							new ValueInteger( uBound.getValue() );
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeInt.getName(),
						new ValueRange< ValueInteger >( lb, ub, ol, or, ValueType.INTEGER )
					);
				}
			}
		);
		convertors.put(
			NamedRangeString.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeString rangeString;
					ValueString lb;
					ValueString ub;
					boolean ol;
					boolean or;

					rangeString = (NamedRangeString) something;
					if ( rangeString.isSetLBound() ) {
						NamedRangeString.LBound lBound;

						lBound = rangeString.getLBound();
						lb = new ValueString( lBound.getValue() );									// Unit ignored!!!
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeString.isSetUBound() ) {
						NamedRangeString.UBound uBound;
						
						uBound = rangeString.getUBound();
						ub = new ValueString( uBound.getValue() );									// Unit ignored!!!
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeString.getName(),
						new ValueRange< ValueString >( lb, ub, ol, or, ValueType.STRING )
					);
				}
			}
		);
		convertors.put(
			NamedRangeRegexp.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeRegexp rangeRegexp;
					ValueRegexp lb;
					ValueRegexp ub;
					boolean ol;
					boolean or;

					rangeRegexp = (NamedRangeRegexp) something;
					if ( rangeRegexp.isSetLBound() ) {
						NamedRangeRegexp.LBound lBound;

						lBound = rangeRegexp.getLBound();
						lb = new ValueRegexp( lBound.getValue() );									// Unit ignored!!!
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeRegexp.isSetUBound() ) {
						NamedRangeRegexp.UBound uBound;
						
						uBound = rangeRegexp.getUBound();
						ub = new ValueRegexp( uBound.getValue() );									// Unit ignored!!!
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeRegexp.getName(),
						new ValueRange< ValueRegexp >( lb, ub, ol, or, ValueType.REGEXP )
					);
				}
			}
		);
		convertors.put(
			NamedRangeVersion.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedRangeVersion rangeVersion;
					ValueVersion lb;
					ValueVersion ub;
					boolean ol;
					boolean or;

					rangeVersion = (NamedRangeVersion) something;
					if ( rangeVersion.isSetLBound() ) {
						NamedRangeVersion.LBound lBound;

						lBound = rangeVersion.getLBound();
						lb = lBound.getValue();														// Unit ignored!!!
						ol = lBound.isOpen();
					} else {
						lb = null;
						ol = true;
					}
					if ( rangeVersion.isSetUBound() ) {
						NamedRangeVersion.UBound uBound;
						
						uBound = rangeVersion.getUBound();
						ub = uBound.getValue();														// Unit ignored!!!
						or = uBound.isOpen();
					} else {
						ub = null;
						or = true;
					}
					return new NameValuePair(
						rangeVersion.getName(),
						new ValueRange< ValueVersion >( lb, ub, ol, or, ValueType.VERSION )
					);
				}
			}
		);

		convertors.put(
			NamedListBoolean.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListBoolean listBoolean;
					ValueList< ValueBoolean > list;
					ValueBoolean item;

					listBoolean = (NamedListBoolean) something;
					list = new ValueList< ValueBoolean >( ValueType.BOOLEAN );
					for ( NamedListBoolean.Item i : listBoolean.getItem() ) {
						item = new ValueBoolean( i.isValue() );
						list.add( item );
					}
					return new NameValuePair( listBoolean.getName(), list );
				}
			}
		);
		convertors.put(
			NamedListDouble.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListDouble listDouble;
					ValueList< ValueDouble > list;
					ValueDouble item;

					listDouble = (NamedListDouble) something;
					list = new ValueList< ValueDouble >( ValueType.DOUBLE );
					for ( NamedListDouble.Item i : listDouble.getItem() ) {
						item = i.isSetUnit() ?
							new ValueDouble( i.getValue(), i.getUnit() ) :
							new ValueDouble( i.getValue() );
						list.add( item );
					}
					return new NameValuePair( listDouble.getName(), list );
				}
			}
		);
		convertors.put(
			NamedListInt.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListInt listInt;
					ValueList< ValueInteger > list;
					ValueInteger item;

					listInt = (NamedListInt) something;
					list = new ValueList< ValueInteger >( ValueType.INTEGER );
					for ( NamedListInt.Item i : listInt.getItem() ) {
						item = i.isSetUnit() ?
							new ValueInteger( i.getValue(), i.getUnit() ) :
							new ValueInteger( i.getValue() );
						list.add( item );
					}
					return new NameValuePair( listInt.getName(), list );
				}
			}
		);
		convertors.put(
			NamedListString.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListString listString;
					ValueList< ValueString > list;
					ValueString item;

					listString = (NamedListString) something;
					list = new ValueList< ValueString >( ValueType.STRING );
					for ( NamedListString.Item i : listString.getItem() ) {
						item = new ValueString( i.getValue() );
						list.add( item );
					}
					return new NameValuePair( listString.getName(), list );
				}
			}
		);
		convertors.put(
			NamedListRegexp.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListRegexp listRegexp;
					ValueList< ValueRegexp > list;
					ValueRegexp item;

					listRegexp = (NamedListRegexp) something;
					list = new ValueList< ValueRegexp >( ValueType.REGEXP );
					for ( NamedListRegexp.Item i : listRegexp.getItem() ) {
						item = new ValueRegexp( i.getValue() );
						list.add( item );
					}
					return new NameValuePair( listRegexp.getName(), list );
				}
			}
		);
		convertors.put(
			NamedListVersion.class.getSimpleName(),
			new ValueInterface() {
				
				@Override
				public NameValuePair convert( AbstractSerializable something ) {
					NamedListVersion listVersion;
					ValueList< ValueVersion > list;
					ValueVersion item;

					listVersion = (NamedListVersion) something;
					list = new ValueList< ValueVersion >( ValueType.VERSION );
					for ( NamedListVersion.Item i : listVersion.getItem() ) {
						item =  i.getValue();
						list.add( item );
					}
					return new NameValuePair( listVersion.getName(), list );
				}
			}
		);
	}
	
	/**
	 * Converts a name-value pair to a JAXB-based XML representation.
	 * 
	 * @param pair The pair to convert.
	 * @return A JAXB-based equivalent of the pair.
	 * @throws ModuleSpecificException When an integrity error is detected.
	 */
	static AbstractSerializable nvpToItem( NameValuePair pair )
	throws ModuleSpecificException {
		ValueCommonInterface commonValue;
		
		commonValue = pair.getValue();
		switch ( commonValue.getType() ) {
			case BOOLEAN: {
				NamedBoolean named;
				ValueBoolean value;

				named = Factory.PROPERTIES.createNamedBoolean();
				named.setName( pair.getName() );
				value = (ValueBoolean) commonValue;
				named.setValue( value.getValue() );
				return named;
			}
			case DOUBLE: {
				NamedDouble named;
				ValueDouble value;
				String unit;

				named = Factory.PROPERTIES.createNamedDouble();
				named.setName( pair.getName() );
				value = (ValueDouble) commonValue;
				named.setValue( value.doubleValue() );
				if ( null != ( unit = value.getUnit() ) ) {
					named.setUnit( unit );
				}
				return named;
			}
			case INTEGER: {
				NamedInt named;
				ValueInteger value;
				String unit;

				named = Factory.PROPERTIES.createNamedInt();
				named.setName( pair.getName() );
				value = (ValueInteger) commonValue;
				named.setValue( value.intValue() );
				if ( null != ( unit = value.getUnit() ) ) {
					named.setUnit( unit );
				}
				return named;
			}
			case STRING: {
				NamedString named;
				ValueString value;

				named = Factory.PROPERTIES.createNamedString();
				named.setName( pair.getName() );
				value = (ValueString) commonValue;
				named.setValue( value.getValue() );
				return named;
			}
			case REGEXP: {
				NamedRegexp named;
				ValueRegexp value;

				named = Factory.PROPERTIES.createNamedRegexp();
				named.setName( pair.getName() );
				value = (ValueRegexp) commonValue;
				named.setValue( value.getPattern() );
				return named;
			}
			case VERSION: {
				NamedVersion named;
				ValueVersion value;

				named = Factory.PROPERTIES.createNamedVersion();
				named.setName( pair.getName() );
				value = (ValueVersion) commonValue;
				named.setValue( value );
				return named;
			}
			case RANGE:
				return valueToRange( pair.getName(), pair.getValue() );
			case LIST:
				return valueToList( pair.getName(), pair.getValue() );
			default:
				throw new ModuleSpecificException(
					Errors.INTG_NVP,
					" (" + String.valueOf( commonValue.getType() ) + ')'
				);
		}
	}
	
	/**
	 * Converts an item from the list of possible values to a name-value pair. Possible values
	 * are bool, double, int, string, regexp, version, rbool, rdouble, rint, rstring, rregexp,
	 * rversion, lbool, ldouble, lint, lstring, lreexp and lversion.
	 * 
	 * @param item The JAXB-based structure to convert.
	 * @return A name-value pair used by the Host Manager.
	 * @throws ModuleSpecificException When an integrity error is detected.
	 */
	static NameValuePair itemToNVP( AbstractSerializable item ) throws ModuleSpecificException {
		ValueInterface convertor;
		
		convertor = convertors.get( item.getClass().getSimpleName() );
		if ( null == convertor ) {
			throw new ModuleSpecificException(
				Errors.INTG_PROPS,
				" (" + item.getClass().getSimpleName() + ')'
			);
		}
		return convertor.convert( item );
	}

	/**
	 * Converts a {@code ValueRange< ? >} to the corresponding JAXB-based representation.
	 * 
	 * @param name Name of the original name-value pair.
	 * @param value The value that can be cast to a {@code ValueRange< ? > }.
	 * @return A JAXB-based instance representing the range.
	 * @throws ModuleSpecificException When an integrity error is detected.
	 */
	@SuppressWarnings( "unchecked" )
	private static AbstractSerializable valueToRange( String name, ValueCommonInterface value )
	throws ModuleSpecificException {
		switch ( value.getElementType() ) {
			case BOOLEAN: {
				ValueRange< ValueBoolean > castValue;
				NamedRangeBoolean result;
				ValueBoolean bound;
				
				castValue = (ValueRange< ValueBoolean >) value;
				result = Factory.PROPERTIES.createNamedRangeBoolean();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeBoolean.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeBooleanLBound();
					lBound.setValue( bound.getValue() );
					lBound.setOpen( castValue.isLeftOpen() );
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeBoolean.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeBooleanUBound();
					uBound.setValue( bound.getValue() );
					uBound.setOpen( castValue.isRightOpen() );
					result.setUBound( uBound );
				}
				return result;
			}
			case DOUBLE: {
				ValueRange< ValueDouble > castValue;
				NamedRangeDouble result;
				ValueDouble bound;
				String unit;
				
				castValue = (ValueRange< ValueDouble >) value;
				result = Factory.PROPERTIES.createNamedRangeDouble();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeDouble.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeDoubleLBound();
					lBound.setValue( bound.doubleValue() );
					lBound.setOpen( castValue.isLeftOpen() );
					if ( null != ( unit = bound.getUnit() ) ) {
						lBound.setUnit( unit );
					}
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeDouble.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeDoubleUBound();
					uBound.setValue( bound.doubleValue() );
					uBound.setOpen( castValue.isRightOpen() );
					if ( null != ( unit = bound.getUnit() ) ) {
						uBound.setUnit( unit );
					}
					result.setUBound( uBound );
				}
				return result;
			}
			case INTEGER: {
				ValueRange< ValueInteger > castValue;
				NamedRangeInt result;
				ValueInteger bound;
				String unit;
				
				castValue = (ValueRange< ValueInteger >) value;
				result = Factory.PROPERTIES.createNamedRangeInt();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeInt.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeIntLBound();
					lBound.setValue( bound.intValue() );
					lBound.setOpen( castValue.isLeftOpen() );
					if ( null != ( unit = bound.getUnit() ) ) {
						lBound.setUnit( unit );
					}
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeInt.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeIntUBound();
					uBound.setValue( bound.intValue() );
					uBound.setOpen( castValue.isRightOpen() );
					if ( null != ( unit = bound.getUnit() ) ) {
						uBound.setUnit( unit );
					}
					result.setUBound( uBound );
				}
				return result;
			}
			case REGEXP: {																			// NONSENSE!!!
				ValueRange< ValueRegexp > castValue;
				NamedRangeRegexp result;
				ValueRegexp bound;
				
				castValue = (ValueRange< ValueRegexp >) value;
				result = Factory.PROPERTIES.createNamedRangeRegexp();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeRegexp.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeRegexpLBound();
					lBound.setValue( bound.getPattern() );
					lBound.setOpen( castValue.isLeftOpen() );
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeRegexp.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeRegexpUBound();
					uBound.setValue( bound.getPattern() );
					uBound.setOpen( castValue.isRightOpen() );
					result.setUBound( uBound );
				}
				return result;
			}
			case STRING: {
				ValueRange< ValueString > castValue;
				NamedRangeString result;
				ValueString bound;
				
				castValue = (ValueRange< ValueString >) value;
				result = Factory.PROPERTIES.createNamedRangeString();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeString.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeStringLBound();
					lBound.setValue( bound.getValue() );
					lBound.setOpen( castValue.isLeftOpen() );
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeString.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeStringUBound();
					uBound.setValue( bound.getValue() );
					uBound.setOpen( castValue.isRightOpen() );
					result.setUBound( uBound );
				}
				return result;
			}
			case VERSION: {
				ValueRange< ValueVersion > castValue;
				NamedRangeVersion result;
				ValueVersion bound;
				
				castValue = (ValueRange< ValueVersion >) value;
				result = Factory.PROPERTIES.createNamedRangeVersion();
				result.setName( name );
				if ( null != ( bound = castValue.getMinValue() ) ) {
					NamedRangeVersion.LBound lBound;
					
					lBound = Factory.PROPERTIES.createNamedRangeVersionLBound();
					lBound.setValue( bound );
					lBound.setOpen( castValue.isLeftOpen() );
					result.setLBound( lBound );
				}
				if ( null != ( bound = castValue.getMaxValue() ) ) {
					NamedRangeVersion.UBound uBound;

					uBound = Factory.PROPERTIES.createNamedRangeVersionUBound();
					uBound.setValue( bound );
					uBound.setOpen( castValue.isRightOpen() );
					result.setUBound( uBound );
				}
				return result;
			}
			default:
				throw new ModuleSpecificException(
					Errors.INTG_NVP,
					" (" + String.valueOf( value.getElementType() ) + ')'
				);
		}
	}
	
	/**
	 * Converts a {@code ValueList< ? >} to the corresponding JAXB-based representation.
	 * 
	 * @param name Name of the original name-value pair.
	 * @param value The value that can be cast to a {@code ValueList< ? >}.
	 * @return A JAXB-based instance representing the list.
	 * @throws ModuleSpecificException When an integrity error is detected.
	 */
	@SuppressWarnings( "unchecked" )
	private static AbstractSerializable valueToList( String name, ValueCommonInterface value )
	throws ModuleSpecificException {
		long index;
		
		index = 0;
		switch ( value.getElementType() ) {
			case BOOLEAN: {
				ValueList< ValueBoolean > castValue;
				NamedListBoolean result;
				List< NamedListBoolean.Item > items;
				NamedListBoolean.Item item;
				
				castValue = (ValueList< ValueBoolean >) value;
				result = Factory.PROPERTIES.createNamedListBoolean();
				result.setName( name );
				items = result.getItem();
				for ( ValueBoolean vb : castValue ) {
					item = Factory.PROPERTIES.createNamedListBooleanItem();
					item.setIndex( index++ );
					item.setValue( vb.getValue() );
					items.add( item );
				}
				return result;
			}
			case DOUBLE: {
				ValueList< ValueDouble > castValue;
				NamedListDouble result;
				List< NamedListDouble.Item > items;
				NamedListDouble.Item item;
				String unit;
				
				castValue = (ValueList< ValueDouble >) value;
				result = Factory.PROPERTIES.createNamedListDouble();
				result.setName( name );
				items = result.getItem();
				for ( ValueDouble vd : castValue ) {
					item = Factory.PROPERTIES.createNamedListDoubleItem();
					item.setIndex( index++ );
					item.setValue( vd.doubleValue() );
					if ( null != ( unit = vd.getUnit() ) ) {
						item.setUnit( unit );
					}
					items.add( item );
				}
				return result;
			}
			case INTEGER: {
				ValueList< ValueInteger > castValue;
				NamedListInt result;
				List< NamedListInt.Item > items;
				NamedListInt.Item item;
				String unit;
				
				castValue = (ValueList< ValueInteger >) value;
				result = Factory.PROPERTIES.createNamedListInt();
				result.setName( name );
				items = result.getItem();
				for ( ValueInteger vi : castValue ) {
					item = Factory.PROPERTIES.createNamedListIntItem();
					item.setIndex( index++ );
					item.setValue( vi.intValue() );
					if ( null != ( unit = vi.getUnit() ) ) {
						item.setUnit( unit );
					}
					items.add( item );
				}
				return result;
			}
			case REGEXP: {																			// NONSENSE!!!
				ValueList< ValueRegexp > castValue;
				NamedListRegexp result;
				List< NamedListRegexp.Item > items;
				NamedListRegexp.Item item;
				
				castValue = (ValueList< ValueRegexp >) value;
				result = Factory.PROPERTIES.createNamedListRegexp();
				result.setName( name );
				items = result.getItem();
				for ( ValueRegexp vr : castValue ) {
					item = Factory.PROPERTIES.createNamedListRegexpItem();
					item.setIndex( index++ );
					item.setValue( vr.getPattern() );
					items.add( item );
				}
				return result;
			}
			case STRING: {
				ValueList< ValueString > castValue;
				NamedListString result;
				List< NamedListString.Item > items;
				NamedListString.Item item;
				
				castValue = (ValueList< ValueString >) value;
				result = Factory.PROPERTIES.createNamedListString();
				result.setName( name );
				items = result.getItem();
				for ( ValueString vs : castValue ) {
					item = Factory.PROPERTIES.createNamedListStringItem();
					item.setIndex( index++ );
					item.setValue( vs.getValue() );
					items.add( item );
				}
				return result;
			}
			case VERSION: {
				ValueList< ValueVersion > castValue;
				NamedListVersion result;
				List< NamedListVersion.Item > items;
				NamedListVersion.Item item;
				
				castValue = (ValueList< ValueVersion >) value;
				result = Factory.PROPERTIES.createNamedListVersion();
				result.setName( name );
				items = result.getItem();
				for ( ValueVersion vv : castValue ) {
					item = Factory.PROPERTIES.createNamedListVersionItem();
					item.setIndex( index++ );
					item.setValue( vv );
					items.add( item );
				}
				return result;
			}
			default:
				throw new ModuleSpecificException(
					Errors.INTG_NVP,
					" (" + String.valueOf( value.getElementType() ) + ')'
				);
		}
	}	
}
