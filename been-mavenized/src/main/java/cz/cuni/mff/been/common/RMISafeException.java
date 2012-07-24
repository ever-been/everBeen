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

package cz.cuni.mff.been.common;

/**
 * This class is a base class for all exceptions thrown in Been that need to be
 * safe for RMI transport over the RMI interface.
 * It never contains any other exceptions so it won't cause ErrorUnmarshallingException
 * when thrown across RMI. However it does contain cause's stack trace which will be
 * written in any {@code printStackTrace()} as if the cause was present.
 * 
 * @author Jiri Tauber
 */
public class RMISafeException extends BeenException {

	private static final long serialVersionUID = -7653594848609300588L;

	private String originalClass = null;

	/**
	 * 
	 */
	public RMISafeException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMISafeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMISafeException(Throwable cause) {
		super();
		saveConvertedCause(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMISafeException(String message, Throwable cause) {
		super(message);
		saveConvertedCause(cause);
	}

	/**
	 * @param cause
	 * @param originalClass
	 */
	public RMISafeException(Throwable cause, String originalClass) {
		this(cause);
		this.originalClass = originalClass;
	}

	/**
	 * @param message
	 * @param cause
	 * @param originalClass
	 */
	public RMISafeException(String message, Throwable cause, String originalClass) {
		this(message, cause);
		this.originalClass = originalClass;
	}

	@Override
	public String toString() {
        String s = (originalClass == null)? getClass().getName() : originalClass;
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
	}

	/**
	 * @param cause
	 */
	private void saveConvertedCause(Throwable cause) {
		if( cause == null ) return;
		RMISafeException safeCause;
		if( cause.getMessage() == null ){
			safeCause = new RMISafeException(cause.getCause(), cause.getClass().toString());
		} else {
			safeCause = new RMISafeException(cause.getMessage(), cause.getCause(), cause.getClass().toString());
		}
		safeCause.setStackTrace(cause.getStackTrace());
		initCause(safeCause);
	}

}
