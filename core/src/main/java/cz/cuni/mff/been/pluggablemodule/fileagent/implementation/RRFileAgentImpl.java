/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jan Tattermusch
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
package cz.cuni.mff.been.pluggablemodule.fileagent.implementation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.fileagent.FileAgent;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.condition.Conjunction;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * Implementation of file agents that stores files into a dataset in RR.
 * 
 * In this implementation, files are stored into RR file store and they are
 * referenced by UUID identified for RR's dataset.
 * 
 * @author Jan Tattermusch
 * 
 */
public class RRFileAgentImpl implements FileAgent {

	private final String analysisName;
	private final String datasetName;
	private RRDataInterface resultsRepositoryData;
	private RRManagerInterface resultsRepositoryManager;
	private final String dataFieldName;

	/**
	 * Initializes results repository reference
	 * 
	 * @throws RemoteException
	 * @throws PluggableModuleException
	 */
	private void initResultsRepositoryReferences() throws RemoteException,
			PluggableModuleException {
		Object rr = CurrentTaskSingleton
				.getTaskHandle()
				.getTasksPort()
				.serviceFind(
						ResultsRepositoryService.SERVICE_NAME,
						Service.RMI_MAIN_IFACE);
		if (rr == null) {
			throw new PluggableModuleException(
					"Results Repository reference cannot be obtained. Maybe it is not running.");
		}
		resultsRepositoryData = (RRDataInterface) rr;
		resultsRepositoryManager = (RRManagerInterface) rr;
	}

	/**
	 * Constructs new instance of RRFileAgentImple
	 * 
	 * @param analysisName
	 *            name of analysis
	 * @param datasetName
	 * @param dataFieldName
	 * @throws PluggableModuleException
	 */
	public RRFileAgentImpl(String analysisName, String datasetName,
			String dataFieldName) throws PluggableModuleException {
		this.analysisName = analysisName;
		this.datasetName = datasetName;
		this.dataFieldName = dataFieldName;

		try {
			initResultsRepositoryReferences();
		} catch (RemoteException e) {
			throw new PluggableModuleException(
					"Error initializing RR reference.",
					e);
		}
	}

	@Override
	public boolean fileExists(Map<String, Serializable> tags)
			throws IOException {
		try {
			UUID fileId = evaluateCondition(tags);
			return (fileId != null);
		} catch (IOException e) {
			throw new IOException("Error loading file.", e);
		} catch (ResultsRepositoryException e) {
			throw new IOException("Error loading file.", e);
		}
	}

	@Override
	public void loadFile(File file, Map<String, Serializable> tags)
			throws IOException {
		try {
			UUID fileId = evaluateCondition(tags);
			if (fileId == null) {
				throw new IOException(
						"Requested file not found (no record satisfies condition or file id set to null).");
			}
			FileStoreClient client = resultsRepositoryData.getFileStoreClient();
			client.downloadFile(fileId, file);
		} catch (IOException e) {
			throw new IOException("Error loading file.", e);
		} catch (ResultsRepositoryException e) {
			throw new IOException("Error loading file.", e);
		}
	}

	@Override
	public void storeFile(File file, Map<String, Serializable> tags)
			throws IOException {

		DatasetDescriptor dd = null;
		try {
			dd = resultsRepositoryManager.getDatasetDescriptor(
					analysisName,
					datasetName);
		} catch (ResultsRepositoryException e) {
			throw new IOException("Error saving file to Results Repository", e);
		}

		UUID fileId = null;
		if (file != null) {
			FileStoreClient client = resultsRepositoryData.getFileStoreClient();
			fileId = client.uploadFile(file);
		}

		DataHandleTuple data = new DataHandleTuple();
		data.set(dataFieldName, DataHandle.create(DataType.UUID, fileId));

		for (String name : tags.keySet()) {
			DataHandle.DataType type = dd.get(name);
			if (type == null) {
				throw new IOException("Error storing file: Tag named \"" + name
						+ "\" does not exist.");
			}
			Object value = tags.get(name);
			data.set(name, DataHandle.create(type, value));
		}

		try {
			resultsRepositoryData.saveData(analysisName, datasetName, data);
		} catch (ResultsRepositoryException e) {
			throw new IOException("Error saving file to Results Repository", e);
		}

	}

	private UUID evaluateCondition(Map<String, Serializable> tags)
			throws IOException, ResultsRepositoryException {
		Conjunction condition = Restrictions.conjunction();
		for (String tagName : tags.keySet()) {
			condition.add(Restrictions.eq(tagName, tags.get(tagName)));
		}

		List<DataHandleTuple> result = resultsRepositoryData.loadData(
				analysisName,
				datasetName,
				condition,
				null,
				null);

		if (result.size() == 0) {
			return null;
		}

		if (result.size() > 1) {
			throw new IOException(
					"More files satisfy condition (try including all key tags into condition criteria).");
		}

		DataHandleTuple fileRecord = result.get(0);
		UUID fileId = fileRecord.get(dataFieldName).getValue(UUID.class);
		return fileId;
	}

}
