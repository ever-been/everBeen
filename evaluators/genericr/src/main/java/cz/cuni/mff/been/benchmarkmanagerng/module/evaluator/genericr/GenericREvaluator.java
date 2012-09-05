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

package cz.cuni.mff.been.benchmarkmanagerng.module.evaluator.genericr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
/**
 * This evaluator simply adds r-evaluator=task trigger to the source dataset.
 * If provided, it also creates a new dataset for storing the task's results.
 * The R task exports data from source dataset into a CSV file, executes R script
 * on them and may save them back to the destination dataset.
 * 
 * @author Jiri Tauber
 */
public class GenericREvaluator extends EvaluatorPluggableModule {

	public static String EVALUATOR_NAME = "Generic_R_Evaluator";
	public static String EVALUATOR_TASK_NAME = "r-evaluator-task";

	// Configuration fields
	/** The name of the source dataset */
	public static String CONFIG_SOURCE_DATASET = "sourceDatasetName";
	/** The evaluated tags in the source dataset (ordered list expected) */ 
	public static String CONFIG_SOURCE_TAGS = "sourceTags";
	/** boolean value telling whether the task should process only new values (default) */
	public static String CONFIG_PROCESS_OLD = "processOld";
	/** Where the tasks stores results - may be empty */  
	public static String CONFIG_DESTINATION_DATASET = "destinationDatasetName";
	/** The destination tags - whitespace-separated list of key=type or key:type definitions
	 * - must be empty if no destination dataset was given */
	public static String CONFIG_DESTINATION_TAGS = "destinationTags";
	/** Comma-separated list of the destination tags which are keys */
	public static String CONFIG_DESTINATION_KEYS = "destinationKeys";
	/** The script */
	public static String CONFIG_R_SCRIPT = "evaluationScript";

	/** CSV separator character */
	public static String CONFIG_SEPARATOR_CHAR = "separatorChar";
	/** CSV quote character */
	public static String CONFIG_QUOTE_CHAR = "quoteChar";
	/** The host where the evaluator should run */
	public static String CONFIG_HOST_RSL = "hostRSL";

	/** Regular expression for checking comma-separated list of tags in CONFIG_SOURCE_TAGS and CONFIG_DESTINATION_KEYS */
	public static String REGEXP_TAG_LIST = "^"+REGEXP_JAVA_IDENTIFIER+"(,"+REGEXP_JAVA_IDENTIFIER+")*$";


	public GenericREvaluator(PluggableModuleManager manager) {
		super(manager);
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doValidateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();
		String field;
		String parameter;

		// ----- Source ----- //
		field = CONFIG_SOURCE_DATASET;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		} else if( !parameter.matches("^"+REGEXP_JAVA_IDENTIFIER+"$") ) {
			errors.add("Configuration parameter '"+field+"' is not a java identifier;");
		}

		field = CONFIG_SOURCE_TAGS;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		} else if( !parameter.matches(REGEXP_TAG_LIST) ){
			errors.add("Configuration parameter '"+field+"' is not a comma separated list of java identifiers;");
		}


		// ----- Destination ----- //
		field = CONFIG_DESTINATION_DATASET;
		parameter = configuration.get(field,0);
		if( parameter != null && !parameter.isEmpty()){
			if( !configuration.get(field,0).matches(REGEXP_JAVA_IDENTIFIER) ) {
				errors.add("Configuration parameter '"+field+"' is not a java identifier;");
			}

			List<String> tagErrors = null;
			field = CONFIG_DESTINATION_TAGS;
			parameter = configuration.get(field,0);
			if( parameter == null || parameter.isEmpty() ){
				errors.add("Configuration parameter '"+field+"' is missing;");
			} else {
				tagErrors = validateDestinationTags( field, parameter);
				errors.addAll(tagErrors);
			}

			field = CONFIG_DESTINATION_KEYS;
			parameter = configuration.get(field,0);
			if( parameter != null && !parameter.isEmpty() ){
				if( !parameter.matches(REGEXP_TAG_LIST) ){
					errors.add("Configuration parameter '"+field+"' is not a comma separated list of java identifiers;");
				} else if( tagErrors != null && tagErrors.isEmpty() ){
					// tag definitions are OK
					String tags = configuration.get(CONFIG_DESTINATION_TAGS,0);
					for (String string : parameter.split("")) {
						if( !tags.contains(string)){
							errors.add("Key tag '"+string+"' is not listed in tag descriptions;");
						}
					}
				}
			}

		} else {
			field = CONFIG_DESTINATION_TAGS;
			parameter = configuration.get(field,0);
			if( parameter != null && !parameter.isEmpty() ){
				errors.add("Configuration parameter '"+field+"' should be empty;");
			}
			field = CONFIG_DESTINATION_KEYS;
			parameter = configuration.get(field,0);
			if( parameter != null && !parameter.isEmpty() ){
				errors.add("Configuration parameter '"+field+"' should be empty;");
			}
		}


		// ----- Evaluation Script ----- //
		field = CONFIG_R_SCRIPT;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		}


		// ----- General Properties ----- //
		field = CONFIG_SEPARATOR_CHAR;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		} else if( parameter.length() != 1 ){
			errors.add("Configuration parameter '"+field+"' is not a single character;");			
		}

		field = CONFIG_QUOTE_CHAR;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		} else if( parameter.length() != 1 ){
			errors.add("Configuration parameter '"+field+"' is not a single character;");
		}

		field = CONFIG_HOST_RSL;
		parameter = configuration.get(field,0);
		if( parameter == null || parameter.isEmpty() ){
			errors.add("Configuration parameter '"+field+"' is missing;");
		} else {
			try {
				// ParserWrapper is in the been core, no need to pack it to the pluggable module package 
				ParserWrapper.parseString(parameter);
			} catch (ParseException e) {
				errors.add("Configuration parameter '"+field+"' is not a valid RSL expression;");
			}
		}

		return errors;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateDatasets(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets(Configuration configuration) throws RemoteException, ResultsRepositoryException {
		Map<String, DatasetDescriptor> datasets = new HashMap<String, DatasetDescriptor>();
		String datasetName = configuration.get(CONFIG_DESTINATION_DATASET, 0); 

		DatasetDescriptor myDesc = parseDestinationTags(
				configuration.get(CONFIG_DESTINATION_TAGS, 0),
				configuration.get(CONFIG_DESTINATION_KEYS, 0));

		DatasetDescriptor desc = null;
		try { 
			desc = getRRManagerInterface()
					.getDatasetDescriptor(getAnalysis().getName(), datasetName);
		} catch (ResultsRepositoryException e) {
			// no problem, just didn't find the descriptor (I hope)
		}

		if( desc == null ){
			// ok, dataset doesn't exist - create it
			datasets.put(datasetName, myDesc);
		} else {
			// dataset does exist - check if it has the expected fields
			StringBuilder errors = new StringBuilder();
			DataType myTagType;
			for (String myTagName : myDesc.tags()) {
				myTagType = myDesc.get(myTagName);
				if( !myTagType.equals(desc.get(myTagName)) ){
					// something smells fishy
					errors.append("Tag '"+myTagName+"' has invalid type. '"+myTagType+"' given but '"+desc.get(myTagName)+"' expected;");
				}
			}
			if( errors.length() > 0 ){
				throw new ResultsRepositoryException(errors.toString());
			}
		}

		// TODO Auto-generated method stub
		return datasets;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateTriggers()
	 */
	@Override
	protected List<RRTrigger> doCreateTriggers() {
		List<RRTrigger> triggers = new ArrayList<RRTrigger>();

		triggers.add( new RRTrigger(
				getAnalysis().getName(),
				getConfiguration().get(CONFIG_SOURCE_DATASET,0),
				EVALUATOR_NAME,
				new AlwaysTrueCondition(),
				getTaskDescriptor()
		));

		return triggers;
	}


	//----- Private Methods --------------------------------------------------//
	/**
	 * Validates whether the destination tags have proper format.
	 * 
	 * @param inputName the name of the input field for error reporting
	 * @param rawInput The dataset description (value of the input field)
	 * @return List of errors in the destination tag definition
	 */
	private List<String> validateDestinationTags(String inputName, String rawInput) {
		ArrayList<String> errors = new ArrayList<String>();
		String[] input = rawInput.split("\\s+"); // splits on any whitespace

		StringBuilder regexp = new StringBuilder();
		regexp.append("^");
		regexp.append(REGEXP_JAVA_IDENTIFIER);
		regexp.append(":(");
		for (DataType type : DataType.values()) {
			regexp.append(type.name());
			regexp.append("|");
		}
		regexp.deleteCharAt(regexp.length()-1);
		regexp.append(")$");
		// At this point the regexp looks something like this:
		// "^[a-zA-Z_][a-zA-Z0-9_]*:(INT|LONG|FLOAT|DOUBLE|STRING|UUID|SMALL_BINARY|FILE|SERIALIZABLE)$"

		int i = 1;
		for (String row : input) {
			if( !row.matches(regexp.toString()) ){
				errors.add("Configuration parameter '"+inputName+"' has invalid format at line"+i+": '"+row+"';");
			}
			i++;
		}
		return errors;
	}


	/**
	 * Creates a dataset descriptor according to parameters.
	 * This is then can be used to store data.
	 * @param tags the whitespace-separated (\n preffered) list of dataset tags.
	 * 				each tak is in the form {@code name:type} or {@code name=type}.
	 * 				Type must be one of the DatasetType options.
	 * @param keys the comma-separated list of key tags in the dataset
	 * @return the destination DatasetDescriptor
	 */
	private DatasetDescriptor parseDestinationTags(String tags, String keys) {
		DatasetDescriptor result = new DatasetDescriptor();
		HashMap<String, Boolean> keyTable = new HashMap<String, Boolean>();
		for (String keyTag : keys.split(",")) {
			keyTable.put(keyTag, true);
		}

		String tagName;
		DataType tagType;
		String[] pair;
		for (String row : tags.split("\\s+")) {
			pair = row.split("[:=]");
			tagName = pair[0];
			tagType = DataType.valueOf(DataType.class, pair[1]);
			result.put(tagName, tagType, keyTable.containsKey(tagName));
		}

		return result;
	}


	/**
	 * @return The complete R-task task descriptor with all the properties set 
	 */
	private TaskDescriptor getTaskDescriptor() {
		TaskDescriptor descriptor = TaskDescriptorHelper.createTask(
				"R-Evaluator-"+getAnalysis().getName(),
				EVALUATOR_TASK_NAME,
				getAnalysis().getEvaluatorContext(),
				Analysis.DEFAULT_HOST_RSL,
				"/"+getAnalysis().getName()+"/evaluator/generic-r/%u"  // random UUID at the end
		);

		// shortcut
		Configuration config = getConfiguration();
		
		TaskDescriptorHelper.addTaskProperties(
			descriptor,
			Pair.pair(Properties.ANALYSIS_NAME, getAnalysis().getName()),
			Pair.pair(Properties.DATASET_NAME, config.get(CONFIG_SOURCE_DATASET, 0)),
			Pair.pair(Properties.SOURCE_TAGS, config.get(CONFIG_SOURCE_TAGS, 0)),
			Pair.pair(Properties.DO_PROCESS_OLD, config.get(CONFIG_PROCESS_OLD, 0)),
			
			Pair.pair(Properties.R_SCRIPT,config.get(CONFIG_R_SCRIPT, 0)),
			
			Pair.pair(Properties.SEPARATOR_CHAR, config.get(CONFIG_SEPARATOR_CHAR, 0)),
			Pair.pair(Properties.QUOTE_CHAR, config.get(CONFIG_QUOTE_CHAR, 0))
		);

		String dataset = config.get(CONFIG_DESTINATION_DATASET, 0);
		if( dataset != null && !dataset.isEmpty() ){
			TaskDescriptorHelper.addTaskProperties(
				descriptor,
				Pair.pair(Properties.DESTINATION_DATASET, dataset),
				Pair.pair(Properties.DESTINATION_TAGS, config.get(CONFIG_DESTINATION_TAGS,0))
			);
		}

		return descriptor;
	}

}
