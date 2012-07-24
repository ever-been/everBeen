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

import java.io.Serializable;
import java.util.Arrays;

import cz.cuni.mff.been.common.id.SID;

/**
 * Represents one screen of the plugin configuration wizard. Screens are
 * further divided into sections with titles and descriptions and in those
 * sections there are items, which set the configuration values. 
 * 
 * Each screen in the wizard has unique identifier - <code>SID</code>.
 * 
 * @author David Majda
 * @author Jiri Tauber
 */
public class Screen implements Serializable {

	private static final long	serialVersionUID	= -7344499510076190687L;

	/** Screen identifier. */
	private SID sid;

	/** Screen sections. */
	private Section[] sections;

	/**
	 * Which buttons to show on the template
	 * previous, next, cancel, finish
	 */
	private boolean[] buttons = new boolean[]{true, true, true, false};


	/**
	 * Allocates a new <code>Screen</code> object.
	 *
	 * @param sid screen identifier
	 * @param sections screen sections
	 */
	public Screen(SID sid, Section[] sections) {
		this.sid = sid;

		this.sections = sections;
		for (int i = 0; i < this.sections.length; i++) {
			this.sections[i].setParent(null);
			this.sections[i].setIndex(i);
		}
	}

	/** @return screen identifier */
	public SID getSid() {
		return sid;
	}

	/** @return screen sections */
	public Section[] getSections() {
		return sections;
	}

	/**
	 * Sets which buttons to show in the page.
	 * @param previous
	 * @param next
	 * @param cancel
	 * @param finish
	 */
	public void setButtons(
			boolean previous,
			boolean next,
			boolean cancel,
			boolean finish){
		buttons[0] = previous;
		buttons[1] = next;
		buttons[2] = cancel;
		buttons[3] = finish;
	}

	public boolean showButtonPrevious(){
		return buttons[0];
	}
	public boolean showButtonNext(){
		return buttons[1];
	}
	public boolean showButtonCancel(){
		return buttons[2];
	}
	public boolean showButtonFinish(){
		return buttons[3];
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Screen(" + sid + "," + Arrays.toString(sections) + ")";
	}
}
