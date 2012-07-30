/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.webinterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.webinterface.Page.LayoutType;
import cz.cuni.mff.been.webinterface.modules.ConfigurationModule;
import cz.cuni.mff.been.webinterface.modules.BenchmarksNgModule;
import cz.cuni.mff.been.webinterface.modules.DebugAssistantModule;
import cz.cuni.mff.been.webinterface.modules.HostsModule;
import cz.cuni.mff.been.webinterface.modules.Module;
import cz.cuni.mff.been.webinterface.modules.PackagesModule;
import cz.cuni.mff.been.webinterface.modules.ResultsRepositoryNGModule;
import cz.cuni.mff.been.webinterface.modules.ServicesModule;
import cz.cuni.mff.been.webinterface.modules.TasksModule;

/**
 * Web interface servlet. It is responsible for managing web interface modules
 * and redirecting requests to them. Redirection is based on the URL.
 * 
 * @author David Majda
 */
public class WebInterfaceServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1770424669837834446L;

	private Page page = Page.getInstance();
	
	/**
	 * Called by the servlet container to indicate to a servlet that the servlet
	 * is being placed into service. Initialises modules.
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		page.clearModules(); /* List needs to be cleared so reinitialisation is possible. */
		page.addModule(PackagesModule.getInstance());
		page.addModule(HostsModule.getInstance());
		page.addModule(TasksModule.getInstance());
		page.addModule(BenchmarksNgModule.getInstance());
		page.addModule(ResultsRepositoryNGModule.getInstance());
		page.addModule(DebugAssistantModule.getInstance());
		page.addModule(ConfigurationModule.getInstance());
		page.addModule(ServicesModule.getInstance());
	}

	/**
	 * Called by the servlet container to indicate to a servlet that the servlet
	 * is being taken out of service.
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		page.clearModules(); // Allow GC of the modules.
	}

	/**
	 * Disables caching for the response. Code is borrowed from the PHP manual
	 * and adapted for Java.
	 * 
	 * @param response an <code>HttpServletResponse</code> object that contains
	 *         the response the servlet sends to the client
	 */
	private void disableCaching(HttpServletResponse response) {
		/* Date in the past. */ 
		response.addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); 

		/* Always modified. */ 
		response.addDateHeader("Last-Modified", (new Date()).getTime()); 

		/* HTTP/1.1 */ 
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); 
		response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 

		/* HTTP/1.0 */ 
		response.addHeader("Pragma", "no-cache");
	}
	
	private void writeWelcomePage(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		page.setRequest(request);
		page.setResponse(response);
		page.setContext(getServletContext());
		page.setRootPath("./");
		page.setShowTitle(true);
		page.setLayoutType(LayoutType.NORMAL);
		page.setActiveModule("");
		page.setActiveAction("");
		
		page.setTitle("");
		page.writeHeader();
		page.writeTemplate("welcome");
		page.writeFooter();
	}
	
	private void writeRedirectToAction(HttpServletRequest request,
			HttpServletResponse response, String moduleID, String action,
			String rootPath) throws IOException {
		page.setRequest(request);
		page.setResponse(response);
		page.setContext(getServletContext());
		page.setRootPath(rootPath);
		page.setActiveModule(moduleID);
		page.setActiveAction("");
		page.redirectToAction(action);
	}

	private void writeUserFriendlyExceptionMessage(Throwable cause)
			throws ServletException, IOException {
		page.getErrorMessages().addHTMLMessage(cause.getMessage());
		page.writeHeader();
		page.writeErrorMessages();
		page.writeFooter();
	}

	private void writeModuleActionPage(HttpServletRequest request,
			HttpServletResponse response, Module module, String action)
			throws ServletException, IOException {
		/* Set up the Page object. */
		page.setRequest(request);
		page.setResponse(response);
		page.setContext(getServletContext());
		page.setRootPath("../../");
		page.setShowTitle(true);
		page.setLayoutType(LayoutType.NORMAL);
		page.setActiveModule(module.getId());
		page.setActiveAction(action);
		
		/* Delegate the action to the module. */
		try {
			module.invokeMethodForAction(request, response, action);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof MissingParamException
					|| cause instanceof InvalidParamValueException
					|| cause instanceof InvalidRequestMethodException
					|| cause instanceof ComponentInitializationException
					|| cause instanceof ConnectException
					|| cause instanceof RemoteException
			) {
				writeUserFriendlyExceptionMessage(cause);
			} else {
				throw new ServletException("Method invocation failed.", e.getCause());
			}
		}
	}
	
	/**
	 * Processes the GET or POST requests and redirects them to web interface
	 * modules according to the URL. Method is synchronised, because multiple
	 * threads can call the servlet simultaneously.
	 *
	 * URL of the request should be in following format (excluding possible
	 * parameters and anchor identifier for clarity):
	 *   
	 *   1. http://&lt;host&gt;/been/
	 *   2. http://&lt;host&gt;/been/&lt;module&gt;/
	 *   3. http://&lt;host&gt;/been/&lt;module&gt;/&lt;action&gt;/
	 * 
	 * In case 1, we show a special welcome page.
	 * 
	 * In case 2 we extract the &lt;module&gt;, find the module object according
	 * to given &lt;module&gt; and and redirect to default action of that module.
	 * 
	 * In case 3 we extract the &lt;module&gt; and &lt;action&gt;, find the
	 * module object according to given &lt;module&gt; and call a method of
	 * module, which typically delegates control to a method corresponding to
	 * given &lt;action&gt; via reflection.
	 * 
	 * We don't call methods via reflection directly, because we want to give
	 * the module a chance to catch exceptions coming from methods (mostly
	 * coming from failures in the RMI calls) in one place.
	 * 
	 * If the URL is not in specified format, or specified module or action is
	 * not found, we write an error page.  
	 * 
	 * @param request an <code>HttpServletRequest</code> object that contains the
	 *         request the client has made of the servlet
	 * @param response an <code>HttpServletResponse</code> object that contains
	 *         the response the servlet sends to the client
	 *         
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if an input or output error is detected when the
	 *          servlet handles the request
	 */
	private synchronized void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/* Make sure the request is parsed in UTF-8 (this encoding is used in the
		 * whole application). This statement helps only in POST requests - to parse
		 * the GET requests correctly, user needs to set attribute
		 * 
		 *   URIEncoding="UTF-8"
		 *   
		 * in the connector definition (<Connector> element in
		 * $TOMCAT_HOME/conf/server.xml file).
		 */ 
		request.setCharacterEncoding("UTF-8");

		disableCaching(response);
		page.setFocusedElement(null);
		page.getInfoMessages().clear();
		page.getWarningMessages().clear();
		page.getErrorMessages().clear();

		String[] parts = request.getRequestURI().split("/");
		switch (parts.length) {
			case 2:
				writeWelcomePage(request, response);
				break;
			case 3:
			case 4:
				String moduleID = parts[2];
				Module module = page.getModuleById(moduleID);
				if (module == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND,
							"Invalid URL: Module \"" + moduleID + "\" not found.");
					return;
				}
				if (parts.length == 4) {
					String action = parts[3];
					if (request.getRequestURI().endsWith("/")) {
						writeModuleActionPage(request, response, module, action);
					} else {
						writeRedirectToAction(request, response, moduleID, action, "../");
					}
				} else {
					String rootPath = request.getRequestURI().endsWith("/") ? "../" : "./";
					writeRedirectToAction(request, response, moduleID,
						module.getDefaultAction(), rootPath);
				}
				break;
			default:		
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						"Invalid URL: Part count doesn't match.");
				return;
		}
	}
	
	/**
	 * Called by the server (via the <code>service</code> method) to allow a
	 * servlet to handle a GET request.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* We process GET and POST request same way... */
		processRequest(request, response);
	}
	
	/**
	 * Called by the server (via the <code>service</code> method) to allow
	 * a servlet to handle a POST request.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* We process GET and POST request same way... */
		processRequest(request, response);
	}

}
