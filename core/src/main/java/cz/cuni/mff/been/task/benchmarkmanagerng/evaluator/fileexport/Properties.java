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
package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.fileexport;

import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;

/**
 * Contains constants with FileExport task properties' names
 * 
 * @author Jiri Tauber
 */
public class Properties extends CommonEvaluatorProperties{

	/**
	 * Name of the property which contains the file tag name.
	 * <br><br>
	 * <b>Example file name:</b><br>
	 * {@code c:\webroot\?analysis.name?\?suite?\?revision?\?build?\?run?.data}<br>
	 * might produce this file: {@code c:\webroot\xampler\ping\123\1\1.data}
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String FILE_TAG = "file.tag";

	/**
	 * Name of the property which contains destination path and file name.
	 * The path may contain tag names which will be replaced by file's tag
	 * values. The tag names to be replaced are specified as {@code ?tag_name?}.
	 * There are two special variables {@code ?analysis.name?} and
	 * {@code ?dataset.name?} which will be replaced by respective parameters.
	 * <br><br>
	 * <b>Example file name:</b><br>
	 * {@code c:\webroot\?analysis.name?\?suite?\?revision?\?build?\?run?.data}<br>
	 * might produce this file: {@code c:\webroot\xampler\ping\123\1\1.data}
	 * <br><br>
	 * This parameter is <b>required</b>
	 */
	public static final String DESTINATION_FILE = "destination.file";

	/**
	 * Name of the property which contains regular expression searched in path.
	 * The path is searched for this pattern after all the variables are replaced.
	 * Any occurence of this pattern is replaced with regular expression contained
	 * in {@code REPLACEMENT_DEST} property.
	 * <br><br>
	 * <b>Example:</b><br>
	 * file name: {@code c:\webroot\?analysis.name?\?suite?\?revision?\?dateTime?.data}<br>
	 * searched regexp: {@code [ :]+}<br>
	 * might produce this file: {@code c:\webroot\xampler\ping\123\1\2009.12.09.1330.data}
	 * <br><br>
	 * This parameter is <b>optional</b>
	 */
	public static final String REPLACEMENT_SRC = "replace.regex";
	/**
	 * Name of the property which contains regular expression replaced in path.
	 * The path is replaced by regexp after all the variables are replaced.<br><br>
	 * This property contains regular expression and may include back references. 
	 * <br><br>
	 * This parameter is <b>optional</b> default is: {@code ""} (empty string}
	 * @see #REPLACEMENT_SRC
	 * @see String#replaceAll(String, String)
	 */
	public static final String REPLACEMENT_DEST = "replace.with";

	/**
	 * Name of the property which says whether the task should read only
	 * the new data from the Results Repository. Data are considered new
	 * if their serial number is higher than {@code last.serial.processed}
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: false
	 */
	public static final String DO_PROCESS_NEW_ONLY = "do.process.new.only";

	/**
	 * Name of the property which says whether to overwrite existing files.
	 * Task will issue a warning for each file that was (not) overwritten.
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: false
	 */
	public static final String DO_OVERWRITE = "do.overwrite";

	/**
	 * Name of the property which contains <b>object</b> representation
	 * of the dataset condition to check out from the Results Repository.
	 * <br><br>
	 * This parameter is <b>optional</b>, default is: {@code AlwaysTrueCondition()}
	 */
	public static final String PROP_CONDITION = "condition";

}
