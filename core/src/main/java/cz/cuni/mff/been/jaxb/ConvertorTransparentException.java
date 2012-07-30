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
package cz.cuni.mff.been.jaxb;

/**
 * Thrown by data type convertors that are not supposed to throw anything in JAXB. It's just
 * a transparent wrapper for other exceptions.
 * 
 * @author Andrej Podzimek
 */
final class ConvertorTransparentException extends RuntimeException {

	private static final long serialVersionUID = -4110279223830323599L;

	/**
	 * A standard chaining constructor. This exception is a wrapper. Its message should distinguish
	 * the data type or parser implementation that caused it.
	 * 
	 * @param message The message this exception should convey.
	 * @param cause The exception that caused this one.
	 */
	public ConvertorTransparentException( String message, Throwable cause ) {
		super( message, cause );
	}
}
