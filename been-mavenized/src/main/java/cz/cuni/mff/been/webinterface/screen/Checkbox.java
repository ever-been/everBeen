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
package cz.cuni.mff.been.webinterface.screen;

/**
 * @author Jiri Tauber
 *
 */
public class Checkbox extends Item {

	private static final long serialVersionUID = 5879209997385695037L;

	public static final String VALUE_CHECKED = "true";
	public static final String VALUE_UNCHECKED = "false";

	/** Checkbox state */
	private boolean checked;

	/**
	 * @param id
	 * @param label
	 * @param checked
	 */
	public Checkbox(String id, String label, boolean checked) {
		super(id, label);
		this.checked = checked;
	}

	public boolean isChecked(){
		return checked;
	}

	public void setChecked(boolean checked){
		this.checked = checked;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues(){
		return new String[]{checked ? VALUE_CHECKED : VALUE_UNCHECKED};
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValue()
	 */
	@Override
	public String getValue() {
		return checked ? VALUE_CHECKED : VALUE_UNCHECKED;
	}
}
