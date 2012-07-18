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
package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.r;

import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;

/**
 * Contains constants with R task properties' names
 * 
 * @author Jiri Tauber
 */
public class Properties extends CommonEvaluatorProperties{

	/**
	 * Name of the property containing the complete R script to run.
	 * Script input file is in csv format called <i>input.rda</i>.
	 * Script is expected to produce csv file called <i>output.rda</i> 
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String R_SCRIPT = "r.script";

	/**
	 * Name of the property which contains comma-separated list of
	 * tags names that need to be loaded from the Results Repository and saved
	 * to the input file. If files are to be processed as script input
	 * then those files will be saved in the working directory of the script
	 * and only their names will be included in the input file. 
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String SOURCE_TAGS = "source.tags";

	/**
	 * Name of the property which contains destination tags.
	 * <br><br>
	 * This parameter is <b>required</b> if {@value #DESTINATION_DATASET}
	 * property is given.
	 * 
	 * @see #SOURCE_TAGS SOURCE_TAGS
	 */
	public static final String DESTINATION_TAGS = "destination.tags";

	/**
	 * Name of the property which contains the destination dataset name.
	 * Data will not be saved back to the repository if this parameter
	 * is omitted. In that case the R script is expected to generate result
	 * files to some properietary permanent storage (i.e. Apache web root).
	 * <br><br>
	 * This parameter is <b>optional</b>
	 */
	public static final String DESTINATION_DATASET = "destination.dataset";

	/**
	 * Name of the property which contains separator character for the
	 * created input and output csv files.
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: ;
	 */
	public static final String SEPARATOR_CHAR = "separator.char";
	/**
	 * Name of the property which contains quote character for the
	 * created input and output csv files.
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: "
	 */
	public static final String QUOTE_CHAR = "quote.char";

	/**
	 * Name of the property which says whether the task should read even
	 * the old data from the Results Repository. Data are considered old
	 * if their serial number is lower than or equal to
	 * last.serial.processed<br><br>
	 * This parameter is <b>optional</b>, default is: false
	 */
	public static final String DO_PROCESS_OLD = "do.process.old";

	/**
	 * Name of the property which contains <b>object</b> representation
	 * of the dataset condition to check out from the Results Repository.
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: {@code AlwaysTrueCondition()}
	 */
	public static final String CONDITION = "condition";

}
