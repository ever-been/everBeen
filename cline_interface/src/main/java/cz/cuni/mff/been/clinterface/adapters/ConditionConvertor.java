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

import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.ResultsModule.Errors;
import cz.cuni.mff.been.common.serialize.Deserialize;
import cz.cuni.mff.been.common.serialize.DeserializeException;
import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.Compare;
import cz.cuni.mff.been.jaxb.td.Condition;
import cz.cuni.mff.been.jaxb.td.Logical;
import cz.cuni.mff.been.jaxb.td.NullCompare;
import cz.cuni.mff.been.jaxb.td.StrVal;
import cz.cuni.mff.been.jaxb.td.True;
import cz.cuni.mff.been.resultsrepositoryng.condition.GroupCondition;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;

/**
 * A bunch of static methods for data structure conversion.
 *
 * @author Andrej Podzimek
 */
public final class ConditionConvertor {

	/**
	 * A callback interface for logical operators.
	 *
	 * @author Andrej Podzimek
	 */
	private static interface LogicalInterface {

		/**
		 * Translates a XML representation of a logical operator to its Condition counterpart.
		 *
		 * @param condition The Condition obtained from XML to translate.
		 * @return A Condition accepted by the Results Repository.
		 * @throws ModuleSpecificException When a severe XML integrity error is encountered.
		 */
		cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert( Logical condition )
		throws ModuleSpecificException;
	}

	/**
	 * A callback interface for comparison operators.
	 *
	 * @author Andrej Podzimek
	 */
	private static interface CompareInterface {

		/**
		 * Translates a XML representation of a comparison operator to its Condition counterpart.
		 * @param condition The Condition obtained from XML to translate.
		 * @return A Condition accepted by the Results Repository.
		 * @throws ModuleSpecificException When there is an error to report.
		 */
		cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert( Compare condition )
		throws ModuleSpecificException;
	}

	/** Map of logical operator convertors. */
	private static final TreeMap< String, LogicalInterface > logicalMap;

	/** Map of comparison operator convertors. */
	private static final TreeMap< String, CompareInterface > compareMap;

	static {
		logicalMap = new TreeMap< String, LogicalInterface >();
		compareMap = new TreeMap< String, CompareInterface >();

		logicalMap.put(
			"and",
			new LogicalInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Logical condition
				) throws ModuleSpecificException {
					return logicalToCondition( condition, Restrictions.conjunction() );
				}
			}
		);
		logicalMap.put(
			"or",
			new LogicalInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Logical condition
				) throws ModuleSpecificException {
					return logicalToCondition( condition, Restrictions.disjunction() );
				}
			}
		);

		compareMap.put(
			"eq",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.eq( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
		compareMap.put(
			"ne",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.ne( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
		compareMap.put(
			"ge",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.ge( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
		compareMap.put(
			"gt",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.gt( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
		compareMap.put(
			"le",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.le( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
		compareMap.put(
			"lt",
			new CompareInterface() {

				@Override
				public cz.cuni.mff.been.resultsrepositoryng.condition.Condition convert(
					Compare condition
				) throws ModuleSpecificException {
					return Restrictions.lt( condition.getProperty(), compareToValue( condition ) );
				}
			}
		);
	}

	/**
	 * Don't do this.
	 */
	private ConditionConvertor() {
	}

	/**
	 * Converts a Condition instance from XML to a Condition used by the Results Repository
	 *
	 * @param condition The Condition instance to read from.
	 * @return A Condition instance used by the Results Repository.
	 * @throws ModuleSpecificException When class instantiation or XML integrity problems occur.
	 */
	static cz.cuni.mff.been.resultsrepositoryng.condition.Condition conditionToCondition(
		Condition condition
	) throws ModuleSpecificException {
		if ( null == condition ) {
			return null; // Restrictions.alwaysTrue();												// Can be null.
		}

		return operandToCondition( condition.getOperand() );
	}

	/**
	 * Converts a Condition used by the Results repository to a JAXB-based Condition instance.
	 * 
	 * @param condition A Condition instance used by the Results Repository.
	 * @param binary Whether objects are printed out using toString() or serialized to base64.
	 * @return A Condition instance suitable for marshalling.
	 * @throws ModuleSpecificException On serialization problems.
	 * @throws IOException When object serialization fails. It should never fail.
	 */
	static Condition conditionToCondition(
		cz.cuni.mff.been.resultsrepositoryng.condition.Condition condition,
		boolean binary
	) throws ModuleSpecificException {
		if ( null == condition ) {
			return null;																			// Is this correct?
		}
		
		Condition result;
		
		result = Factory.TD.createCondition();
		try {
			result.setOperand( condition.buildJAXBStructure( binary ) );
		} catch ( IOException exception ) {															// Special meaning here.
			throw new ModuleSpecificException( Errors.FAIL_SER, exception );
		}
		return result;
	}
	
	/**
	 * Converts a condition operator from XML to a Condition used by the Results Repository.
	 *
	 * @param operand The logical or comparison operator to convert.
	 * @return An instance of Condition that can be used by the Results Repository.
	 * @throws ModuleSpecificException When XML integrity errors are detected.
	 */
	private static cz.cuni.mff.been.resultsrepositoryng.condition.Condition operandToCondition(
		JAXBElement< ? extends AbstractSerializable > operand
	) throws ModuleSpecificException {

		if ( Compare.class == operand.getDeclaredType() ) {
			CompareInterface convertor;

			convertor = compareMap.get( operand.getName().getLocalPart() );
			if ( null == convertor ) {
				throw new ModuleSpecificException(
					Errors.INTG_COND,
					" (" + operand.getName().toString() + ')'
				);
			}
			return convertor.convert( (Compare) operand.getValue() );
		} else if ( Logical.class == operand.getDeclaredType() ) {
			LogicalInterface convertor;

			convertor = logicalMap.get( operand.getName().getLocalPart() );
			if ( null == convertor ) {
				throw new ModuleSpecificException(
					Errors.INTG_COND,
					" (" + operand.getName().toString() + ')'
				);
			}
			return convertor.convert( (Logical) operand.getValue() );
		} else if ( NullCompare.class == operand.getDeclaredType() ) {
			NullCompare compare;
			
			compare = (NullCompare) operand.getValue();
			return compare.isValue() ?
				Restrictions.isNull( compare.getProperty() ) :
				Restrictions.isNotNull( compare.getProperty() );
		} else if ( True.class == operand.getDeclaredType() ) {
			return Restrictions.alwaysTrue();
		} else {
			throw new ModuleSpecificException(
				Errors.INTG_COND,
				" (" + operand.getName().toString() + ')'
			);
		}
	}

	/**
	 * Converts a Compare instance from XML to an Object representing the compared value.
	 *
	 * @param condition The comparison operator condition to convert.
	 * @return An Object representing the value to compare.
	 * @throws ModuleSpecificException When class deserialization or instantiation fails.
	 */
	private static Serializable compareToValue( Compare condition ) throws ModuleSpecificException {
		if ( condition.isSetBinVal() ) {
			try {
				return Deserialize.fromBase64( condition.getBinVal() );
			} catch ( DeserializeException exception ) {
				throw new ModuleSpecificException(
					exception.getError( Errors.class ),
					" (" + condition.getProperty() + ')',
					exception.getCause()															// DeserializeException ommitted.
				);
			}
		} else {																					// assuming isSetStrVal()
			StrVal strVal;
			
			strVal = condition.getStrVal();
			try {
				return Deserialize.fromString( strVal );
			} catch ( DeserializeException exception ) {
				throw new ModuleSpecificException(
					exception.getError( Errors.class ),
					" (" + strVal.getClazz() + ')',
					exception.getCause()															// DeserializeException ommitted.
				);
			}
		}
	}

	/**
	 * Converts a Logical instance from XML to a GroupCondition instance.
	 *
	 * @param condition The logical operator condition to convert.
	 * @param result The group condition to which all the subconditions should be added.
	 * @return The {@code result} parameter.
	 * @throws ModuleSpecificException When XML integrity errors are detected.
	 */
	private static GroupCondition logicalToCondition( Logical condition, GroupCondition result )
	throws ModuleSpecificException {
		for ( JAXBElement< ? extends AbstractSerializable > element : condition.getOperand() ) {
			if ( Logical.class == element.getDeclaredType() ) {
				LogicalInterface convertor;

				convertor = logicalMap.get( element.getName().getLocalPart() );
				if ( null == convertor ) {
					throw new ModuleSpecificException(
						Errors.INTG_COND,
						" (" + element.getName().toString() + ')'
					);
				}
				result.add( convertor.convert( (Logical) element.getValue() ) );
			} else if ( Compare.class == element.getDeclaredType() ) {
				CompareInterface convertor;

				convertor = compareMap.get( element.getName().getLocalPart() );
				if ( null == convertor ) {
					throw new ModuleSpecificException(
						Errors.INTG_COND,
						" (" + element.getName().toString() + ')'
					);
				}
				result.add( convertor.convert( (Compare) element.getValue() ) );
			} else if ( NullCompare.class == element.getDeclaredType() ) {
				NullCompare nullCompare;
				
				nullCompare = (NullCompare) element.getValue();
				result.add(
					nullCompare.isValue() ?
						Restrictions.isNull( nullCompare.getProperty() ) :
						Restrictions.isNotNull( nullCompare.getProperty() )
				);
			} else if ( True.class == element.getDeclaredType() ) {
				result.add( Restrictions.alwaysTrue() );
			} else {
				throw new ModuleSpecificException(
					Errors.INTG_COND,
					" (" + element.getName().toString() + ')'
				);
			}
		}
		return result;
	}
}
