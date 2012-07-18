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
package cz.cuni.mff.been.webinterface.screen.transcoder;

import java.util.ArrayList;
import java.util.TreeMap;

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.common.inputvalidator.InputValidator;
import cz.cuni.mff.been.common.inputvalidator.InputValidatorFactory;
import cz.cuni.mff.been.jaxb.config.Type;
import cz.cuni.mff.been.jaxb.config.Value;
import cz.cuni.mff.been.webinterface.screen.*;
import cz.cuni.mff.been.webinterface.screen.Input.Size;

/**
 * Class used to Convert JAXB Config elements into Screen elements in ScreenTranscoder
 * 
 * @see ScreenTranscoder
 * 
 * @author Jiri Tauber
 */
class JaxbConfigTranscoder {

	private enum JaxbItemType {
		CHECKBOX( "checkbox" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {
				return new Checkbox(
						item.getId(),
						item.getDesc(),
						"true".equalsIgnoreCase( value != null ? value :
							(item.getDefault() != null ? item.getDefault() : "")));
			}
		},
		RADIO( "radio" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {
				// TODO Auto-generated method stub
				return null;
			}
		},
		TEXT( "text" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {

				return createInput(item, Size.BIG, value);
			}
		},
		TEXTAREA( "textarea" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {
				return createInput(item, Size.AREA, value);
			}
		},
		RSL( "rsl" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {
				return new RSLInput(
						item.getId(),
						item.getDesc(),
						value != null ? value :
							(item.getDefault() != null ? item.getDefault() : "")
				);
			}
		},
		UNKNOWN( "unknown type" ) {
			@Override
			public Item getItem(
					cz.cuni.mff.been.jaxb.config.Item item,
					String value) {
				return createInput(item, Size.BIG, value);
			}
		};

		/** Name of the type (used in XML). */
		private final String typeName;
		
		private JaxbItemType( String typeName ) {
			this.typeName = typeName;
		}			

		@Override
		public String toString() {
			return typeName;
		}

		public abstract Item getItem(
				cz.cuni.mff.been.jaxb.config.Item item,
				String value);

		/**
		 * Creates new input field - agregates functions of TextArea & Text
		 * which differ only in one parameter
		 * 
		 * @param item
		 * @param size
		 * @param value
		 * @return the screen representation of a text field
		 */
		private static Input createInput(
				cz.cuni.mff.been.jaxb.config.Item item,
				Size size,
				String value) {
			InputValidator validator = null;
			if( item.isSetConstraint() ){
				validator = InputValidatorFactory.fromString(item.getConstraint());
			}
			return new Input(
					item.getId(),
					item.getDesc(),
					value != null ? value :
						(item.getDefault() != null ? item.getDefault() : ""),
					size,
					validator);
		}
	}

	/** Map used for fast selection of item transcoder by item type */
	TreeMap<String,JaxbItemType> reverseMap;

	TreeMap<String, Type> customTypes;

	/**
	 * Initialize new Transcoder instance
	 * @param config - The JAXB tree root containing all the user-defined types
	 */
	public JaxbConfigTranscoder(cz.cuni.mff.been.jaxb.config.Config config) {

		reverseMap = new TreeMap<String,JaxbItemType>();
		for ( JaxbItemType type : JaxbItemType.values() ) {
			reverseMap.put( type.toString(), type );
		}

		customTypes = new TreeMap<String, Type>();
		for( Type type : config.getType() ){
			customTypes.put(type.getId(), type);
		}

	}


	/**
	 * Convert single JAXB item into Screen Item.
	 * @param jaxbItem
	 * @return the screen item representation
	 */
	public Item getItem(cz.cuni.mff.been.jaxb.config.Item jaxbItem, Configuration configuration) {
		String[] values;
		values = configuration != null ? configuration.get(jaxbItem.getId()) : null;

		JaxbItemType type = reverseMap.get(jaxbItem.getType());
		String value = values != null && values.length > 0 ? values[0] : null;
		if( type != null ){
			return type.getItem(jaxbItem, value);
		} else {
			Type customType = customTypes.get(jaxbItem.getType());
			if( customType != null ){
				if( customType.isSetMulti() && customType.isMulti() ){
					return getMultiselect( jaxbItem, values, customType );
				} else {
					for(Value option : customType.getValue()){
						// one group in this type is enough to call it radio
						if( option.isSetGroup() ){
							return getRadio( jaxbItem, configuration, customType);
						}
					}
					return getSelect(jaxbItem, value, customType);
				}
			}
		}
		return JaxbItemType.UNKNOWN.getItem(jaxbItem, value);
	}


	//***** Private methods **************************************************//
	/**
	 * @param jaxbItem
	 * @param values
	 * @param type
	 * @return the screen representation for custom multiselect field 
	 */
	private Item getMultiselect(
			cz.cuni.mff.been.jaxb.config.Item jaxbItem,
			String[] values,
			Type type) {

		Option[] options = createOptions(type);

		// create list of selected items' indexes
		int[] selected = new int[0];
		if( values != null && values.length > 0 ){
			ArrayList<Integer> tmpSelected = new ArrayList<Integer>();
			int i = 0;
			for (Option o : options) {
				for (String s : values) {
					if( o.getId().equals(s) ){
						tmpSelected.add(i);
						break;
					}
				}
				i++;
			}
			if( !tmpSelected.isEmpty() ){
				selected = new int[tmpSelected.size()];
				for (int j = 0; j < tmpSelected.size(); j++) {
					selected[j] = tmpSelected.get(j);
				}
			}
		}
		return new MultiSelect(
				jaxbItem.getId(),
				jaxbItem.getDesc(),
				options,
				false,
				selected);
	}


	/**
	 * @param jaxbItem
	 * @param value
	 * @param type
	 * @return the screen representation of select item
	 */
	private Item getSelect(
			cz.cuni.mff.been.jaxb.config.Item jaxbItem,
			String value,
			Type type) {

		Option[] options = createOptions(type);

		int selected = 0;
		if( value != null ){
			int i = 0;
			for (Option o : options) {
				if( o.getId().equals(value) ){
					selected = i;
					break;
				}
				i++;
			}
		}
		return new Select(
				jaxbItem.getId(),
				jaxbItem.getDesc(),
				options,
				selected);
	}


	/**
	 * Creates options for the select type.
	 * @param type the select type JAXB representation
	 * @return the screen option list
	 */
	private Option[] createOptions(Type type) {
		ArrayList<Option> options = new ArrayList<Option>();
		for(Value value : type.getValue()){
			options.add( new Option(value.getName(), value.getDesc()) );
		}
		return options.toArray(new Option[0]);
	}


	/**
	 * @param jaxbItem
	 * @param type
	 * @return the screen representation of a radio selection
	 */
	private RadiosWithSections getRadio(
			cz.cuni.mff.been.jaxb.config.Item jaxbItem,
			Configuration configuration,
			Type type) {

		ArrayList<RadioWithSectionItem> items = new ArrayList<RadioWithSectionItem>();
		RadioWithSectionItem item;
		for(Value value : type.getValue()){
			item = new RadioWithSectionItem(
						new Option(value.getName(), value.getDesc()),
						null);
			if( value.isSetGroup() ){
				item.setSection(
						ScreenTranscoder.sectionFromJaxbConfig(this, value.getGroup(), configuration) ); 
			}
			items.add(item);
		}

		int selected = 0;
		String[] values = configuration != null ? configuration.get(jaxbItem.getId()) : null;
		if( values != null && values.length > 0 ){
			int i = 0;
			for (RadioWithSectionItem it : items) {
				if( it.getOption().getId().equals(values[0]) ){
					selected = i;
					break;
				}
				i++;
			}
		}

		return new RadiosWithSections(
				jaxbItem.getId(),
				items.toArray(new RadioWithSectionItem[0]),
				selected);
	}

}
