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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.webinterface.modules.Module;

/**
 * Class responsible for managing of several things when generating the page.
 * TODO: This singleton should be converted to a static class.
 * 
 * @author David Majda
 */
public class Page {
	/**
	 * Laoyut types.
	 * 
	 * @author David Majda
	 */
	public enum LayoutType {
		/** Normal layout type, used on most pages. */
		NORMAL,
		/** Simple layout type, used in popups such as R functions help. */
		SIMPLE
	}
	
	/** 
	 * Directory with templates (relative to the application's root directory, including leading
	 * and trailing slash).
	 */
	private static final String TEMPLATE_DIR = "/templates/";
	
	/** Template extension, including leading dot. */ 
	private static final String TEMPLATE_EXT = ".jsp";

	/** Class instance (singleton pattern). */
	private static Page instance;

	/** 
	 * HTTP request. Needs to be set by the servlet every time when the request
	 * is processed.
	 */
	private HttpServletRequest request = null;
	
	/** 
	 * HTTP response. Needs to be set by the servlet every time when the request
	 * is processed.
	 */
	private HttpServletResponse response = null;
	
	/** 
	 * Servlet context. Needs to be set by the servlet every time when the request
	 * is processed.
	 */
	private ServletContext context = null;
	
	/**
	 * Relative path from current dir to the root dir; includes trailing
	 * slash.
	 */ 
	private String rootPath = "";
	
	/** Page title. */
	private String title = "";
	
	/** Flag if show page title at the top of the page. */
	private boolean showTitle = true;
	
	/** Layout type. */
	private LayoutType layoutType = LayoutType.NORMAL;
	
	/**
	 * Focused element ID. See comment at the <code>setFocusedElement</code>
	 * method.
	 */ 
	private String focusedElementId = null;
	
	/**
	 * Focused element form. See comment at the <code>setFocusedElement</code>
	 * method.
	 */ 
	private String focusedElementForm = null;
	
	/**
	 * Focused element name. See comment at the <code>setFocusedElement</code>
	 * method.
	 */ 
	private String focusedElementElement = null;
	
	/** Web interface modules. */
	private ArrayList<Module> modules = new ArrayList<Module>();
	
	/**
	 * Active module ID. Needs to be set by the servlet every time when the request
	 * is processed.
	 */
	private String activeModule = "";
	
	/**
	 * Active module ID. Needs to be set by the servlet every time when the request
	 * is processed.
	 */
	private String activeAction = "";
	
	/**
	 * Information messages for the user.
	 */
	private Messages infoMessages = new Messages();
	
	/**
	 * Warning messages for the user.
	 */
	private Messages warningMessages = new Messages();
	
	/**
	 * Error messages for the user.
	 */
	private Messages errorMessages = new Messages();
	
	/**
	 * Allocates a new <code>Page</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private Page() {
		super();
	}
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static Page getInstance() {
		if (instance == null) {
			instance = new Page();
		}
		return instance;
	}
	
	/** @return HTTP request */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Sets the HTTP request.
	 * 
	 * @param request the HTTP request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/** @return HTTP response */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Sets the HTTP response.
	 * 
	 * @param response the HTTP response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/** @return servlet context */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * Sets the servlet context.
	 * 
	 * @param context the servlet context to set
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}

	/** @return relative path from current dir to the root dir */
	public String getRootPath() {
		return rootPath;
	}
	
	/**
	 * Sets the relative path from current dir to the root dir.
	 * 
	 * @param rootPath Relative path from current dir to the root dir to set
	 */
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	/** @return page title */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the page title.
	 * 
	 * @param title page title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/** @return flag if show page title at the top of the page */
	public boolean getShowTitle() {
		return showTitle;
	}
	
	/**
	 * Sets the flag if show page title at the top of the page.
	 * 
	 * @param showTitle flag to set
	 */
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle; 
	}

	/** @return layout type */
	public LayoutType getLayoutType() {
		return layoutType;
	}
	
	/**
	 * Sets the layout type.
	 * 
	 * @param layoutType layout type to set
	 */
	public void setLayoutType(LayoutType layoutType) {
		this.layoutType = layoutType; 
	}

	/** @return the focusedElementId */
	public String getFocusedElementId() {
		return focusedElementId;
	}

	/** @return the focusedElementForm */
	public String getFocusedElementForm() {
		return focusedElementForm;
	}

	/** @return the focusedElementName */
	public String getFocusedElementElement() {
		return focusedElementElement;
	}

	/**
	 * Sets the focused element ID.
	 * 
	 * Focused elements can be set by their ID or by the form and element name,
	 * hence more overloads of method <code>setFocusedElement</code>.
	 * 
	 * @param id focused element ID to set
	 */
	public void setFocusedElement(String id) {
		focusedElementId = id;
		focusedElementForm = null;
		focusedElementElement = null;
	}
	
	/**
	 * Sets the focused element form and name.
	 * 
	 * Focused elements can be set by their ID or by the form and element name,
	 * hence more overloads of method <code>setFocusedElement</code>.
	 * 
	 * @param form focused element form to set
	 * @param element focused element name to set
	 */
	public void setFocusedElement(int form, int element) {
		focusedElementId = null;
		focusedElementForm = Integer.toString(form);
		focusedElementElement = Integer.toString(element);
	}

	/**
	 * Sets the focused element form and name.
	 * 
	 * Focused elements can be set by their ID or by the form and element name,
	 * hence more overloads of method <code>setFocusedElement</code>.
	 * 
	 * @param form focused element form to set
	 * @param element focused element name to set
	 */
	public void setFocusedElement(int form, String element) {
		focusedElementId = null;
		focusedElementForm = Integer.toString(form);
		focusedElementElement = element;
	}

	/**
	 * Sets the focused element form and name.
	 * 
	 * Focused elements can be set by their ID or by the form and element name,
	 * hence more overloads of method <code>setFocusedElement</code>.
	 * 
	 * @param form focused element form to set
	 * @param element focused element name to set
	 */
	public void setFocusedElement(String form, int element) {
		focusedElementId = null;
		focusedElementForm = form;
		focusedElementElement = Integer.toString(element);
	}

	/**
	 * Sets the focused element form and name.
	 * 
	 * Focused elements can be set by their ID or by the form and element name,
	 * hence more overloads of method <code>setFocusedElement</code>.
	 * 
	 * @param form focused element form to set
	 * @param element focused element name to set
	 */
	public void setFocusedElement(String form, String element) {
		focusedElementId = null;
		focusedElementForm = form;
		focusedElementElement = element;
	}

	/** @return web interface module count */
	public int getModuleCount() {
		return modules.size();
	}
	
	/**
	 * Returns specified web interface module.
	 * 
	 * @param index web interface module index
	 * @return web interface module with specified index
	 */
	public Module getModule(int index) {
		return modules.get(index);
	}
	
	/**
	 * Returns web interface module with specified ID.
	 * 
	 * @param id web interface module ID
	 * @return web interface module with specified ID
	 */
	public Module getModuleById(String id) {
		for ( Module module : modules ) {
			if (module.getId().equals(id)) {
				return module;
			}
		}
		return null;
	}
	
	/**
	 * Adds new web interface module.
	 * 
	 * @param module web interface module to add
	 */
	public void addModule(Module module) {
		modules.add(module);
	}
	
	/**
	 * Deletes all web interface modules. 
	 */
	public void clearModules() {
		modules.clear();
	}
	
	/** @return active module ID */
	public String getActiveModule() {
		return activeModule;
	}
	
	/**
	 * Sets the active module ID.
	 * 
	 * @param activeModule active module ID to set
	 */
	public void setActiveModule(String activeModule) {
		this.activeModule = activeModule;
	}
		
	/** @return active action */
	public String getActiveAction() {
		return activeAction;
	}
	
	/**
	 * Sets the active action.
	 * 
	 * @param activeAction active action to set
	 */
	public void setActiveAction(String activeAction) {
		this.activeAction = activeAction;
	}

	/** @return information messages for the user */
	public Messages getInfoMessages() {
		return infoMessages;
	}
	
	/**
	 * Sets the information messages for the user.
	 * 
	 * @param infoMessages information messages for the user to set
	 */
	public void setInfoMessages(Messages infoMessages) {
		this.infoMessages = infoMessages;
	}
	
	/** @return warning messages for the user */
	public Messages getWarningMessages() {
		return warningMessages;
	}
	
	/**
	 * Sets the warning messages for the user.
	 * 
	 * @param warningMessges warning messages for the user to set
	 */
	public void setWarningMessages(Messages warningMessges) {
		this.warningMessages = warningMessges;
	}

	/** @return error messages for the user */
	public Messages getErrorMessages() {
		return errorMessages;
	}
	
	/**
	 * Sets the error messages for the user.
	 * 
	 * @param errorMessges error messages for the user to set
	 */
	public void setErrorMessages(Messages errorMessges) {
		this.errorMessages = errorMessges;
	}

	/**
	 * Writes a template filled with specified data.
	 * 
	 * @param template name of the template
	 * @param data <code>Map&lt;String, Object&gt;</code> with data, keys must be
	 *         <code>String</code>s; can be <code>null</code>
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeTemplate(String template, Map<String, Object> data) throws ServletException, IOException {
		if (data != null) {
			for ( String key : data.keySet() ) {
				context.setAttribute(key, data.get(key));
			}
		}
		context.getRequestDispatcher(TEMPLATE_DIR + template + TEMPLATE_EXT).include(request, response);
	}
	
	/**
	 * Writes a template without any data.
	 * 
	 * @param template name of the template
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeTemplate(String template) throws ServletException, IOException {
		writeTemplate(template, new HashMap<String, Object>());
	}

	/**
	 * Writes page header.
	 *
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeHeader() throws ServletException, IOException {
		String onload;
		if (focusedElementForm != null && focusedElementElement != null) {
			onload = "if (shouldAutoFocus()) focusElementByFormAndElement("
				+ (Routines.isInteger(focusedElementForm)
					? focusedElementForm
					: "\"" + focusedElementForm + "\"")
				+ ", "
				+ (Routines.isInteger(focusedElementElement)
					? focusedElementElement
					: "\"" + focusedElementElement + "\"")
				+ ");";
		} else  if (focusedElementId != null) {
			onload = "if (shouldAutoFocus()) focusElementById(\""
				+ focusedElementId + "\");";
		} else {
			onload = "";
		}
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("onload", onload);
		
		response.setContentType("text/html; charset=utf-8");
		writeTemplate("header", data);
	}
	
	/**
	 * Writes page footer.
	 * 
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeFooter() throws ServletException, IOException {
		writeTemplate("footer");
	}

	/**
	 * Writes info messages obtained from the <code>Errors</code> class.
	 * 
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeInfoMessages() throws ServletException, IOException {
		writeTemplate("info-messages");
	}

	/**
	 * Writes warning messages obtained from the <code>Errors</code> class.
	 * 
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeWarningMessages() throws ServletException, IOException {
		writeTemplate("warning-messages");
	}

	/**
	 * Writes error messages obtained from the <code>Errors</code> class.
	 * 
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 */
	public void writeErrorMessages() throws ServletException, IOException {
		writeTemplate("error-messages");
	}
	
	/**
	 * Redirect to specified action and supplies this action with parameters.
	 * 
	 * @param action action to redirect
	 * @param params action parameters
	 * @throws IOException if some I/O error occurs
	 */
	public void redirectToAction(String action, Map<String, String> params) throws IOException {
		String location = rootPath + activeModule + "/" + action + "/";
		if (!params.isEmpty()) {
			String[] paramsInURL = new String[params.size()];
			int i = 0;
			for ( String key : params.keySet() ) {
				String value = params.get(key);
				paramsInURL[i] = URLEncoder.encode(key, "UTF-8")
					+ "=" + URLEncoder.encode(value, "UTF-8");
				i++;
			}
			location += "?" + Routines.join("&", paramsInURL);
		}
		response.sendRedirect(response.encodeRedirectURL(location));
	}

	/**
	 * Redirect to specified action with no parameters.
	 * 
	 * @param action action to redirect
	 * @throws IOException if some I/O error occurs
	 */
	public void redirectToAction(String action) throws IOException {
		redirectToAction(action, new HashMap<String, String>());
	}
	
	/**
	 * Returns relative URL of the current action ("./").
	 * 
	 * @return relative URL of the current action ("./")
	 */
	public String currentActionURL() {
		return "./";
	}
	
	/**
	 * Returns relative URL for specified module and action. Returned URL
	 * includes trailing slash.
	 * 
	 * @param module module
	 * @param action action
	 * @return relative URL for specified module and action
	 */
	public String moduleActionURL(String module, String action) {
		return rootPath + module + "/" + action + "/";
	}

	/**
	 * Returns relative URL for action in current module. Returned URL includes
	 * trailing slash.
	 * 
	 * @param action action
	 * @return relative URL for specified action in current module
	 */
	public String actionURL(String action) {
		return moduleActionURL(activeModule, action);
	}
}
