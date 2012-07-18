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

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.common.id.SID;
import cz.cuni.mff.been.webinterface.screen.*;

/**
 * Class used to create {@link Screen} objects from different representations.
 *  
 * @author Jiri Tauber
 */
public class ScreenTranscoder {

	//***** JAXB creation ****************************************************//
	/**
	 * Creates {@link Screen} object from JAXB {@link cz.cuni.mff.been.jaxb.config.Config Config}
	 * representation.
	 * 
	 * @param screenId - id or the created screen
	 * @param jaxbConfig - The JAXB Config representation
	 * @param values - The actual values on the screen - may be null to use defaults 
	 * @return the Screen representation of JAXB configuration
	 */
	public static Screen fromJaxbConfig(
			SID screenId,
			cz.cuni.mff.been.jaxb.config.Config jaxbConfig,
			Configuration values){

		JaxbConfigTranscoder transcoder = new JaxbConfigTranscoder(jaxbConfig);
		Section[] sections = new Section[jaxbConfig.getGroup().size()];
		for (int i = 0; i < sections.length; i++) {
			sections[i] = sectionFromJaxbConfig(transcoder, jaxbConfig.getGroup().get(i), values);
		}
		return new Screen(screenId, sections);
	}

	/**
	 * Converts JAXB Config group into section.
	 * @param transcoder - Initialized JaxbItemTranscoder to use.
	 * @param jaxbGroup - The group to transform
	 * @param values - The actual values on the screen - may be null to use defaults 
	 * @return Screen Section object
	 * @see JaxbConfigTranscoder
	 */
	static Section sectionFromJaxbConfig(
			JaxbConfigTranscoder transcoder,
			cz.cuni.mff.been.jaxb.config.Group jaxbGroup,
			Configuration values){

		Item[] items = new Item[jaxbGroup.getItem().size()];
		for (int i = 0; i < items.length; i++) {
			cz.cuni.mff.been.jaxb.config.Item jaxbItem = jaxbGroup.getItem().get(i); 
			items[i] = transcoder.getItem(jaxbItem, values );
		}
		return new Section(items, jaxbGroup.getDesc(), null);
	}

}
