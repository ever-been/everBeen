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
package cz.cuni.mff.been.softwarerepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Date metadata attribute helper class.
 * 
 * @author David Majda
 */
public class DateAttributeHelper extends AttributeHelper<Date> {
	/** Date format pattern used to read/write date in RFC 1123 format. */
	private static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	
	/** Class instance (singleton pattern). */
	private static DateAttributeHelper instance;

	/**
	 * Allocates a new <code>DateAttributeHelper</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private DateAttributeHelper() {
		super();
	}
	
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#validateInXML(org.w3c.dom.Element)
	 */
	@Override
	public String validateInXML(Element element) {
		String value = extractTextValueFromElement(element);
		if (value == null) {
			return "Invalid value of element <" + element.getNodeName() + ">.";
		}
		
		try {
	    SimpleDateFormat format = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
			format.parse(extractTextValueFromElement(element));
		} catch (ParseException e) {
			return "Invalid value of element <" + element.getNodeName() + ">.";
		}
		return null;
	}
	
	/**
	 * @see cz.cuni.mff.been.softwarerepository.AttributeHelper#readValueFromElement(org.w3c.dom.Element)
	 */
	@Override
	public Date readValueFromElement(Element element) {
		try {
	    SimpleDateFormat format = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
			return format.parse(extractTextValueFromElement(element));
		} catch (ParseException e) {
			assert false: "Date should be checked in this moment, parsing can't fail.";
			return null;
		}
	}
	
	@Override
	public Element writeValueToElement(Document document, String tagName, Date value) {
    SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    if (value != null) {
			Element result = document.createElement(tagName);
			result.appendChild(document.createTextNode(formatter.format(value)));
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static DateAttributeHelper getInstance() {
		if (instance == null) {
			 instance = new DateAttributeHelper();
		}
		return instance;
	}
}
