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
package cz.cuni.mff.been.benchmarkmanagerng;

/**
 * Exception thrown in case there is error in Analysis object
 * like: object is null, analysis is not valid, etc. 
 * @author Jiri Tauber
 */
public class AnalysisException extends BenchmarkManagerException {

	private static final long serialVersionUID = 1724141193378550880L;

	/**
	 * 
	 */
	public AnalysisException() {
		super();
	}

	/**
	 * @param message
	 */
	public AnalysisException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AnalysisException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AnalysisException(String message, Throwable cause) {
		super(message, cause);
	}

}
