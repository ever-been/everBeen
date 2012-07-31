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
package cz.cuni.mff.been.webinterface.screen;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cz.cuni.mff.been.common.id.SID;
import cz.cuni.mff.been.common.inputvalidator.InputValidator;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.Messages;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Page;
import cz.cuni.mff.been.webinterface.Params;
import cz.cuni.mff.been.webinterface.RSLValidator;
import cz.cuni.mff.been.webinterface.Routines;

/**
 * Processes screens in the web interface.
 * "Processes" means three things:
 * 
 * 1. Ensures all parameters were sent in the HTTP request (<code>ensure</code>
 *    method).
 * 2. Checks all parametsrs' values (<code>check</code> method).
 * 3. Updates all items' values according to the parameters sent in the HTTP
 *    request (<code>updateItems</code>) method.
 *    
 * For each type of item, there exists an subclass of <code>ItemHandler</code>
 * class. This subclass processes parameters for items of its type - the
 * <code>ScreenHandler</code> delegates its work using a Class->ItemHandler
 * hashmap.  
 *   
 * @author David Majda
 */
public class ScreenHandler {

	private static final String PARAM_SID = "sid"; 

	/**
	 * Subclasses of this abstract process HTTP parameters for items of given
	 * type. 
	 * 
	 * @author David Majda
	 */
	private abstract static class ItemHandler {
		/**
		 * Checks if required parameters were sent and they have valid values. If
		 * the parameters are OK, the method doesn't do anything, otherwise it
		 * throws <code>MissingParamException</code> or
		 * <code>InvalidParamValueException</code> respectively.
		 *  
		 * @param item processed screen item
		 * @throws MissingParamException if some required parameter is missing
		 * @throws InvalidParamValueException if required parameter contains
		 *          invalid value
		 */
		public abstract void ensure(Item item)
			throws MissingParamException, InvalidParamValueException;
		
		
		/**
		 * Checks if required parameters have valid values and writes error message
		 * if some parameters do not.
		 * 
		 * This method is used when validating free-text fields (e.g. when user
		 * should enter only integer value) and in similar situations.  
		 * 
		 * @param item processed screen item
		 */
		public abstract void check(Item item);
		
		/**
		 * Updates the item value according to the parameters sent in the HTTP
		 * request.
		 * 
		 * @param item processed screen item
		 */
		public abstract void updateItem(Item item);
	}
	
	private Params params = Params.getInstance();
	private Messages errorMessages = Page.getInstance().getErrorMessages(); 
	private HttpServletRequest request;
	private Map<Class< ? >, ItemHandler> itemHandlers;

	private final ItemHandler staticTextHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			/* Don't do anything  - no params expected. */
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			/* Don't do anything - no params expected. */
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			/* Don't do anything. */
		}
	};

	private final ItemHandler inputHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			params.ensureExists(item.formElementName());
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			InputValidator validator = ((Input) item).getValidator();
			if (validator != null) {
				String value = request.getParameter(item.formElementName());
				String validationResult = validator.validate(value);
				if (validationResult != null) {
					errorMessages.addTextMessage(item.getLabel() + ": "
							+ validationResult);
				}
			}
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			((Input) item).setValue(request.getParameter(item.formElementName()));
		}
	};

	private final ItemHandler rslInputHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			params.ensureExists(item.formElementName());
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			String rsl = request.getParameter(item.formElementName());
			String validationResult = RSLValidator.validate(rsl);
			if (validationResult != null) {
				errorMessages.addHTMLMessage(item.getLabel() + ": " + validationResult);
			}
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			((RSLInput) item).setValue(request.getParameter(item.formElementName()));
		}
	};
	
	private final ItemHandler selectHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			params.ensureExists(item.formElementName());
			String id = request.getParameter(item.formElementName());
			for (Option option: ((Select) item).getOptions()) {
				if (option.getId().equals(id)) {
					return;
				}
			}
			throw new InvalidParamValueException("Parameter \"" 
					+ item.formElementName() + "\" has invalid value.");
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			/* Don't do anything - every value that passed ensure should be OK. */
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			((Select) item).setSelectedId(request.getParameter(item.formElementName()));
		}
	};
	
	private final ItemHandler mulitSelectHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			params.ensureExists(item.formElementName());
			String[] indexStrings = Routines.split2(",",
				request.getParameter(item.formElementName()));
			for (String indexString: indexStrings) {
				int index;
				try {
					index = Integer.valueOf(indexString);
				} catch (NumberFormatException e) {
					throw new InvalidParamValueException("Parameter \"" 
							+ item.formElementName() + "\" has invalid value.");
				}
				if (index < 0 || ((MultiSelect) item).getOptions().length <= index) { 
					throw new InvalidParamValueException("Parameter \"" 
							+ item.formElementName() + "\" has invalid value.");
				}
			}
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			/* Don't do anything - every value that passed ensure should be OK. */
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			String[] indexStrings = Routines.split2(",",
				request.getParameter(item.formElementName()));
			int[] indexes = new int[indexStrings.length];
			for (int i = 0; i < indexes.length; i++) {
				indexes[i] = Integer.valueOf(indexStrings[i]);
			}
			((MultiSelect) item).setSelectedIndexes(indexes);
		}
	};
	
	private final ItemHandler radiosWithSectionsHandler = new ItemHandler() {
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#ensure(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void ensure(Item item)
				throws MissingParamException, InvalidParamValueException {
			params.ensureExists(item.formElementName());

			for (RadioWithSectionItem radioItem: ((RadiosWithSections) item).getItems()) {
				if (radioItem.getSection() != null) {
					for (Item item2: radioItem.getSection().getItems()) {
						itemHandlers.get(item2.getClass()).ensure(item2);
					}
				}
			}
			
			String id = request.getParameter(item.formElementName());
			for (RadioWithSectionItem radioItem: ((RadiosWithSections) item).getItems()) {
				if (radioItem.getOption().getId().equals(id)) {
					return;
				}
			}
			throw new InvalidParamValueException("Parameter \"" 
					+ item.formElementName() + "\" has invalid value.");
		}
		
		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#check(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void check(Item item) {
			String id = request.getParameter(item.formElementName());
			for (RadioWithSectionItem radioItem: ((RadiosWithSections) item).getItems()) {
				if (radioItem.getOption().getId().equals(id)) {
					if (radioItem.getSection() != null) {
						for (Item item2: radioItem.getSection().getItems()) {
							itemHandlers.get(item2.getClass()).check(item2);
						}
					}
				}
			}
		}

		/**
		 * @see cz.cuni.mff.been.webinterface.benchmarks.ScreenHandler.ItemHandler#updateItem(cz.cuni.mff.been.apps.gui.Item)
		 */
		@Override
		public void updateItem(Item item) {
			((RadiosWithSections) item).setSelectedId(request.getParameter(item.formElementName()));

			String id = request.getParameter(item.formElementName());
			for (RadioWithSectionItem radioItem: ((RadiosWithSections) item).getItems()) {
				if (radioItem.getOption().getId().equals(id)) {
					if (radioItem.getSection() != null) {
						for (Item item2: radioItem.getSection().getItems()) {
							itemHandlers.get(item2.getClass()).updateItem(item2);
						}
					}
				}
			}
		}
	};

	private final ItemHandler checkboxHandler = new ItemHandler() {

		@Override
		public void check(Item item) {
			// assuming always present
		}

		@Override
		public void ensure(Item item) throws MissingParamException,
				InvalidParamValueException {
			// assuming always present
		}

		@Override
		public void updateItem(Item item) {
			String value = request.getParameter(item.formElementName());
			((Checkbox) item).setChecked( value != null && !value.isEmpty() );
		}
	};

	/**
	 * Allocates a new <code>ScreenHandler</code> object.
	 * 
	 * @param aRequest HTTP request in which the parameters will be processed
	 */
	public ScreenHandler(HttpServletRequest aRequest) {
		this.request = aRequest;
		
		itemHandlers = new HashMap<Class< ? >, ItemHandler>();
		itemHandlers.put(StaticText.class, staticTextHandler); 		
		itemHandlers.put(Input.class, inputHandler);
		itemHandlers.put(RSLInput.class, rslInputHandler);
		itemHandlers.put(Select.class, selectHandler);
		itemHandlers.put(MultiSelect.class, mulitSelectHandler);
		itemHandlers.put(RadiosWithSections.class, radiosWithSectionsHandler);
		itemHandlers.put(Checkbox.class, checkboxHandler);
	}
	
	/**
	 * Checks if required parameters were sent for all items and they have valid
	 * values. If the parameters are OK, the method doesn't do anything, otherwise
	 * it throws <code>MissingParamException</code> or
	 * <code>InvalidParamValueException</code> respectively.
	 *  
	 * @param screen processed screen
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IllegalScreenSequenceException 
	 */
	public void ensure(Screen screen) throws MissingParamException,
			InvalidParamValueException, IllegalScreenSequenceException {
		params.ensureExists(PARAM_SID);
		int sid;
		try {
			sid = Integer.valueOf(request.getParameter(PARAM_SID));
		} catch (NumberFormatException e) {
			throw new InvalidParamValueException("Parameter \"" 
					+ PARAM_SID + "\" has invalid value.");
		}
		if ( !screen.getSid().equals(new SID(sid)) ){
			throw new IllegalScreenSequenceException();
		}
		for (Section section: screen.getSections()) {
			for (Item item: section.getItems()) {
				itemHandlers.get(item.getClass()).ensure(item);
			}
		}
	}

	/**
	 * Checks if required parameters for all items have valid values and writes
	 * error message if some parameters do not.
	 * 
	 * @param screen processed screen
	 */
	public void check(Screen screen) {
		for (Section section: screen.getSections()) {
			for (Item item: section.getItems()) {
				itemHandlers.get(item.getClass()).check(item);
			}
		}
	}

	/**
	 * Updates all items' values according to the parameters sent in the HTTP
	 * request.
	 * 
	 * @param screen processed screen
	 */
	public void updateItems(Screen screen) {
		for (Section section: screen.getSections()) {
			for (Item item: section.getItems()) {
				itemHandlers.get(item.getClass()).updateItem(item);
			}
		}
	}
}
