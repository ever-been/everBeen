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

/**
 * Represents a static text control in the plugin configuration wizard screen.
 * 
 * Static text control displays a non-editable text next to the label.
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 */
public class StaticText extends Item {

	private static final long	serialVersionUID	= -8122646148114057857L;

	/** Static control text. */ 
	private String text;

	/** @return static control text */
	public String getText() {
		return text;	
	}

	/**
	 * Allocates a new <code>StaticText</code> object.
	 * 
	 * @param id static text identifier
	 * @param label static control label
	 * @param text  static control text
	 */
	public StaticText(String id, String label, String text) {
		super(id, label);
		this.text = text;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return text;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		return new String[]{text};
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValue()
	 */
	@Override
	public String getValue() {
		return text;
	}
}
