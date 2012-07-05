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
package cz.cuni.mff.been.webinterface.modules;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.AnalysisState;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerCallbackInterface;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMModule;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.id.SID;
import cz.cuni.mff.been.common.inputvalidator.JavaIdentifierInputValidator;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.jaxb.config.Config;
import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Page.LayoutType;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.webinterface.screen.Checkbox;
import cz.cuni.mff.been.webinterface.screen.IllegalScreenSequenceException;
import cz.cuni.mff.been.webinterface.screen.Input;
import cz.cuni.mff.been.webinterface.screen.Item;
import cz.cuni.mff.been.webinterface.screen.MultiSelect;
import cz.cuni.mff.been.webinterface.screen.Option;
import cz.cuni.mff.been.webinterface.screen.RSLInput;
import cz.cuni.mff.been.webinterface.screen.RadiosWithSections;
import cz.cuni.mff.been.webinterface.screen.Screen;
import cz.cuni.mff.been.webinterface.screen.ScreenHandler;
import cz.cuni.mff.been.webinterface.screen.Section;
import cz.cuni.mff.been.webinterface.screen.Select;
import cz.cuni.mff.been.webinterface.screen.Input.Size;
import cz.cuni.mff.been.webinterface.screen.transcoder.ScreenTranscoder;

/**
 * Web interface module for a BenchmarkManagerNg
 * 
 * @author Jiri Tauber
 */
public class BenchmarksNgModule extends Module {

	private static class WizardMetadata {
		/** Flag telling us whether wizard is editing analysis */
		public boolean editing = false;
		/** The edited analysis object - editing or creating works with this instance */
		public Analysis analysis = null;
		/** Name of the module used for loading {@code lastConfigDescriptor} */
		public BMModule currentModule = null;
		/**
		 * Last screen shown by wizard (assumed to be currently shown)
		 * This field is null only when last(finish) screen was displayed 
		 */
		public Screen currentScreen = null;

		/** Clear this wizard metadata */ 
		public void clear(){
			editing = false;
			analysis = null;
			currentScreen = null;
			currentModule = null;
		}

		/**
		 * Calls {@code clear()} and then initializes this wizard metadata with
		 * the first wizard screen.
		 */
		public void init(Analysis analysis){
			clear();
			if( analysis != null ){
				this.editing = true;
				this.analysis = analysis;
			}
			this.currentScreen = null;
		}

	}

	/** Class instance (singleton pattern). */
	private static BenchmarksNgModule instance;

	/** A reference to the BenchmarkManagerng */
	private final ServiceReference< BenchmarkManagerInterface > benchmarkManager;

	/** Page data */
	private HashMap< String, Object > data = new HashMap< String, Object >();

	/**
	 * Name of the analysis which will be forced to run if "run" button is
	 * pressed on the same one.
	 */
	private String forceAnalysisName = null;

	/** Analysis create / edit wizard metadata */
	private WizardMetadata wizard = new WizardMetadata();

	//***** Singleton Pattern methods ****************************************//
	/**
	 * Allocates a new <code>BenchmarksNgModule</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private BenchmarksNgModule() {
		super();
		
		this.benchmarkManager = new ServiceReference< BenchmarkManagerInterface >(
			new TaskManagerReference(),
			BenchmarkManagerService.SERVICE_NAME,
			Service.RMI_MAIN_IFACE,
			BenchmarkManagerService.SERVICE_HUMAN_NAME
		);

		/* Initialize general module info... */
		this.id = "benchmarksng";
		this.name = "Benchmarks NG";
		this.defaultAction = "list-analyses";
		
		this.menu = new MenuItem[] {
			new MenuItem("list-analyses", "List Analyses"),
			new MenuItem("new-analysis", "Create Analysis")
		};
	}
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static BenchmarksNgModule getInstance() {
		if (instance == null) {
			 instance = new BenchmarksNgModule();
		}
		return instance;
	}


	//***** Module Commands **************************************************//
	/**
	 * Shows form for new analysis and guides the user through
	 * the creation process.
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 */
	public void newAnalysis( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, IOException, ServletException {
		data.clear();

		// reset wizard when starting over
		if( !params.existsOneOf("previous", "next", "finish") ){
			wizard.init(null);
		}

		runWizard();
	}


	/**
	 * Action handles editing of an analysis.
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 */
	public void editAnalysis( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, IOException, ServletException {

		// initialize wizard when starting to edit analysis
		if( params.exists("edit") ){
			String name = page.getRequest().getParameter("name");
			try {
				Analysis analysis = benchmarkManager.get().getAnalysis(name);
				wizard.init(analysis);
				wizard.analysis.setState(AnalysisState.UNKNOWN);
				data.put("contexts", null);
			} catch ( BenchmarkManagerException e ) {
				handleException(e, "Benchmark Manager error");
				page.redirectToAction("list-analyses");
			} catch ( RemoteException e ) {
				handleException(e, "RMI error");
				page.redirectToAction("list-analyses");
			}
		}
		runWizard();
	}


	/**
	 * Lists analyses saved in the BMng and handles their running and deleting
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 */
	public void listAnalyses( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException {

		data.clear();
		page.setTitle( "Analyses view" );

		// Stop Scheduler button pressed
		if( params.exists("stop_scheduler") ){
			try {
				benchmarkManager.get().stopScheduler();
				infoMessages.addTextMessage("Scheduler has been stopped. "
						+"The change might take a minute to take effect. "
						+"The button disapears when it's finished.");
			} catch (RemoteException e) {
				warningMessages.addTextMessage("Can't stop Scheduler: "+e.getMessage());
			}
		}

		// Run analysis button pressed
		if( params.exists("run") && params.exists("name")){
			runAnalysis(page.getRequest().getParameter("name"));
		} else {
			forceAnalysisName = null;
		}

		// Delete analysis button pressed
		if( params.exists("delete") && params.exists("name")){
			String name = page.getRequest().getParameter("name");
			try {
				benchmarkManager.get().deleteAnalysis(name);
				infoMessages.addTextMessage("Analysis '"+name+"' deleted");
			} catch (BenchmarkManagerException e) {
				handleException(e, "Error deleting analysis "+name);
			} catch (RemoteException e) {
				handleException(e, "Error deleting analysis "+name);
			}
		}

		// Load list of analyses for template
		Collection<Analysis> analyses = null;
		boolean schedulerRunning = false;
		try {
			analyses = benchmarkManager.get().getAnalyses();
			schedulerRunning = benchmarkManager.get().isSchedulerRunning();
		} catch ( BenchmarkManagerException e ) {
			handleException(e, "Benchmark Manager error");
			return;
		} catch ( RemoteException e ) {
			handleException(e, "RMI error");
			return;
		}

		// nothing special, just print out the list
		if( analyses.size() <= 0 ){
			warningMessages.addTextMessage("Couldn't find any analyses!");
		}
		data.put("analyses", analyses);
		data.put("schedulerRunning", schedulerRunning);

		page.writeHeader();
		page.writeTemplate( "benchmarksng-list-analyses", data );
		page.writeFooter();
	}


	/**
	 * Shows analysis detail page
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException 
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 * @throws MissingParamException 
	 */
	public void analysisDetail( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException, MissingParamException {
		params.ensureExists("name");
		String name = page.getRequest().getParameter("name");

		HashMap<String, Object> data = new HashMap<String, Object>();
		Analysis analysis = null;
		Map<String, AnalysisState> contexts = new HashMap<String, AnalysisState>();

		// "Run analysis" button pressed
		if( params.exists("run") ){
			runAnalysis(name);
		}

		// "Finish context" button pressed
		if( params.exists("finish_context") && params.exists("context") ){
			String contextId = page.getRequest().getParameter("context");
			try {
				((BenchmarkManagerCallbackInterface)benchmarkManager.get())
						.reportAnalysisFinish(contextId);
			} catch (BenchmarkManagerException e) {
				errorMessages.addTextMessage(
						"Benchmark manager exception: "+e.getMessage());
			}
		}

		try {
			analysis = benchmarkManager.get().getAnalysis(name);
			contexts = benchmarkManager.get().getActiveContexts(name); 
		} catch (AnalysisException e) {
			handleException(e, "Invalid analysis name: "+e.getMessage());
		} catch (BenchmarkManagerException e) {
			handleException(e, "Error in Benchmark Manager: "+e.getMessage());
		} catch (RemoteException e) {
			handleException(e, "Error contacting Benchmark Manager: "+e.getMessage());
		}
		data.put("analysis", analysis);
		data.put("contexts", contexts);

		page.setTitle("Analysis details: "+name);
		page.writeHeader();
		page.writeTemplate("benchmarksng-analysis-detail", data);
		page.writeFooter();

		data.put("contexts", null);
	}


	/**
	 * Action prints out the RSL help
	 * 
	 * @param request
	 * @param response
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 */
	public void rslHelp( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String type = params.exists("type") ? page.getRequest().getParameter("type") : "host";
		data.put("type", type);

		page.setLayoutType(LayoutType.SIMPLE);
		page.setTitle("");
		page.writeHeader();
		page.writeTemplate("rsl-help", data);
		page.writeFooter();
		page.setLayoutType(LayoutType.NORMAL);
	}


	//***** Private Methods **************************************************//
	/**
	 * Tries to run analysis with the specified name. If the Benchmark Manager
	 * throws an {@code AnalysisException} it is assumed that analysis is already
	 * running. In that case {@code analysisName} is saved for future reuse.<br>
	 * If the next call to this function has the same {@code analysisName} the
	 * analysis run is forced. If the next call has different {@code analysisName}
	 * then the last saved name is discarded.
	 * 
	 * @see BenchmarkManagerInterface#runAnalysis(String, boolean)
	 * @param analysisName The name of analysis to run
	 * @throws ComponentInitializationException when BenchmarkManager reference
	 * 		can't be resolved.
	 */
	private void runAnalysis(String analysisName) throws ComponentInitializationException {
		try {
			if( analysisName.equals(forceAnalysisName) ){
				benchmarkManager.get().runAnalysis(analysisName, true);
				forceAnalysisName = null;
			} else {
				forceAnalysisName = null;
				try {
					benchmarkManager.get().runAnalysis(analysisName, false);
				} catch (AnalysisException e) {
					errorMessages.addTextMessage(e.getMessage());
					errorMessages.addTextMessage("Another \"Run\" command will try to force running the analysis.");
					forceAnalysisName = analysisName;
					return;
				}
			}
		} catch ( RemoteException e ) {
			handleException(e, "There is a problem, RMI hates you!");
			return;
		} catch (BenchmarkManagerException e) {
			handleException(e, "Benchmark Manager error");
			return;
		}
		infoMessages.addTextMessage("Running analysis "+analysisName);
		return;
	}


	/**
	 * Transforms screen to configuration
	 * 
	 * @param screen screen to transform
	 * @return resulting configuration object 
	 */
	private Configuration screenToConfiguration(Screen screen) {
		Configuration result = new Configuration();
		for (Section section : screen.getSections()) {
			sectionToConfiguration(section, result);
		}
		return result;
	}


	/**
	 * Saves section fields as configuration fields in given configuration
	 * 
	 * @param section
	 * @param configuration
	 */
	private void sectionToConfiguration(Section section, Configuration configuration){
		for (Item item : section.getItems()){
			if( item instanceof RadiosWithSections ){
				sectionToConfiguration(
						((RadiosWithSections)item).getSelectedItem().getSection(),
						configuration);
			}
			configuration.set(item.getId(), item.getValues());
		}
	}


	//***** Wizard methods ***************************************************//
	/**
	 * Creates the first screen in the "create or edit analysis" wizard.
	 * This screen contains analysis name, description, generator, ...
	 * <br>Does not modify the wizard metadata object.
	 * 
	 * @param analysis - the analysis we are edditing - can be {@code null}
	 * @return the created screen
	 * @throws ComponentInitializationException when BMng reference couldn't be found
	 */
	private Screen getFirstWizardScreen(Analysis analysis) throws ComponentInitializationException{

		// Create default values for simple fields
		String name = analysis != null ? analysis.getName() : "";
		String description = analysis != null ? analysis.getDescription() : "";
		String resultsLink = analysis != null ? analysis.getResultsLink() : "";
		Condition generatorRSL = analysis != null ? analysis.getGeneratorHostRSL() : Analysis.DEFAULT_HOST_RSL;
		boolean repeat = analysis != null ? analysis.getRunPeriod() != null : true;
		String period = analysis != null && analysis.getRunPeriod() != null
							? analysis.getRunPeriod().toString() : "";

		// Load generators and evaluators as options
		String[][] modules = loadBMModules();
		Option[] generators = new Option[modules[0].length];
		for (int i = 0; i < generators.length; i++) {
			generators[i] = new Option(modules[0][i], modules[0][i]);
		}
		int selectedGenerator = 0;

		Option[] evaluators = new Option[modules[1].length];
		for (int i = 0; i < evaluators.length; i++) {
			evaluators[i] = new Option(modules[1][i], modules[1][i]);
		}
		int[] selectedEvaluators = new int[0];

		// Create defaults for generator & evaluator selects
		if( analysis != null ){
			for (int i=0; i < generators.length; i++ ) {
				if( generators[i].getId().equals(analysis.getGenerator().getPackageName()) ){
					selectedGenerator = i;
					break;
				}
			}
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			for (BMEvaluator evaluator : analysis.getEvaluators()) {
				for (int i = 0; i < evaluators.length; i++) {
					if( evaluators[i].getId().equals(evaluator.getPackageName()) ){
						tmp.add(i);
					}
				}
			}
			selectedEvaluators = new int[tmp.size()];
			for (int i = 0; i < selectedEvaluators.length; i++) {
				selectedEvaluators[i] = tmp.get(i);
			}
		}

		// Create the screen
		Section[] sections = new Section[]{
			new Section(new Item[]{
					new Input("name", "Analysis name", name, Size.BIG, new JavaIdentifierInputValidator()),
					new Input("description", "Description", description, Size.AREA, null),
					new Input("results_link", "Link to results web", resultsLink, Size.BIG, null)
				},
				"Analysis parameters",
				null),
			new Section( new Item[]{
					new Select("generator", "Generator", generators, selectedGenerator),
					new RSLInput("generator_rsl", "Generator host", generatorRSL.toString()),
				},
				"Generator settings",
				null),
			new Section(new Item[]{
					new MultiSelect("evaluators", "Evaluators", evaluators, true, selectedEvaluators),
				},
				"Evaluators",
				null),
			new Section(new Item[]{
					new Checkbox("periodical", "Run periodically", repeat),
					new Input("period", "Period (minutes)", period, Size.SMALL, null),
				},
				"Scheduling options",
				null)
		};
		Screen result = new Screen(new SID(0), sections);
		result.setButtons(false, true, true, wizard.editing);
		return result;
	}


	/**
	 * Performs wizard screen transition.<ul>
	 * <li>Discards current screen when <i>cancel</i> or <i>previous</i> button
	 * is pressed. Loads previous or first screen respectively.</li>
	 * <li>Checks current screen validity when <i>next</i> or <i>finish</i>
	 * button is pressed. Reports errors if there are any on the current screen.
	 * Loads the next screen if the current screen is valid and <i>next</i>
	 * button is pressed.</li>
	 * </ul>
	 * <br>This method <b>modifies</b> the {@code wizard} metadata field.
	 * @throws ComponentInitializationException 
	 * @throws IOException when there is an error loading the servlet file
	 * @throws ServletException when there is an error including the servlet file
	 */
	private void runWizard() throws ComponentInitializationException, IOException, ServletException {

		if( params.exists("finish") && page.getRequest().getParameter("sid").equals("finish") ){
		// ----- create the analysis when finish on the last page was pressed ----- //
			try {
				if( wizard.editing ){
					benchmarkManager.get().updateAnalysis(wizard.analysis);
				} else {
					benchmarkManager.get().createAnalysis(wizard.analysis);
				}
				infoMessages.addTextMessage("Analysis saved successfully");
				page.redirectToAction("list-analyses");
				return;
			} catch (RemoteException e) {
				handleException(e, "RMI error occured");
			} catch (BenchmarkManagerException e) {
				handleException(e, "RMI error occured");
			}

		} else if( params.exists("cancel") ){
		// ----- Start the real wizard processing ----- //

			// Discard current metadata and start over
			wizard.clear();
			wizard.currentScreen = getFirstWizardScreen(null);

		} else if( params.exists("previous") ){
		// ----- Go to the previous page ----- //

			int sid = 0;
			if( wizard.currentScreen == null ){
				sid = wizard.analysis.getEvaluators().size()+2;
			} else {
				sid = ((Long)wizard.currentScreen.getSid().value()).intValue();
			}
			loadWizardScreen(sid-1);

		} else if( params.existsOneOf("next", "finish") ){
		// ----- Go to the next (or last) page ----- //
			Screen screen = wizard.currentScreen;
			assert screen != null : "User has probably changed the template";

			// Validate the current screen input syntax
			validateAndUpdateScreen(screen);
			if( errorMessages.isEmpty() ){
				// Screen input syntax is OK, save and validate the semantics
				if( screen.getSid().equals(new SID(0)) ){
					updateAnalysis(screen);
				} else {
					// Validate the configuration
					validateAndSetConfiguration(screen, wizard.currentModule);
				}

				if( errorMessages.isEmpty() ){ // it might have changed in the block above
					// Decide which page to display next
					if( params.exists("next") ){
						int sid = ((Long)wizard.currentScreen.getSid().value()).intValue();
						loadWizardScreen(sid +1);

					} else { // params.exists("finish")
						if( !wizard.analysis.isComplete() ){
							errorMessages.addTextMessage("Analysis configuration is not complete!");
							assert wizard.analysis.getGenerator().getConfiguration() != null : "impossible happens sometimes";
							int sid = 2;
							for( BMEvaluator ev : wizard.analysis.getEvaluators() ){
								if( ev.getConfiguration() == null ){
									loadWizardScreen(sid);
									break;
								}
								sid++;
							}
						} else {
							wizard.currentModule = null;
							wizard.currentScreen = null;
						}
					}
				}
			}

		} else {
			loadWizardScreen(0);
		}

		// ----- Write out the screen ----- //
		String template;
		if( wizard.currentScreen != null ){
			data.put("screen", wizard.currentScreen);
			template = "screen";
			if( wizard.currentScreen.getSections().length == 0 ){
				infoMessages.addTextMessage("This module doesn't have any configuration");
			}
			if( wizard.editing && wizard.currentModule != null ){
				warningMessages.addTextMessage("Editing modules may corrupt the analysis !!!");
			}
		} else {
			page.setTitle( wizard.analysis.getName() );
			warningMessages.addTextMessage(
					"Please review your settings and hit finish to save the analysis");
			data.put("analysis", wizard.analysis);
			template = "benchmarksng-analysis-finish";
		}

		page.writeHeader();
		page.writeTemplate( template, data );
		page.writeFooter();

	}


	/**
	 * @param sid - the ordinal number of the screen which should be created<br>
	 * 0 = The first screen (create analysis)<br>
	 * 1 = Generator module configuration screen<br>
	 * 2-n = Evaluator configuration screens
	 * @throws ComponentInitializationException 
	 */
	private void loadWizardScreen(int sid) throws ComponentInitializationException {
		if( sid == 0 ){
			// Load the start page
			wizard.currentModule = null;
			wizard.currentScreen = getFirstWizardScreen(wizard.analysis);
			page.setTitle( (wizard.editing ? "Edit" : "Create new")+" analysis" );
			return;

		} else if( sid == 1 ){
			// Load generator
			wizard.currentModule = wizard.analysis.getGenerator();
		} else {
			// Load evaluator (or null)
			BMEvaluator[] evaluators = wizard.analysis.getEvaluators().toArray(new BMEvaluator[0]);
			if( sid < evaluators.length + 2 ){ // screen 2=> evaluator[0]
				wizard.currentModule = evaluators[sid-2];
			} else {
				wizard.currentModule = null;
			}
		}

		if( wizard.currentModule == null ){
			wizard.currentScreen = null;
			page.setTitle( (wizard.editing ? "Edit" : "Create new")+" analysis" );
			return;
		}

		// create the next screen
		Config config;
		try {
			config = benchmarkManager.get().
					getConfigurationDescription(wizard.currentModule);
		} catch (BenchmarkManagerException e) {
			handleException(e, "BenchmarkManagerng failure");
			return;
		} catch (RemoteException e) {
			handleException(e, null);
			return;
		}
		wizard.currentScreen = ScreenTranscoder.fromJaxbConfig(
				new SID(sid),
				config,
				wizard.currentModule.getConfiguration());
		wizard.currentScreen.setButtons(true, true, true, wizard.editing);
		page.setTitle( wizard.currentModule.getName()+" configuration" );
	}


	/**
	 * Validates request data with screen information.
	 * Sets errorMessages if there are errors.
	 * @param screen
	 */
	private void validateAndUpdateScreen(Screen screen) {
		try {
			ScreenHandler screenHandler = new ScreenHandler(page.getRequest());
			screenHandler.ensure(screen); //throws exception when req. fields are missing or invalid
			screenHandler.check(screen);  //sets error messages if there are any errors
			screenHandler.updateItems(screen); //sets screen values to request values for reuse
		} catch (MissingParamException e) {
			handleException(e, "The form is missing required prameter(s)");
		} catch (InvalidParamValueException e) {
			handleException(e, "The form's required parameter(s) have invalid value");
		} catch (IllegalScreenSequenceException e) {
			handleException(e, "Please don't use the browser controls in wizard");
		}
	}


	/**
	 * Converts screen data to {@link Configuration} and saves it into the
	 * {@code module}. The module is then validated using Benchmark Manager. 
	 * 
	 * @param screen - screen to process
	 * @param module - module to save the config to
	 * @throws ComponentInitializationException when BenchmarkManagerRefference can't be resolved
	 */
	private void validateAndSetConfiguration(
			Screen screen, BMModule module)
	throws ComponentInitializationException {

		try {
			Configuration cfg = screenToConfiguration(screen);
			module.setConfiguration(cfg);
			Collection<String> errors = benchmarkManager.get().validateModuleConfiguration(module);
			for (String text : errors) {
				errorMessages.addTextMessage(text);
			}
		} catch (RemoteException e) {
			handleException(e, null);
		}
	}


	/**
	 * Loads list of Evaluators and Generators from the BM and saves their
	 * string representation to the result array.
	 * Also prints out warning messages if no modules were found.
	 *
	 * @throws ComponentInitializationException
	 * @return Array with 2 fields:<br>
	 * 0 -> List of generators<br>
	 * 1 -> List of evaluators
	 */
	private String[][] loadBMModules() throws ComponentInitializationException{
		BenchmarkManagerInterface bmNg = benchmarkManager.get();
		String[][] result = new String[2][0];
		try {

			Collection<BMGenerator> generators = bmNg.getGenerators();
			if( generators.size() <= 0 ){
				warningMessages.addTextMessage("Couldn't find any generators in the SW repository!");
			} else {
				result[0] = new String[generators.size()];
				int i = 0;
				for ( BMGenerator g : generators) {
					result[0][i] = g.getPackageName();
					i++;
				}
			}

			Collection<BMEvaluator> evaluators = bmNg.getEvaluators();
			if( evaluators.size() <= 0 ){
				warningMessages.addTextMessage("Couldn't find any evaluators in the SW repository!");
			} else {
				result[1] = new String[evaluators.size()];
				int i = 0;
				for ( BMEvaluator e : evaluators) {
					result[1][i] = e.getName()+"-"+e.getVersion();
					i++;
				}
			}

		} catch ( BenchmarkManagerException e ) {
			errorMessages.addTextMessage(
					"Couldn't load list of modules because error ocured: "+e.getMessage());
		} catch (RemoteException e) {
			errorMessages.addTextMessage(
					"Couldn't load list of modules because error ocured: "+e.getMessage());
		}

		return result;
	}


	/**
	 * Checks the validity of the first wizard screen and saves the data to
	 * the {@code analysis} object.
	 *  
	 * @param screen Screen with source data 
	 */
	private void updateAnalysis(Screen screen) {

		// list of mandatory fields in order how they apear on the page
		boolean[] mandatoryField = new boolean[]{
				true, false, false, true, true, false, true, false };
		int i = 0;
		// check if all mandatory fields are set
		for (Section s : screen.getSections()) {
			for (Item item : s.getItems()) {
				if( i >= mandatoryField.length ) break;
				if( mandatoryField[i] && item.getValue().isEmpty() ){
					errorMessages.addTextMessage(
							item.getLabel()+" must not be empty");
				}
				i++;
			}
		}
		Section[] sections = screen.getSections();
		// Scheduling time must not be empty when checkbox is checked:
		if( Checkbox.VALUE_CHECKED.equals(sections[3].getItems()[0].getValue()) &&
				sections[3].getItems()[1].getValue().isEmpty() ){
			errorMessages.addTextMessage(
					sections[3].getItems()[1].getLabel()+" must not be empty");
		}

		Analysis analysis = wizard.analysis;

		// Gather values: name, description, generator
		String name = sections[0].getItems()[0].getValue();
		String description = sections[0].getItems()[1].getValue();
		String generatorPackage = sections[1].getItems()[0].getValue();
		BMGenerator generator;
		if( analysis != null ){
			generator = analysis.getGenerator();
		} else {
			generator = createGenerator(generatorPackage);
		}

		// create new analysis if necessary (none created or name change)
		if( analysis == null || (wizard.editing && !analysis.getName().equals(name)) ){
			try {
				wizard.analysis = new Analysis( name, description, generator );
			} catch (AnalysisException e) {
				errorMessages.addTextMessage(e.getMessage());
				return;
			}
			analysis = wizard.analysis;
		}
		String resultsLink = sections[0].getItems()[2].getValue();
		analysis.setResultsLink(resultsLink);

		// Change detection (only when editing analysis):
		if( wizard.editing && analysis.getID() != null ){
			if( !analysis.getName().equals(name) ){
				errorMessages.addTextMessage("Can't change analysis name");
			}
			if( !analysis.getGenerator().getPackageName().equals(generatorPackage) ){
				errorMessages.addTextMessage("Can't change analysis generator");
			}
		}

		Condition generatorHost = null;
		try {
			generatorHost = ParserWrapper.parseString(sections[1].getItems()[1].getValue());
		} catch (ParseException exception) {
			errorMessages.addTextMessage("Can't parse RSL: " + exception.getMessage());
		}

		if( !errorMessages.isEmpty() ) return;


		// Execute the changes:
		if( !analysis.getDescription().equals(description) ){
			analysis.setDescription(description);
		}

		if( !analysis.getGenerator().isSimilarTo(generator) ){
			analysis.setGenerator(generator);
		}

		if( !analysis.getGeneratorHostRSL().equals(generatorHost) ){
			analysis.setGeneratorHostRSL(generatorHost);
		}

		// Update the evaluator list - a bit tricky operation here.
		if( analysis.getEvaluators().isEmpty() ){
			for( String str : sections[2].getItems()[0].getValues() ){
				analysis.addEvaluator(createEvaluator(str));
			}
		} else {
			String[] selected = sections[2].getItems()[0].getValues();
			BMEvaluator[] newEvals = new BMEvaluator[selected.length];
			for (int j = 0; j < selected.length; j++) {
				String str = selected[j];
				// try to find existing evaluator
				for (BMEvaluator evaluator : analysis.getEvaluators()) {
					if(str.equals(evaluator.getPackageName())){
						newEvals[j] = evaluator;
						analysis.removeEvaluator(evaluator);
						break;
					}
				}
				// create new evaluator if existing wasn't found
				if( newEvals[j] == null ){
					newEvals[j] = createEvaluator(str);
				}
			}
			analysis.removeEvaluators();
			for (BMEvaluator evaluator : newEvals) {
				analysis.addEvaluator(evaluator);
			}
		}

		// Update the scheduling options
		if( Checkbox.VALUE_CHECKED.equals(sections[3].getItems()[0].getValue()) ){
			analysis.setRunPeriod(Integer.decode(sections[3].getItems()[1].getValue()));
		} else {
			analysis.setRunPeriod(null); // scheduling disaled
		}
	}


	//***** Utility methods **************************************************//
	/**
	 * Creates an instance of BMGenerator from given input string.
	 * The input string should be in form <code>moduleName-moduleVersion</code>
	 * 
	 * @param input String directly from the form
	 * @return the instance
	 */
	private BMGenerator createGenerator(String input){
		Integer dash = input.lastIndexOf('-');
		String moduleName = input.substring(0, dash);
		String moduleVersion = input.substring(dash+1, input.length());
		return new BMGenerator( moduleName, moduleVersion );
	}


	/**
	 * Creates an instance of BMEvaluator from given input string.
	 * The input string should be in form <code>moduleName-moduleVersion</code>
	 * 
	 * @param input String directly from the form
	 * @return the instance
	 */
	private BMEvaluator createEvaluator(String input){
		Integer dash = input.lastIndexOf('-');
		String moduleName = input.substring(0, dash);
		String moduleVersion = input.substring(dash+1, input.length());
		return new BMEvaluator( moduleName, moduleVersion );
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
					(message == null ? "" : message+":<br>")
					+e.toString());
		}
	}

}
