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

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.been.common.Version;

/**
 * Container class for the package metadata.
 * 
 * PackageMetadata is intended to be immutable, as
 * it is accessed during querying by the callback class and it shouldn't be possible  
 * for the callback class to alter the metadata.
 * 
 * However, not all class fields (notably hardwarePlatforms, softwarePlaftorms and attributeInfo)
 * are immutable now, so immutability of the whole class isn't guaranteed. 
 * 
 * This behavior should be changed (e.g. by some read-only replacement for ArrayList and Date)
 * if the class is ever passed to someone we don't trust.   
 * 
 * @author David Majda
 */
public class PackageMetadata implements Serializable {

	private static final long	serialVersionUID	= -5555364263925080803L;

	private static Method getGetter(String name) {
		try {
			return PackageMetadata.class.getMethod(name, (Class[]) null);
		} catch (SecurityException e) {
			assert false : "This should never happen.";
			return null;
		} catch (NoSuchMethodException e) {
			assert false : "This should never happen.";
		return null;
		}
	}
	
	/** Information about attributes. */
	public static final AttributeInfo[] ATTRIBUTE_INFO = new AttributeInfo[] {
		new AttributeInfo(
				"name",
				"package name",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				getGetter("getName")
		),
		new AttributeInfo(
				"version", 
				"version",
				Version.class,
				VersionAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				getGetter("getVersion")
		),
		new AttributeInfo(
				"hardwarePlatforms",
				"hardware platforms",
				ArrayList.class,
				ArrayListAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getHardwarePlatforms")
		),
		new AttributeInfo(
				"softwarePlatforms",
				"software platforms",
				ArrayList.class,
				ArrayListAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getSoftwarePlatforms")
		),
		new AttributeInfo(
				"type",
				"package type",
				PackageType.class,
				PackageTypeAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				getGetter("getType")
		),
		new AttributeInfo(
				"humanName",
				"human package name",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
                                        
				),
				getGetter("getHumanName")
		),
		new AttributeInfo(
				"downloadURL",
				"downloaded from",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getDownloadURL")
		),
		new AttributeInfo(
				"downloadDate",
				"download date and time",
				Date.class,
				DateAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.SOURCE,
					PackageType.BINARY,
					PackageType.TASK,
					PackageType.DATA,
                                        PackageType.MODULE
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getDownloadDate")
		),
		new AttributeInfo(
				"sourcePackageFilename",
				"source package",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.BINARY,
					PackageType.TASK,
                                        PackageType.MODULE
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getSourcePackageFilename")
		),
		new AttributeInfo(
				"binaryIdentifier",
				"binary identifier",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.BINARY
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getBinaryIdentifier")
		),
		new AttributeInfo(
				"buildConfiguration",
				"build configuration name",
				String.class,
				StringAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.BINARY
				),
				EnumSet.noneOf(PackageType.class),
				getGetter("getBuildConfiguration")
		),
		new AttributeInfo(
				"providedInterfaces",
				"interfaces provided by pluggable module",
				ArrayList.class,
				ArrayListAttributeHelper.getInstance(),
				EnumSet.of(
					PackageType.MODULE
				),
				EnumSet.of(
						PackageType.MODULE
				),
				getGetter("getProvidedInterfaces")
		),
	};
		
	/** Filename of the package in the Software Repository. */
	private String filename;
	/** Size of the package file. */
	private long size;
	/** Canonical name of the package. */
	private String name;
	/** Package version. */
	private Version version;
	/** Hardware platforms. */
	private ArrayList< ? > hardwarePlatforms;
	/** Software platforms. */
	private ArrayList< ? > softwarePlatforms;   
	/** Determines package type. */
	private PackageType type;  
	/** Human-readable package name. */
	private String humanName;   
	/** Source of this package, if downloaded from some external site. */
	private String downloadURL; 
	/** Download date and time, if downloaded from some external site. */
	private Date downloadDate;    
	/** Filename of the source package in Software Repository. */
	private String sourcePackageFilename;
	/** BID of the binary in experiment, which this package is used in. */
	private String binaryIdentifier;
	/** Name of the build configuration, in which this package was built. */
	private String buildConfiguration;
	/** List of interfaces provided by pluggable module */
	private ArrayList< ? > providedInterfaces;
	
	/** @return the filename */
	public String getFilename() {
		return filename;
	}
	
	/** @return the size */
	public long getSize() {
		return size;
	}

	/** @return the name */
	public String getName() {
		return name;
	}
	
	/** @return the version */
	public Version getVersion() {
		return version;
	}
	
	/** @return the hardwarePlatforms */
	public ArrayList< ? > getHardwarePlatforms() {
		return hardwarePlatforms;
	}
	
	/** @return the softwarePlatform */
	public ArrayList< ? > getSoftwarePlatforms() {
		return softwarePlatforms;
	}
	
	/** @return the type */
	public PackageType getType() {
		return type;
	}
	
	/** @return the humanName */
	public String getHumanName() {
		return humanName;
	}
	
	/** @return the downloadURL */
	public String getDownloadURL() {
		return downloadURL;
	}
	
	/** @return the downloadDate */
	public Date getDownloadDate() {
		return downloadDate;
	}
	
	/** @return the sourcePackageFilename */
	public String getSourcePackageFilename() {
		return sourcePackageFilename;
	}
	
	/** @return the binaryIdentifier */
	public String getBinaryIdentifier() {
		return binaryIdentifier;
	}

	/** @return the buildConfiguration */
	public String getBuildConfiguration() {
		return buildConfiguration;
	}
	
	/** @return the buildConfiguration */
	public ArrayList< ? > getProvidedInterfaces() {
		return providedInterfaces;
	}

	/**
	 * Allocates a new <code>PackageMetadata</code> object.
	 *
	 * @param filename filename of the package in the Software Repository
	 * @param size size of the package file
	 * @param name canonical name of the package
	 * @param version package version
	 * @param hardwarePlatforms hardware platforms
	 * @param softwarePlatforms software platforms
	 * @param type determines package type
	 * @param humanName human-readable package name
	 * @param downloadURL source of this package, if downloaded from some external site
	 * @param downloadDate download date and time, if downloaded from some external site
	 * @param sourcePackageFilename filename of the source package in the Software Repository
	 * @param binaryIdentifier BID of the binary in experiment, which this package is used in
	 * @param buildConfiguration name of the build configuration, in which this package was built
	 */
	public PackageMetadata(String filename, long size, String name,
			Version version, ArrayList< ? > hardwarePlatforms, ArrayList< ? > softwarePlatforms,
			PackageType type, String humanName, String downloadURL,
			Date downloadDate, String sourcePackageFilename, String binaryIdentifier,
			String buildConfiguration, ArrayList< ? > providedInterfaces) {
		super();
		this.filename = filename;
		this.size = size;
		this.name = name;
		this.version = version;
		this.hardwarePlatforms = hardwarePlatforms;
		this.softwarePlatforms = softwarePlatforms;
		this.type = type;
		this.humanName = humanName;
		this.downloadURL = downloadURL;
		this.downloadDate = downloadDate;
		this.sourcePackageFilename = sourcePackageFilename;
		this.binaryIdentifier = binaryIdentifier;
		this.buildConfiguration = buildConfiguration;
		this.providedInterfaces = providedInterfaces;
	}
	
	
	/**
	 * Necessary for creating instances in the web interface. 
	 */
	public PackageMetadata() {
		super();
	}
	
	private void appendIfNotNull(Element parent, Element child) {
		if (child != null) {
			parent.appendChild(child);
		}
	}
	
	/**
	 * Saves the metadata to a file. 
	 * 
	 * @param metadataFile file, where the metadata should be saved
	 * @throws TransformerException if an unrecoverable error occurs during the
	 *          course of the transformation
	 */
	public void saveToFile(String metadataFile) throws TransformerException {
		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			assert false: "ParserConfigurationException thrown where it should be not.";
			return;
		}
		
		Element rootElement = document.createElement("package");
		document.appendChild(rootElement);
		
		appendIfNotNull(rootElement,
			StringAttributeHelper.getInstance().writeValueToElement(document, "name", name)
		);
		appendIfNotNull(rootElement,
			VersionAttributeHelper.getInstance().writeValueToElement(document, "version", version)
		);
		appendIfNotNull(rootElement,
			ArrayListAttributeHelper.getInstance().writeValueToElement(document, "hardwarePlatforms", hardwarePlatforms)
		);
		appendIfNotNull(rootElement,
			ArrayListAttributeHelper.getInstance().writeValueToElement(document, "softwarePlatforms", softwarePlatforms)
		);
		appendIfNotNull(rootElement,
			PackageTypeAttributeHelper.getInstance().writeValueToElement(document, "type", type)
		);
		appendIfNotNull(rootElement,
			StringAttributeHelper.getInstance().writeValueToElement(document, "humanName", humanName)
		);
		appendIfNotNull(rootElement,
			StringAttributeHelper.getInstance().writeValueToElement(document, "downloadURL", downloadURL)
		);
		appendIfNotNull(rootElement,
			DateAttributeHelper.getInstance().writeValueToElement(document, "downloadDate", downloadDate)
		);
		appendIfNotNull(rootElement,
			StringAttributeHelper.getInstance().writeValueToElement(document, "sourcePackageFilename", sourcePackageFilename)
		);
		appendIfNotNull(rootElement,
			StringAttributeHelper.getInstance().writeValueToElement(document, "binaryIdentifier", binaryIdentifier)
		);
		appendIfNotNull(rootElement,
				StringAttributeHelper.getInstance().writeValueToElement(document, "buildConfiguration", buildConfiguration)
			);
		appendIfNotNull(rootElement,
				ArrayListAttributeHelper.getInstance().writeValueToElement(document, "providedInterfaces", providedInterfaces)
			);
		
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			assert false: "TransformerConfigurationException thrown where it should be not.";
			return;
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(new DOMSource(document), new StreamResult(new File(metadataFile)));
	}
}
