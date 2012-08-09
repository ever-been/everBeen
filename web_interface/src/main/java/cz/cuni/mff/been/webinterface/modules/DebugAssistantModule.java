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
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.debugassistant.DebugAssistantException;
import cz.cuni.mff.been.debugassistant.DebugAssistantInterface;
import cz.cuni.mff.been.debugassistant.SuspendedTask;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

import static cz.cuni.mff.been.services.Names.DEBUG_ASSISTANT_SERVICE_HUMAN_NAME;
import static cz.cuni.mff.been.services.Names.DEBUG_ASSISTANT_SERVICE_NAME;


/**
 * Debug assistant module
 * 
 * @author Jan Tattermusch
 */
public class DebugAssistantModule extends Module {

	/** Class instance (singleton pattern). */
	private static DebugAssistantModule instance;

	private HashMap< String, Object > data = new HashMap< String, Object >();

	private ServiceReference<DebugAssistantInterface> debugAssistant;

	/**
	 * Allocates a new <code>DummyConfigModule</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private DebugAssistantModule() {
		super();
		
		this.debugAssistant = new ServiceReference< DebugAssistantInterface >(
				new TaskManagerReference(),
				DEBUG_ASSISTANT_SERVICE_NAME,
				Service.RMI_MAIN_IFACE,
				DEBUG_ASSISTANT_SERVICE_HUMAN_NAME
			);

		
		/* Initialize general module info... */
		this.id = "debugassistant";
		this.name = "Debug Assistant";
		this.defaultAction = "list-tasks";
		
		this.menu = new MenuItem[] {
			new MenuItem("list-tasks", "List Tasks")
		};
	}
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static DebugAssistantModule getInstance() {
		if (instance == null) {
			 instance = new DebugAssistantModule();
		}
		return instance;
	}


	/**
	 * Prints out the list of suspended tasks in debug assistant
	 * 
	 * @param request
	 * @param response
	 * @throws ComponentInitializationException
	 * @throws ServletException
	 * @throws IOException
	 */
	public void listTasks( HttpServletRequest request, HttpServletResponse response )
	throws ComponentInitializationException, ServletException, IOException {
		
		
		try {
			if (params.exists("run")) {
				params.ensureExist("taskid");

				UUID taskId = UUID.fromString(request.getParameter("taskid"));
				debugAssistant.get().runSuspendedTask( taskId);
				
				infoMessages.addTextMessage("Task has been run successfully.");
			}
			
			if (params.exists("delete")) {
				params.ensureExist("taskid");

				UUID taskId = UUID.fromString(request.getParameter("taskid"));
				debugAssistant.get().unregisterTask(taskId);
				
				infoMessages.addTextMessage("Task has been deleted from Debug Assistant's list.");
			}

			page.setTitle("Suspended tasks");


			// load list of analyses
			Collection<SuspendedTask> suspendedTasks = null;

			suspendedTasks = debugAssistant.get().getSuspendedTasks();

			data.put("tasks", suspendedTasks);

			if( suspendedTasks.size() <= 0 ){
				warningMessages.addTextMessage("Couldn't find any tasks to run.");
			}
		
		} catch (DebugAssistantException e) {
			handleException(e, "Error occured when executing request.");
		} catch (MissingParamException e) {
			handleException(e, "Error occured when executing request.");
		}
		
		page.writeHeader();
		page.writeTemplate( "debugassistant-list-tasks", data );
		page.writeFooter();
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
