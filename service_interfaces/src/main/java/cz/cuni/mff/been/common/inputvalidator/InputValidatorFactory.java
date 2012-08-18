/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.common.inputvalidator;

/**
 * Factory class with static methods to instantiate different types of input
 * validators.
 *  
 * @author Jiri Tauber
 *
 */
public class InputValidatorFactory {

	private enum ValidatorType{
		INTEGER {
			@Override
			public InputValidator createValidator(){
				return new IntegerInputValidator(); 
			};
		},
		JAVAIDENTIFIER {
			@Override
			public InputValidator createValidator(){
				return new JavaIdentifierInputValidator();
			}
		},
		NOTEMPTY {
			@Override
			public InputValidator createValidator(){
				return new NotEmptyInputValidator(); 
			};
		};
		public abstract InputValidator createValidator();
	}

	/**
	 * Creates input validator from its name.
	 * For example {@code "integer"} will instantiate {@code ItegerInputValidator}
	 * 
	 * @param name Name of the input validator class
	 * @return Instance of the validator or {@code null} if validator name is invalid
	 */
	public static InputValidator fromString(String name){
		ValidatorType type;
		try{
			type = ValidatorType.valueOf(ValidatorType.class, name.toUpperCase());
		} catch (IllegalArgumentException e){
			return null;
		}
		return type.createValidator();
	}
}
