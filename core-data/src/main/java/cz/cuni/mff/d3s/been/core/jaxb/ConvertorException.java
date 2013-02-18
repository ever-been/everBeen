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
package cz.cuni.mff.d3s.been.core.jaxb;


/**
 * Thrown by binding parsers when the data parse methods throw an exception. The message and
 * cause of the exception will be extracted from the corresponding transparent internal
 * exception
 * 
 * @author Andrej Podzimek
 */
public final class ConvertorException extends Exception {

	private static final long serialVersionUID = 6033297686719982085L;

	/**
	 * Initializes the convertor exception with a message and a cause. All the data is taken
	 * from the supplied transparent exception which is forgotten and ommitted from the exception
	 * chain.
	 * 
	 * @param cause A transparent exception referencing the cause of this one.
	 */
	public ConvertorException( ConvertorTransparentException cause ) {
		super( cause.getMessage(), cause.getCause() );												// The transparent exc. forgotten.
	}
}
