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
package cz.cuni.mff.been.webinterface.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Page.LayoutType;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

/**
 * Results repository NG module
 * 
 * @author Jan Tattermusch
 * @author Jiri Tauber
 */
public class ResultsRepositoryNGModule extends Module {

	/** Class instance (singleton pattern). */
	private static ResultsRepositoryNGModule instance;

	private HashMap< String, Object > data = new HashMap< String, Object >();

	private ServiceReference<RRManagerInterface> resultsRepository;

	/**
	 * Allocates a new <code>DummyConfigModule</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private ResultsRepositoryNGModule() {
		super();
		
		this.resultsRepository = new ServiceReference< RRManagerInterface >(
				new TaskManagerReference(),
				ResultsRepositoryService.SERVICE_NAME,
				Service.RMI_MAIN_IFACE,
				ResultsRepositoryService.SERVICE_HUMAN_NAME
			);

		
		/* Initialize general module info... */
		this.id = "resultsrepositoryng";
		this.name = "Results Repository NG";
		this.defaultAction = "list-analyses";
		
		this.menu = new MenuItem[] {
			new MenuItem("list-analyses", "List Analyses")
		};
	}
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static ResultsRepositoryNGModule getInstance() {
		if (instance == null) {
			 instance = new ResultsRepositoryNGModule();
		}
		return instance;
	}


	/**
	 * Prints out the list of available analyses in the RR
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws ServletException
	 * @throws IOException
	 */
	public void listAnalyses( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException {
		
		page.setTitle("Analyses");

		// load list of analyses
		Collection<String> analyses = null;
		try {
			analyses = resultsRepository.get().getAnalyses();
			data.put("analyses", analyses);

			if( analyses.size() <= 0 ){
				warningMessages.addTextMessage("Couldn't find any analyses!");
			}
		} catch (ResultsRepositoryException e) {
			handleException(e, "Results repository could not return list of analyses.");
		}
		
		page.writeHeader();
		page.writeTemplate( "resultsng-list-analyses", data );
		page.writeFooter();
	}


	/**
	 * Lists datasets for one analysis
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws ServletException
	 * @throws IOException
	 * @throws MissingParamException
	 */
	public void listDatasets( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException, MissingParamException {
		params.ensureExists("analysis");

		page.setTitle("Datasets for analysis "+page.getRequest().getParameter("analysis"));

		// load datasets for chosen analysis
		try {
			String name = page.getRequest().getParameter("analysis");
			data.put("analysis", name);

			List<String> datasets = resultsRepository.get().getDatasets(name);

			HashMap<String, DatasetDescriptor> descriptors = new HashMap<String, DatasetDescriptor>();
			for( String dataset : datasets ){
				DatasetDescriptor desc = resultsRepository.get().getDatasetDescriptor(name, dataset);
				descriptors.put(dataset, desc);
			}

			data.put("descriptors", descriptors );
		} catch (ResultsRepositoryException e) {
			handleException(e, "Results Repository has a problem with your request.");
		}

		page.writeHeader();
		page.writeTemplate("resultsng-list-datasets", data );
		page.writeFooter();
	}

	/**
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws MissingParamException 
	 * @throws IOException 
	 */
	public void deleteDataset( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, MissingParamException, IOException{
		params.ensureExist("analysis", "dataset");

		String analysis = page.getRequest().getParameter("analysis");
		String dataset = page.getRequest().getParameter("dataset");

		try {
			resultsRepository.get().deleteDataset(analysis, dataset);
		} catch (ResultsRepositoryException e) {
			errorMessages.addTextMessage(e.getMessage());
		}

		HashMap<String, String> params = new HashMap<String, String>(2);
		params.put("analysis", analysis);
		page.redirectToAction("listDatasets", params);
	}

	/**
	 * Shows the dataset data in a separate page
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws MissingParamException
	 * @throws ComponentInitializationException
	 */
	public void datasetData( HttpServletRequest request, HttpServletResponse response )
	throws ServletException, IOException, MissingParamException, ComponentInitializationException{
		params.ensureExist("analysis", "dataset");

		page.setTitle("Data for dataset "+page.getRequest().getParameter("dataset")+
				" in analysis "+page.getRequest().getParameter("analysis"));
		page.setLayoutType(LayoutType.SIMPLE);
		page.writeHeader();
		AJAXdatasetData(request, response);
		page.writeFooter();
		page.setLayoutType(LayoutType.NORMAL);
	}

	/**
	 * Prints out a page fragment containing dataset data (no page header or footer)
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws ServletException
	 * @throws IOException
	 * @throws MissingParamException 
	 */
	public synchronized void AJAXdatasetData( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException, MissingParamException {
		params.ensureExist("analysis", "dataset");

		RRManagerInterface rrManager = resultsRepository.get();
		RRDataInterface rrData = (RRDataInterface)resultsRepository.get();
		
		// load datasets for chosen analysis
		data.put("data", null );

		try {
			String analysis = page.getRequest().getParameter("analysis");
			String dataset = page.getRequest().getParameter("dataset");

			List<DataHandleTuple> datasetData = rrData.loadData(analysis, dataset, null, null, null);
			data.put("data", datasetData );

			DatasetDescriptor descriptor = rrManager.getDatasetDescriptor(analysis, dataset);
			ArrayList<String> headers = new ArrayList<String>();
			for(String tagName : descriptor.idTags()) {
				headers.add(tagName);
			}
			for(String tagName : descriptor.dataTags()) {
				headers.add(tagName);
			}
			data.put("headers", headers.toArray(new String[0]) );
			
		} catch (ResultsRepositoryException e) {
			handleException(e, "Results Repository has a problem with your request.");
		}

		page.writeTemplate( "resultsng-dataset-data", data );
	}


	/**
	 * Puts exception message in the error messages and prints the stack trace
	 * to the stderr output.
	 * 
	 * @param e The exception to process.
	 * @param message Message introducing the error - can be {@code null} to
	 * print only the error message
	 */
	private void handleException(Throwable e, String message) {
		if( e.getMessage() != null ){
			errorMessages.addHTMLMessage(
				(message == null ? "" : message+":<br>")
				+e.getMessage());
		} else {
			errorMessages.addHTMLMessage(
					message == null ? "Unknown error" : message);
		}
	}

}
