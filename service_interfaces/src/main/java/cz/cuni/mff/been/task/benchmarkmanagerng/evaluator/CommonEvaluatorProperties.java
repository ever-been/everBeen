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
package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator;

/**
 * @author Jiri Tauber
 *
 */
public class CommonEvaluatorProperties {

	/**
	 * Name of property which says what is the last processed data serial number<br>
	 * This one is set by the <b>Results repository</b>
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String LAST_SERIAL_PROCESSED = "last.serial.processed";

	/**
	 * Name of property (id of trigger that triggered evaluator)<br>
	 * This one is set by the <b>Results repository</b>
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String TRIGGER_ID = "trigger.id";

	/**
	 * Name of property which says what is the name of the analysis.<br>
	 * This property must be set by the <b>evaluator pluggable module</b> otherwise
	 * the task won't be able to find the dataset.
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String ANALYSIS_NAME = "analysis.name";

	/**
	 * Name of property which says what is the name of the trigger dataset.<br>
	 * This property must be set by the <b>evaluator pluggable module</b> otherwise
	 * the task won't be able to find the dataset.
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String DATASET_NAME = "dataset.name";

	
}
