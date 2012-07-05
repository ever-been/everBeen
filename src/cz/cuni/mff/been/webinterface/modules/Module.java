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
package cz.cuni.mff.been.webinterface.modules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.webinterface.Messages;
import cz.cuni.mff.been.webinterface.Page;
import cz.cuni.mff.been.webinterface.Params;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.event.EventManager;

/**
 * Web interface module base class. Modules wrap functionality in one area of
 * the interface, substituting methods for parts of the URLs (called
 * "actions").
 * 
 * @author David Majda
 */
public class Module {
	protected Page page = Page.getInstance();
	protected Params params = Params.getInstance();
	protected EventManager eventManager = EventManager.getInstance(); 
	protected Messages infoMessages = page.getInfoMessages(); 
	protected Messages warningMessages = page.getWarningMessages();
	protected Messages errorMessages = page.getErrorMessages(); 

	/** Module ID. Should contain only alphanumeric characters or dash ("-"). */
	protected String id;
	/** Human-readable module name. */
	protected String name;
	/** Name of the default action. */
	protected String defaultAction;
	/** Module menu; maps action names to human-readable section descriptions. */ 
	protected MenuItem[] menu;

	/**
	 * A public trap door to other means of control, such as the command line interface.
	 * Sends an event using the web interface's internal event manager.
	 * 
	 * @param event The event to send.
	 */
	public void sendEvent( Event event ) {
		eventManager.sendEvent( event );
	}
	
	/**
	 * A public trap door to other means of control, such as the command line interface.
	 * Registers an event listener with the web interface's event manager.
	 * 
	 * @param listener
	 */
	public void registerEventListener( EventListener listener ) {
		eventManager.registerEventListener( listener );
	}
	
	/** @return module ID. */
	public String getId() {
		return id;
	}

	/** @return human-readable module name. */
	public String getName() {
		return name;
	}

	/** @return name of the default action. */
	public String getDefaultAction() {
		return defaultAction;
	}

	/** @return module menu */
	public MenuItem[] getMenu() {
		return menu;
	}

	/**
	 * Transforms action name to method name. It searches for dash character
	 * ("-"), omits them and capitalises one letter after them.
	 * 
	 * For example text "long-action-name" becomes "longActionName".
	 * 
	 * @param action action name
	 * @return method name derived from the action name
	 */
	private String actionToMethod(String action) {
		String result = "";
		boolean makeNextUpperCase = false;
		for (int i = 0; i < action.length(); i++) {
			char ch = action.charAt(i);
			if (ch == '-') {
				makeNextUpperCase = true;
			} else {
				if (makeNextUpperCase) {
					result += Character.toUpperCase(ch);
					makeNextUpperCase = false;
				} else {
					result += ch;
				}
			}
		}
		return result;
	}
	
	/**
	 * Return <code>Method</code> object corresponding to given action name.
	 * 
	 * @param action action name
	 * @return <code>Method</code> object corresponding to given action name
	 * @throws SecurityException if there is some security violation when
	 *          obtaining the <code>Method</code> object
	 * @throws NoSuchMethodException if corresponding method cannot be found
	 */
	private Method getMethodForAction(String action) throws
			NoSuchMethodException {
		Class< ? >[] params = { HttpServletRequest.class, HttpServletResponse.class };
		String methodName = actionToMethod(action);
		Method result;
		try {
			result = getClass().getMethod(methodName, params);
		} catch (NoSuchMethodException e) {
			/* Little hack: Some methods would have same names as Java reserved
			 * words, so they have an underscore appended. Before we give up, try to
			 * find a method with underscore at the end.
			 */
			result = getClass().getMethod(methodName + "_", params);
		}
		return result;
	}
	
	/**
	 * Invokes method for given action, which is found by reflection. If the 
	 * method can't be found or is not accessible for security reasons, write
	 * an error page. Other improbable exceptions are transformed into
	 * <code>ServletException</code>.  
	 * 
	 * Exceptions from the invoked method itself are encapsulated in the
	 * <code>InvocationTargetException</code>, which is passed to the caller
	 * - the servlet. Some known contained exceptions are then displayed to the
	 * user in a "user friendly" manner, other are thrown further and caught by
	 * the exception handler which produces usual ugly exception message.
	 * 
	 * Concrete module implementations will typically override this method and
	 * apply additional exception processing. This is necessary to give user
	 * more meaningful error messages in cases such as invalidating of the
	 * remote RMI reference (user needs to know which service has problems, not
	 * to see some low-level description which the original exception contains).
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param action action to invoke
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if some kind of method invocation error occurs 
	 * @throws InvocationTargetException wraps an exception thrown by the invoked
	 *                                    method  
	 */
	public void invokeMethodForAction(HttpServletRequest request,
			HttpServletResponse response, String action) throws ServletException,
			IOException, InvocationTargetException {
		Object[] params = { request, response };
		Method method;
		
		/* Find method for given action. */
		try {
			method = getMethodForAction(action);
		} catch (SecurityException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Invalid URL: Method for action \"" + action + "\" can't be accessed.");
			return;
		} catch (NoSuchMethodException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Invalid URL: Method for action \"" + action + "\" not found.");
			return;
		}
		
		/* Invoke the method. */
		try {
			method.invoke(this, params);
		} catch (IllegalAccessException e) {
			throw new ServletException("Method invocation failed.", e);
		} catch (IllegalArgumentException e) {
			throw new ServletException("Method invocation failed.", e);
		} 
	}

}
