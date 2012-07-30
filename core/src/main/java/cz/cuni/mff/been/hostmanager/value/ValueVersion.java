/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.value;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.Version;

import cz.cuni.mff.been.hostmanager.InputParseException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * Class which represents version of software. Version is stored as triplet of numbers and some 
 * free form string.<br>
 * <br>
 * Version should contain at least one version number (major version) but at most 3 version numbers 
 * (major, minor and release numbers) and can contain free form string with more precise description 
 * ("beta", "rc3", etc.). This free-form string should be called "build identification string".<br>
 * Numbers in version string should be separated with "." (period) character. Build identification 
 * string should be separated from the build numbers by "-" (dash) or "_" (underscore) character. 
 * It should contain only non-capital letters and numbers. Words in build id string should be 
 * separated by "-" (dash).
 * 
 * Valid examples:<br>
 * <br>
 * 1.0.5-rc1 is represented as major=1, minor=0, release=5, buildID="-rc1"<br>
 * 1.0-20050404 is represented as major=1, minor=0, release=0, buildID="-20050404"<br>
 * <br>
 * ValueVersion class is also able to handle "non-standard" version representations. For example "1.0a" 
 * is represented as 1.0.0a (WITHOUT - or _!).
 *
 * @author Branislav Repcek
 *
 */
public class ValueVersion 
	implements ValueBasicInterface< ValueVersion >, Serializable, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= -4558219241825107398L;

	/**
	 * Number of digits numerical fields will be aligned when converting to string.
	 */
	public static final int NUMERIC_ALIGNMENT = 4;
	
	/**
	 * Major version number.
	 */
	private int major;
	
	/**
	 * Minor version number.
	 */
	private int minor;
	
	/**
	 * Release number.
	 */
	private int release;
	
	/**
	 * Build identification string (beta, r2...).
	 */
	private String buildIdentifier;

	/**
	 * Read ValueVersion from the XML node.
	 * 
	 * @param node Node with version data.
	 * 
	 * @throws InputParseException If there was an error while parsing input.
	 */
	public ValueVersion(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/**
	 * Allocate empty <code>ValueVersion</code> value (all fields are set to 0).
	 */
	public ValueVersion() {
		
		major = minor = release = 0;
		buildIdentifier = null;
	}
	
	/**
	 * Initialise <code>ValueVersion</code> from <code>Version</code>.
	 * @param ver
	 */
	public ValueVersion(Version ver) {
	
		parseString(ver.toString());
	}
	
	/**
	 * Create new <code>ValueVersion</code>.
	 * 
	 * @param majorVer Major version number.
	 * @param minorVer Minor version number.
	 * @param releaseNumber Release number.
	 * @param buildId Build identification string.
	 */
	public ValueVersion(int majorVer, int minorVer, int releaseNumber, String buildId) {
		
		major = majorVer;
		minor = minorVer;
		release = releaseNumber;
		buildIdentifier = buildId;
	}
	
	/**
	 * Create new <code>ValueVersion</code> with empty buil identification string.
	 * 
	 * @param majorVer Major version number.
	 * @param minorVer Minor version number.
	 * @param releaseNumber Release number.
	 */
	public ValueVersion(int majorVer, int minorVer, int releaseNumber) {

		major = majorVer;
		minor = minorVer;
		release = releaseNumber;
		buildIdentifier = "";
	}
	
	/**
	 * Create new <code>ValueVersion</code> from string.
	 * 
	 * @param versionString String to convert to version value.
	 */
	public ValueVersion(String versionString) {
		if (null == versionString || "(unknown)".equals(versionString)) {
			buildIdentifier = null;
			major = minor = release = 0;
		} else {
			parseString(versionString);
		}
	}
	
	/**
	 * Converts a String to a version string. Useful for JAXB binding.
	 * 
	 * @param versionString String to convert to version value.
	 * @return An instance of ValueVersion for the {@code versionString}.
	 */
	public static ValueVersion fromString(String versionString) {
		return new ValueVersion(versionString);
	}
	
	/**
	 * Set value of this ValueVersion according to the string.
	 * 
	 * @param versionString String containing version.
	 */
	private void parseString(String versionString) {
		
		int strPos = 0;
		int strBeg = 0;
		
		major = minor = release = 0;
		buildIdentifier = "";
		
		// major version
		while ((strPos < versionString.length())
		       && ("0123456789".indexOf(versionString.charAt(strPos)) != -1)) {
			++strPos;
		}
		
		if (strPos != 0) {
			major = Integer.valueOf(versionString.substring(0, strPos)).intValue();
			
			if (strPos >= versionString.length()) {
				return;
			}
		} else {
			buildIdentifier = versionString;
			return;
		}
		
		if (versionString.charAt(strPos) == '.') {
			strBeg = ++strPos;
		} else {
			buildIdentifier = versionString.substring(strPos);
			return;
		}
		
		// minor version
		while ((strPos < versionString.length())
		       && ("0123456789".indexOf(versionString.charAt(strPos)) != -1)) {
			++strPos;
		}

		if (strPos > strBeg) {
			minor = Integer.valueOf(versionString.substring(strBeg, strPos)).intValue();
			
			if (strPos >= versionString.length()) {
				return;
			}
		} else {
			buildIdentifier = versionString.substring(strBeg);
			return;
		}
		
		if (versionString.charAt(strPos) == '.') {
			strBeg = ++strPos;
		} else {
			buildIdentifier = versionString.substring(strPos);
			return;
		}
		
		// release
		while ((strPos < versionString.length())
		       && ("0123456789".indexOf(versionString.charAt(strPos)) != -1)) {
			++strPos;
		}

		if (strPos > strBeg) {
			release = Integer.valueOf(versionString.substring(strBeg, strPos)).intValue();
			
			if (strPos >= versionString.length()) {
				return;
			}
		} else {
			buildIdentifier = versionString.substring(strBeg);
			return;
		}
		
		// rest of string is build id
		buildIdentifier = versionString.substring(strPos);		
	}
	
	/**
	 * Return sign of the integer.
	 * 
	 * @param x Value to test.
	 * @return Returns -1, 0, or 1 as given parameter is less than, equal to, or greater than zero. 
	 */
	private int sgn(int x) {
		
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/*
	 * @see java.lang.Comparable#compareTo
	 */
	public int compareTo(ValueVersion ver) {

		if (major > ver.major) {
			return 1;
		} else if (major < ver.major) {
			return -1;
		} else {
			if (minor > ver.minor) {
				return 2;
			} else if (minor < ver.minor) {
				return -2;
			} else {
				if (release > ver.release) {
					return 3;
				} else if (release < ver.release) {
					return -3;
				} else {
					return 4 * sgn(buildIdentifier.compareTo(ver.buildIdentifier));
				}
			}
		}
	}
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof ValueVersion) {
			return equals((ValueVersion) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare specified <code>ValueVersion</code> with this value for equality. Two versions are equal iff
	 * all their version numbers and build IDs are equal.
	 * 
	 * @param ver Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code> otherwise.
	 */
	public boolean equals(ValueVersion ver) {
		
		return (major == ver.major) && (minor == ver.minor) && (release == ver.release)
		       && (ver.buildIdentifier.equals(buildIdentifier));
	}

	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		return toString().hashCode();
	}

	/**
	 * Create textual representation of version. All numerical fields in the result will be aligned to
	 * <code>numericalAlignment</code> digits by zeroes from the left. Fields in version are separated
	 * by dot character.
	 * 
	 * @return String containing textual representation of version.
	 */
	public String toStringAligned() {
		
		if (buildIdentifier != null) {
			return align(major) + "." + align(minor) + "." + align(release) + buildIdentifier;
		} else {
			return "(unknown)";
		}
	}

	/*
	 * @see java.lang.Object#toString
	 */
	@Override
	public String toString() {
		
		if (buildIdentifier != null) {
			return Integer.toString(major) + "." 
			       + Integer.toString(minor) + "." 
			       + Integer.toString(release)
			       + buildIdentifier;
		} else {
			return "(unknown)";
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#greaterThan
	 */
	public boolean greaterThan(Object o) {
		
		if (o instanceof ValueVersion) {
			return greaterThan((ValueVersion) o);
		} else {
			return false;
		}
	}

	/**
	 * Test if current value is greater than specified one.
	 * 
	 * @param v Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has greater value than given value, 
	 *         <code>false</code> otherwise.
	 */
	public boolean greaterThan(ValueVersion v) {
		
		if (major > v.major) {
			return true;
		} else if (major == v.major) {
			if (minor > v.minor) {
				return true;
			} else if (minor == v.minor) {
				if (release > v.release) {
					return true;
				} else if (release == v.release) {
					if (buildIdentifier.compareTo(v.buildIdentifier) > 0) {
						return true;
					}
				}
			}
		}
		
		return false; 
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#lessThan
	 */
	public boolean lessThan(Object o) {
		
		if (o instanceof ValueVersion) {
			return lessThan((ValueVersion) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Test if current value is less than specified one.
	 * 
	 * @param v Value to compare to.
	 * 
	 * @return <code>true</code> if this instance has lower value than given value, 
	 *         <code>false</code> otherwise.
	 */
	public boolean lessThan(ValueVersion v) {
		
		if (major < v.major) {
			return true;
		} else if (major == v.major) {
			if (minor < v.minor) {
				return true;
			} else if (minor == v.minor) {
				if (release < v.release) {
					return true;
				} else if (release == v.release) {
					if (buildIdentifier.compareTo(v.buildIdentifier) < 0) {
						return true;
					}
				}
			}
		}
		
		return false; 
	}
	
	/**
	 * Convert number to string and fill with zeroes from right to create string with length of 
	 * <code>numericalAlignment</code> characters.
	 * 
	 * @param val Integer to convert.
	 * @return String with given number, result is at least <code>numericalAlignment</code> characters long.
	 */
	private static String align(int val) {
		
		String res = String.valueOf(val);
		
		// add 0 on the left
		while (res.length() < NUMERIC_ALIGNMENT) {
			res = "0" + res;
		}
		
		return res;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {
		
		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain version data. Node name is \""
					+ node.getNodeName() + "\".");
		}
		
		parseString(XMLHelper.getAttributeValueByName("value", node));
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document document) {
		
		/* Resulting node
		 * 
		 * <version value="<version>"/>
		 * 
		 * where <version> is value of this class converted to the string using toString method.
		 */
		
		Element element = document.createElement(getXMLNodeName());
		
		element.setAttribute("value", this.toString());
		
		return element;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "version";
	}

	/**
	 * @return Build identification string.
	 */
	public String getBuildIdString() {
		return buildIdentifier;
	}

	/**
	 * @param buildId New build identification string.
	 */
	public void setBuildIdString(String buildId) {
		
		this.buildIdentifier = buildId;
	}

	/**
	 * @return Major version number.
	 */
	public int getMajorVersion() {
		
		return major;
	}

	/**
	 * @param major New major version number.
	 * 
	 * @throws IllegalArgumentException If given version number is negative. 
	 */
	public void setMajorVersion(int major) throws IllegalArgumentException {
		
		if (major < 0) {
			throw new IllegalArgumentException("Negative version number is not allowed.");
		}
		
		this.major = major;
	}

	/**
	 * @return Minor version number.
	 */
	public int getMinorVersion() {
		
		return minor;
	}

	/**
	 * @param minor New minor version number.
	 * 
	 * @throws IllegalArgumentException If given version number is negative.
	 */
	public void setMinorVersion(int minor) throws IllegalArgumentException {
		
		if (minor < 0) {
			throw new IllegalArgumentException("Negative version number is not allowed.");
		}
		
		this.minor = minor;
	}

	/**
	 * @return Release number.
	 */
	public int getReleaseNumber() {
		return release;
	}

	/**
	 * @param release New release number.
	 * 
	 * @throws IllegalArgumentException If given release number is negative.
	 */
	public void setReleaseNumber(int release) throws IllegalArgumentException {
		
		if (release < 0) {
			throw new IllegalArgumentException("Negative version number is not allowed.");
		}
		
		this.release = release;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.value.ValueBasicInterface#getUnit()
	 */
	public String getUnit() {
		
		return null;
	}

	@Override
	public ValueType getType() {
		
		return ValueType.VERSION;
	}

	@Override
	public ValueType getElementType() {
		
		return getType();
	}
}
