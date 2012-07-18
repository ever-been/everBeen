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

package cz.cuni.mff.been.hostmanager.database;

import java.io.Serializable;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.SubstituteVariableValues;

import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This class contains conditions application must meet before alias which represents given application
 * can be created. Alias definitions are processed by the <code>HostDatabaseEngine</code> when host
 * is added and list of corresponding <code>SoftwareAlias</code> classes is added to the properties 
 * of the host.
 * <br>
 * You can specify also restrictions on OperatingSystem (which are checked before applications installed
 * on the host are tested).
 * <br>
 * Resulting alias will have 4 properties - name of the alias, name of the application it represents,
 * version of the application and application's vendor. You can define value of each property. In 
 * definition you can use variables which represent properties of the application that matched
 * conditions specified in the alias definition. Variables will then be substituted with value they 
 * from the application. Allowed variables are <i>${name}</i> (name of the application), 
 * <i>${vendor}</i> (application's vendor) and <i>${version}</i> (application version). Use of variables is
 * not limited in any way, you can use any of them in any property of the result. Note that variable
 * names are case sensitive.
 * 
 * @see cz.cuni.mff.been.hostmanager.database.SoftwareAlias
 *
 * @author Branislav Repcek
 */
public class SoftwareAliasDefinition implements XMLSerializableInterface, Serializable {

	private static final long	serialVersionUID	= 8581555971921137240L;

	/**
	 * XML node name.
	 */
	public static final String XML_NODE_NAME = "aliasDefinition";
	
	/**
	 * Name of the resulting alias.
	 */
	private String aliasName;
	
	/**
	 * Restriction for the operating system.
	 */
	private RestrictionInterface osRestriction;
	
	/**
	 * Restriction for application.
	 */
	private RestrictionInterface appRestriction;
	
	/**
	 * Resulting app name.
	 */
	private String resultName;
	
	/**
	 * Resulting version.
	 */
	private String resultVersion;
	
	/**
	 * Resulting vendor.
	 */
	private String resultVendor;
	
	/**
	 * Create new alias definition.
	 * 
	 * @param aliasName Name of the alias.
	 * @param resultName Name that will appear in the resulting <code>SoftwareAlias</code>. This 
	 *        parameter is required and cannot be <code>null</code> or empty string. Note that alias
	 *        names do not need to be unique.
	 * @param resultVendor Vendor that will appear in the resulting <code>SoftwareAlias</code>. This
	 *        can be <code>null</code> or empty string.
	 * @param resultVersion Version that will appear in the resulting <code>SoftwareAlias</code>. 
	 *        This can be <code>null</code>or empty string.
	 * @param osRestriction Condition on the operating system. Set this to <code>null</code> to
	 *        disable operating system test (that is, every OS will pass). Path to the object in 
	 *        restriction has to be empty, since only local properties of the os object can be tested.
	 * @param appRestriction Condition on the application that this alias represents. This is required
	 *        parameter and cannot be <code>null</code>. Path to the object in restriction has to be
	 *        empty, since only local properties of the application object can be tested.
	 * 
	 * @throws InvalidArgumentException If some of the required arguments is <code>null</code> or 
	 *         empty string or if there are syntax errors in definition..
	 */
	public SoftwareAliasDefinition(String aliasName, String resultName, String resultVendor, 
			String resultVersion, ObjectRestriction osRestriction, ObjectRestriction appRestriction) 
			throws InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(aliasName, "aliasName");
		MiscUtils.verifyStringParameterBoth(resultName, "resultName");
		MiscUtils.verifyParameterIsNotNull(appRestriction, "appRestriction");
		
		if ((osRestriction != null) && (!osRestriction.getObjectPath().equals(""))) {
			throw new InvalidArgumentException("Object path for OS restriction has to be empty string.");
		}
		
		if (!appRestriction.getObjectPath().equals("")) {
			throw new InvalidArgumentException("Object path in restriction for application"
					+ " is not empty.");
		}
		
		this.aliasName = aliasName;
		this.resultName = resultName;
		this.resultVendor = resultVendor;
		this.resultVersion = resultVersion;
		
		validateDefinitionHeader();
		
		this.osRestriction = osRestriction;
		this.appRestriction = appRestriction;
	}
	
	/**
	 * Create new alias definition.
	 * 
	 * @param aliasName Name of the alias.
	 * @param resultName Name that will appear in the resulting <code>SoftwareAlias</code>. This 
	 *        parameter is required and cannot be <code>null</code> or empty string. Note that alias
	 *        names do not need to be unique.
	 * @param resultVendor Vendor that will appear in the resulting <code>SoftwareAlias</code>. This
	 *        can be <code>null</code> or empty string.
	 * @param resultVersion Version that will appear in the resulting <code>SoftwareAlias</code>. 
	 *        This can be <code>null</code>or empty string.
	 * @param osRestriction Condition on the operating system. Set this to <code>null</code> to
	 *        disable operating system test (that is, every OS will pass).
	 * @param appRestriction Condition on the application that this alias represents. This is required
	 *        parameter and cannot be <code>null</code>.
	 * 
	 * @throws InvalidArgumentException If some of the required arguments is <code>null</code> or 
	 *         empty string or if there are syntax errors in definition.
	 */
	public SoftwareAliasDefinition(String aliasName, String resultName, String resultVendor,
			String resultVersion, RSLRestriction osRestriction, RSLRestriction appRestriction)
			throws InvalidArgumentException {

		MiscUtils.verifyStringParameterBoth(aliasName, "aliasName");
		MiscUtils.verifyStringParameterBoth(resultName, "resultName");
		MiscUtils.verifyParameterIsNotNull(appRestriction, "appRestriction");
	
		this.aliasName = aliasName;
		this.resultName = resultName;
		this.resultVendor = resultVendor;
		this.resultVersion = resultVersion;
		
		validateDefinitionHeader();
		
		this.osRestriction = osRestriction;
		this.appRestriction = appRestriction;
	}
	
	/**
	 * Create new alias definition from the XML file node.
	 * 
	 * @param node Node containing data.
	 * 
	 * @throws InputParseException If there was an error parsing data.
	 */
	public SoftwareAliasDefinition(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	public Element exportAsElement(Document document) {
		
		/*
		 * Resulting node:
		 * 
		 * <aliasDefinition alias=<aliasName> name=<resultName> vendor=<resultVendor> 
		 *   version=<resultVersion> >
		 *     <os type="<type>">
		 *       <operating system restriction/>
		 *     </os>
		 *     <app type="<type>">
		 *       <application restriction/>
		 *     </app>
		 * </aliasDefinition>
		 * 
		 * where type is canonical name of the runtime type of the restriction
		 */
		
		Element element = document.createElement(getXMLNodeName());
		
		element.setAttribute("alias", aliasName);
		element.setAttribute("name", resultName);
		
		if (resultVendor != null) {
			element.setAttribute("vendor", resultVendor);
		}
		
		if (resultVersion != null) {
			element.setAttribute("version", resultVersion);
		}
		
		if (osRestriction != null) {
			Element osRestr = document.createElement("os");
			
			element.appendChild(osRestr);
			osRestr.setAttribute("type", osRestriction.getClass().getCanonicalName());
			
			osRestr.appendChild(osRestriction.exportAsElement(document));
		}
		
		Element appRestr = document.createElement("app");
		element.appendChild(appRestr);
		appRestr.setAttribute("type", appRestriction.getClass().getCanonicalName());
		appRestr.appendChild(appRestriction.exportAsElement(document));
		
		return element;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		
		return XML_NODE_NAME;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	//@SuppressWarnings("null")
	public void parseXMLNode(Node node) throws InputParseException {
		
		aliasName = XMLHelper.getAttributeValueByName("alias", node);
		resultName = XMLHelper.getAttributeValueByName("name", node);
		
		if (XMLHelper.hasAttribute("version", node)) {
			resultVersion = XMLHelper.getAttributeValueByName("version", node);
		} else {
			resultVersion = null;
		}
		
		if (XMLHelper.hasAttribute("vendor", node)) {
			resultVendor = XMLHelper.getAttributeValueByName("vendor", node);
		} else {
			resultVendor = null;
		}
		
		Node osNode;
		
		try {
			osNode = XMLHelper.getSubNodeByName("os", node);
		} catch (InputParseException e) {
			// no os node, not an error
			osNode = null;
		}
		
		if (osNode != null) {
			String restrictionType = XMLHelper.getAttributeValueByName("type", osNode);

			// this is kind of stupid, but using Java's reflection here is waaaay too long
			if (restrictionType.equals(RSLRestriction.class.getCanonicalName())) {
				Node n = XMLHelper.getSubNodeByName(RSLRestriction.XML_NODE_NAME, osNode);
				
				osRestriction = new RSLRestriction(n);
			} else if (restrictionType.equals(ObjectRestriction.class.getCanonicalName())) {
				Node n = XMLHelper.getSubNodeByName(ObjectRestriction.XML_NODE_NAME, osNode);
				
				osRestriction = new ObjectRestriction(n);
			} else {
				throw new InputParseException("Unknown restriction type \"" + restrictionType + "\".");
			}
		} else {
			osRestriction = null;
		}
		
		Node appNode = XMLHelper.getSubNodeByName("app", node);
		String restrictionType = XMLHelper.getAttributeValueByName("type", appNode);

		if (restrictionType.equals(RSLRestriction.class.getCanonicalName())) {
			Node n = XMLHelper.getSubNodeByName(RSLRestriction.XML_NODE_NAME, appNode);
			
			appRestriction = new RSLRestriction(n);
		} else if (restrictionType.equals(ObjectRestriction.class.getCanonicalName())) {
			Node n = XMLHelper.getSubNodeByName(ObjectRestriction.XML_NODE_NAME, appNode);
			
			appRestriction = new ObjectRestriction(n);
		} else {
			throw new InputParseException("Unknown restriction type \"" + restrictionType + "\".");
		}
		
		try {
			validateDefinitionHeader();
		} catch (Exception e) {
			throw new InputParseException("Error parsing definition: " + e.getMessage(), e);
		}
	}

	/**
	 * @return Name of the alias.
	 */
	public String getAliasName() {
		return aliasName;
	}

	/**
	 * @param aliasName Name of the alias.
	 */
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	/**
	 * @return Restriction for the application this alias represents.
	 */
	public RestrictionInterface getAppRestriction() {
		return appRestriction;
	}

	/**
	 * @param appRestriction Restriction for the application this alias represents.
	 */
	public void setAppRestriction(RestrictionInterface appRestriction) {
		this.appRestriction = appRestriction;
	}

	/**
	 * @return Operating system restriction.
	 */
	public RestrictionInterface getOsRestriction() {
		return osRestriction;
	}

	/**
	 * @param osRestriction Operating system restriction.
	 */
	public void setOsRestriction(RestrictionInterface osRestriction) {
		this.osRestriction = osRestriction;
	}

	/**
	 * @return Product name from the alias.
	 */
	public String getResultName() {
		return resultName;
	}

	/**
	 * @param resultName String which will be displayed as the product's name.
	 */
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	/**
	 * @return Vendor of the product this alias represents.
	 */
	public String getResultVendor() {
		return resultVendor;
	}

	/**
	 * @param resultVendor String which will be displayed as a vendor of the product.
	 */
	public void setResultVendor(String resultVendor) {
		this.resultVendor = resultVendor;
	}

	/**
	 * @return Version of the product this alias represents.
	 */
	public String getResultVersion() {
		return resultVersion;
	}

	/**
	 * @param resultVersion the resultVersion to set
	 */
	public void setResultVersion(String resultVersion) {
		this.resultVersion = resultVersion;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return resultVersion.hashCode() + 31 * resultName.hashCode() + 967 * resultVendor.hashCode()
		       + 29789 * aliasName.hashCode() + 923467 * osRestriction.hashCode()
		       + 28627493 * appRestriction.hashCode();
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof SoftwareAliasDefinition) {
			SoftwareAliasDefinition t = (SoftwareAliasDefinition) o;
			
			return aliasName.equals(t.aliasName) && resultVersion.equals(t.resultVersion)
			       && resultVendor.equals(t.resultVendor) && resultName.equals(t.resultName)
			       && osRestriction.equals(t.osRestriction) && appRestriction.equals(t.appRestriction);
		} else {
			return false;
		}
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "{" + aliasName + "|" + resultName + ";" + resultVendor + ";" + resultVersion + "}";
	}
	
	/**
	 * Validate string used in alias definition. This will check if they have correct format and
	 * use only allowed variables.
	 * 
	 * @throws InvalidArgumentException If some string is invalid.
	 */
	private void validateDefinitionHeader() throws InvalidArgumentException {
		
		String []allowedNames = new String[] {
				"version",
				"name",
				"vendor"
			};
		
		ValidatingDataProvider provider = new ValidatingDataProvider(allowedNames);
		SubstituteVariableValues substitutor = new SubstituteVariableValues("[\\p{Alpha}_]+");
		
		try {
			substitutor.parseString(aliasName, provider);
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid alias name in definition: "
					+ e.getMessage(), e);
		}

		try {
			substitutor.parseString(resultName, provider);
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid result name in alias definition: "
					+ e.getMessage(), e);
		}
	
		try {
			substitutor.parseString(resultVendor, provider);
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid result vendor in alias definition: "
					+ e.getMessage(), e);
		}
	
		try {
			substitutor.parseString(resultVersion, provider);
		} catch (Exception e) {
			throw new InvalidArgumentException("Invalid result version in alias definition: "
					+ e.getMessage(), e);
		}
	}
	
	/**
	 * This class is used to verify if only certain variables are used during variable substitution.
	 * Instance contains list of allowed variable names. For any other name, query will fail.
	 *
	 * @author Branislav Repcek
	 */
	private class ValidatingDataProvider 
		implements SubstituteVariableValues.VariableValueProviderInterface< String > {

		private HashSet< String > allowedNames;
		
		/**
		 * Create new data provider.
		 * 
		 * @param allowedVariables Array containing names of allowed variables.
		 */
		public ValidatingDataProvider(String []allowedVariables) {
			
			allowedNames = new HashSet< String >();
			
			for (String c: allowedVariables) {
				allowedNames.add(c);
			}
		}
		
		/*
		 * @see cz.cuni.mff.been.common.SubstituteVariableValues.VariableValueProviderInterface#getValue(java.lang.String)
		 */
		public String getValue(String variableName) {
			
			if (allowedNames.contains(variableName)) {
				
				return "#" + variableName + "#";
			} else {
				return null;
			}
		}
	}
}
