/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.clinterface.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

import cz.cuni.mff.been.clinterface.ref.ServiceReference;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * Abstract ancestor of all Results Repository adapters, with some common
 * utility methods.
 * 
 * @author Andrej Podzimek
 */
abstract class ResultsRepositoryAbstractAdapter {

	/** A reference to the Results Repository's manager interface. */
	protected final ServiceReference<RRManagerInterface> rrManagerReference;

	/** A reference to the Results Repository's data interface. */
	protected final ServiceReference<RRDataInterface> rrDataReference;

	/**
	 * Initializes the Results Repository reference using the supplied Task
	 * Manager reference.
	 * 
	 * @param taskManagerReference
	 *            A reference to query the Task Manager.
	 */
	protected ResultsRepositoryAbstractAdapter(
			TaskManagerReference taskManagerReference) {
		this.rrManagerReference = new ServiceReference<RRManagerInterface>(
				taskManagerReference,
				ResultsRepositoryService.SERVICE_NAME, // TODO: One name, 2
														// ifaces???
				ResultsRepositoryService.RMI_MAIN_IFACE,
				ResultsRepositoryService.SERVICE_HUMAN_NAME);
		this.rrDataReference = new ServiceReference<RRDataInterface>(
				taskManagerReference,
				ResultsRepositoryService.SERVICE_NAME, // TODO: One name, 2
														// ifaces???
				ResultsRepositoryService.RMI_MAIN_IFACE,
				ResultsRepositoryService.SERVICE_HUMAN_NAME);
	}

	/**
	 * Analyses and datasets getter.
	 * 
	 * @return A list of analyses and their datasets.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<Pair<String, ? extends Iterable<String>>> getAnalyses()
			throws ResultsRepositoryException, RemoteException,
			ComponentInitializationException {
		RRManagerInterface rrmi;
		ArrayDeque<Pair<String, ? extends Iterable<String>>> result;

		result = new ArrayDeque<Pair<String, ? extends Iterable<String>>>();
		rrmi = rrManagerReference.get();

		for (String analysis : rrmi.getAnalyses()) {
			result.add(Pair.pair(analysis, rrmi.getDatasets(analysis))); // getDatasets()
																			// doesn't
																			// throw!
		}
		return result;
	}

	/**
	 * Analyses and datasets getter with filtering.
	 * 
	 * @param pattern
	 *            The regular expression analysis names must match.
	 * @return A filtered list of analyses and their datasets.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<Pair<String, ? extends Iterable<String>>> getAnalyses(
			Pattern pattern) throws ResultsRepositoryException,
			RemoteException, ComponentInitializationException {
		RRManagerInterface rrmi;
		ArrayDeque<Pair<String, ? extends Iterable<String>>> result;

		result = new ArrayDeque<Pair<String, ? extends Iterable<String>>>();
		rrmi = rrManagerReference.get();

		for (String analysis : rrmi.getAnalyses()) {
			if (pattern.matcher(analysis).matches()) {
				result.add(Pair.pair(analysis, rrmi.getDatasets(analysis))); // getDatasets()
																				// doesn't
																				// throw!
			}
		}
		return result;
	}

	/**
	 * Datasets getter
	 * 
	 * @param analysis
	 *            Name of the analysis.
	 * @return A list of dataset descriptors and dataset names.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<Pair<String, DatasetDescriptor>>
			getDatasets(String analysis) throws RemoteException,
					ResultsRepositoryException,
					ComponentInitializationException {
		RRManagerInterface rrmi;
		ArrayDeque<Pair<String, DatasetDescriptor>> result;

		result = new ArrayDeque<Pair<String, DatasetDescriptor>>();
		rrmi = rrManagerReference.get();

		for (String dataset : rrmi.getDatasets(analysis)) {
			try {
				result.add(Pair.pair(
						dataset,
						rrmi.getDatasetDescriptor(analysis, dataset))); // This
																		// may
																		// throw!
																		// Ugly
																		// API!!!
			} catch (ResultsRepositoryException exception) {
				CurrentTaskSingleton.getTaskHandle().logWarning(
						"Dataset (" + analysis + ", " + dataset + ") is gone.");
			}
		}
		return result;
	}

	/**
	 * Datasets getter with filtering.
	 * 
	 * @param analysis
	 *            Name of the analysis.
	 * @param pattern
	 *            The regular expression dataset names must match.
	 * @return A filtered list of dataset descriptors and dataset names.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<Pair<String, DatasetDescriptor>> getDatasets(
			String analysis,
			Pattern pattern) throws RemoteException,
			ResultsRepositoryException, ComponentInitializationException {
		RRManagerInterface rrmi;
		ArrayDeque<Pair<String, DatasetDescriptor>> result;

		result = new ArrayDeque<Pair<String, DatasetDescriptor>>();
		rrmi = rrManagerReference.get();

		for (String dataset : rrmi.getDatasets(analysis)) {
			if (pattern.matcher(dataset).matches()) {
				try {
					result.add(Pair.pair(
							dataset,
							rrmi.getDatasetDescriptor(analysis, dataset)) // This
																			// may
																			// throw!
																			// Ugly
																			// API!!!
					);
				} catch (ResultsRepositoryException exception) {
					CurrentTaskSingleton.getTaskHandle().logWarning(
							"Dataset (" + analysis + ", " + dataset
									+ ") is gone.");
				}
			}
		}
		return result;
	}

	/**
	 * Deletes the required dataset from Results Repository's database.
	 * 
	 * @param analysis
	 *            Name of the analysis to which the dataset belongs.
	 * @param dataset
	 *            Name of the dataset.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public void deleteDataset(String analysis, String dataset)
			throws ResultsRepositoryException, RemoteException,
			ComponentInitializationException {
		rrManagerReference.get().deleteDataset(analysis, dataset);
	}

	/**
	 * Triggers list getter.
	 * 
	 * @param analysis
	 *            Name of the analysis.
	 * @param dataset
	 *            Name of the dataset.
	 * @return A list of triggers bound to the analysis and dataset.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<RRTrigger> getTriggers(String analysis, String dataset)
			throws ResultsRepositoryException, RemoteException,
			ComponentInitializationException {
		return rrManagerReference.get().getTriggers(analysis, dataset);
	}

	/**
	 * Filtered trigers list getter.
	 * 
	 * @param analysis
	 *            Name of the analysis.
	 * @param dataset
	 *            Name of the dataset.
	 * @param pattern
	 *            The regular experssion evaluator names must match.
	 * @return A filtered list of triggers bound to the analysis and dataset.
	 * @throws ResultsRepositoryException
	 *             when something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public Iterable<RRTrigger> getTriggers(
			String analysis,
			String dataset,
			Pattern pattern) throws ResultsRepositoryException,
			RemoteException, ComponentInitializationException {
		ArrayDeque<RRTrigger> result;

		result = new ArrayDeque<RRTrigger>();
		for (RRTrigger trigger : rrManagerReference.get().getTriggers(
				analysis,
				dataset)) {
			if (pattern.matcher(trigger.getEvaluator()).matches()) {
				result.add(trigger);
			}
		}
		return result;
	}

	/**
	 * Deletes one trigger by its UUID.
	 * 
	 * @param triggerID
	 *            UUID of the trigger to delete.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws NoSuchElementException
	 *             When no such trigger is found.
	 */
	public void deleteTrigger(UUID triggerID)
			throws ResultsRepositoryException, RemoteException,
			ComponentInitializationException, NoSuchElementException {
		rrManagerReference.get().deleteTrigger(triggerID);
	}

	/**
	 * Deletes triggers identified by analysis, dataset and evaluator.
	 * 
	 * @param analysis
	 *            Name of the analysis.
	 * @param dataset
	 *            Name of the dataset.
	 * @param evaluator
	 *            Name of the evaluator used by the trigger to delete.
	 * @throws ResultsRepositoryException
	 *             When something bad happens in the Results Repository.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 */
	public void
			deleteTriggers(String analysis, String dataset, String evaluator)
					throws ResultsRepositoryException, RemoteException,
					ComponentInitializationException {
		rrManagerReference.get().deleteTriggers(analysis, dataset, evaluator);
	}

	/**
	 * Downloads a file from the Results Repository and writes its contents into
	 * a stream.
	 * 
	 * @param uuid
	 *            Unique identifier of the file to download.
	 * @param output
	 *            The stream to write the file.
	 * @throws IOException
	 *             When the file upload fails.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 * @throws RemoteException
	 *             When it rains.
	 */
	public void downloadFile(UUID uuid, OutputStream output)
			throws RemoteException, ComponentInitializationException,
			IOException {
		rrDataReference.get().getFileStoreClient().downloadFile(uuid, output);
	}

	/**
	 * Uploads a file into the Results Repository, reading its contents from a
	 * stream.
	 * 
	 * @param input
	 *            The stream to read the file.
	 * @return Unique identifier assigned to the new file.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 * @throws IOException
	 *             When the file upload fails.
	 */
	public UUID uploadFile(InputStream input) throws RemoteException,
			ComponentInitializationException, IOException {
		return rrDataReference.get().getFileStoreClient().uploadFile(input);
	}

	/**
	 * Deletes a file from the Results Repository.
	 * 
	 * @param uuid
	 *            Unique identifier of the file to delete.
	 * @throws RemoteException
	 *             When it rains.
	 * @throws ComponentInitializationException
	 *             When the Service Reference reports a failure.
	 * @throws IOException
	 *             When the file upload fails.
	 */
	public void deleteFile(UUID uuid) throws RemoteException,
			ComponentInitializationException, IOException {
		rrDataReference.get().getFileStoreClient().dismissFile(uuid);
	}

	/**
	 * Drops the underlying Benchmark Manager reference.
	 */
	public void drop() {
		rrManagerReference.drop();
		rrDataReference.drop();
	}
}
