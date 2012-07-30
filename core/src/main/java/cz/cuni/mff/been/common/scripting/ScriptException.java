/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.common.scripting;

import cz.cuni.mff.been.pluggablemodule.*;

/**
 * Exception for errors occured when executing a script
 *
 * @author Jan Tattermusch
 */
public class ScriptException extends PluggableModuleException {
	
	private static final long serialVersionUID = -101185895414473062L;

	
	public ScriptException() {
		super();
	}
	
	public ScriptException(String message) {
		super(message);
	}
	
	public ScriptException(Throwable cause) {
		super(cause);
	}
	
	public ScriptException(String message, Throwable cause) {
		super(message, cause);
	}
}
