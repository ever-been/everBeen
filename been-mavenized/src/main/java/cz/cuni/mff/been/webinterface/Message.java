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

/**
 * Class representing a message which will be displayed to the user on the web
 * page. Each message has a text and a format, which can be plain text or
 * HTML.
 * 
 * Class is immutable - message text and format can't be changed after
 * creating the instance.
 * 
 * @author David Majda
 */
public class Message {
	/** Format of the message. */
	public enum Format {
		/** Plain text format. */
		TEXT,
		/** HTML format. */
		HTML
	};
	
	/** Message text. */
	private String text;
	/** Message format. */
	private Format format;
	
	/** @return message text */
	public String getText() {
		return text;
	}

	/** @return message format */
	public Format getFormat() {
		return format;
	}
	
	/**
	 * Allocates a new <code>Message</code> object. 
	 * 
	 * @param text message text
	 * @param format message format
	 */
	public Message(String text, Format format) {
		this.text = text;
		this.format = format;
	}
}
