/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.webinterface.config;

import javax.servlet.ServletContext;

import cz.cuni.mff.been.jaxb.config.Item;

/**
 * This interface is implemented by classes that can convert configuration items to their
 * XHTML representation.
 * 
 * @author Andrej Podzimek
 */
public interface ItemToXHTML {

	/**
	 * Configuration item to XHTML converter.
	 * 
	 * @param application Servlet context this request runs in.
	 * @param prefix A prefix used to generate form imput names from item names.
	 * @param item The configuration item right from the JAXB parser.
	 * @return A string of XHTML elements that can be used inside forms.
	 */
	String toXHTML( ServletContext application, String prefix, Item item );
}
