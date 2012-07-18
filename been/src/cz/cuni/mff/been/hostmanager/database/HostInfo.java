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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueList;
import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueType;


/**
 * HostInfo is data storage for all host related informations collected by detectors.
 * It stores all info about hardware and software installed on host.
 *
 * @author Branislav Repcek
 */
class HostInfo extends PropertyTree
implements Serializable, XMLSerializableInterface, HostInfoInterface {

	private static final long	serialVersionUID	= 7703208309802773360L;

	/**
	 * Name of host.
	 */
	private String hostName;

	/**
	 * Detector identification string.
	 */
	private Detectors detector;

	/**
	 * Date of last check of host.
	 */
	private String checkDate;

	/**
	 * Time of last check of host.
	 */
	private String checkTime;

	/**
	 * Info about host operating system.
	 */
	private OperatingSystem operatingSystem;

	/**
	 * List of software products (applications) installed on system.
	 */
	private ArrayList< Product > products;

	/**
	 * List of all installed disk drives.
	 */
	private ArrayList< DiskDrive > diskDrives;

	/**
	 * List of all processors installed on host.
	 */
	private ArrayList< Processor > processors;

	/**
	 * List of network adapters installed on host.
	 */
	private ArrayList< NetworkAdapter > networkAdapters;

	/**
	 * List of aliases.
	 */
	private ArrayList< SoftwareAlias > aliases;

	/**
	 * Informations about host memory.
	 */
	private Memory memory;

	/**
	 * Storage for Java related info.
	 */
	private JavaInfo javaInfo;

	/**
	 * Storage for properties of BEEN disk.
	 */
	private BeenDisk beenDisk;

	/**
	 * Date and time of last check of the host.
	 */
	private Date lastCheck;

	/**
	 * Object which contains user-defined properties.
	 */
	private PropertyTree userObject;

	/**
	 * Constructor which will create empty class with empty property list.
	 */
	protected HostInfo() {

		super("host", null);

		try {
			putProperty(Properties.HOST_NAME, null);
			putProperty(Properties.DETECTOR, null);
			putProperty(Properties.CHECK_DATE, null);
			putProperty(Properties.CHECK_TIME, null);

			diskDrives = new ArrayList< DiskDrive >();
			processors = new ArrayList< Processor >();
			networkAdapters = new ArrayList< NetworkAdapter >();

			putProperty(Properties.DRIVES, new ValueInteger(0));
			putProperty(Properties.PROCESSORS, new ValueInteger(0));
			putProperty(Properties.ADAPTERS, new ValueInteger(0));

			products = new ArrayList< Product >();

			putProperty(Properties.APPLICATIONS, new ValueInteger(0));

			userObject = new PropertyTree(Objects.USER_OBJECT, this);
			addObject(userObject);

			aliases = new ArrayList< SoftwareAlias >();
			putProperty(Properties.ALIASES, new ValueInteger(0));

			Collection<ValueString> groups = new ArrayList<ValueString>();
			groups.add(new ValueString(HostGroup.DEFAULT_GROUP_NAME));
			setMemberOf(groups);
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add properties to HostInfo.";
		}
	}

	/**
	 * NOTE: this operation is not supported for HostInfo class. You should always use constructor to
	 *       create instance of this class. This method will always throw UnsupportedOperationException.
	 * 
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
	 */
	@Override
	public void parseXMLNode(Node node) throws InputParseException {

		throw new UnsupportedOperationException("HostInfo class cannot be parse from XML node."
			+ " Use constructor instead.");
	}

	/**
	 * Parse given input source and fill current instance with data from the source.
	 * 
	 * @param input Input source containing XML version of the HostInfo class.
	 * 
	 * @throws InputParseException If there was an error while parsing input.
	 */
	private void parse(InputSource input) throws InputParseException {

		DocumentBuilder builder = null;

		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new InputParseException(e.getMessage());
		}

		Document document = null;

		try {
			document = builder.parse(input);
		} catch (Exception e) {
			throw new InputParseException(e.getMessage());
		}

		try {
			Node hostInfoNode = XMLHelper.getSubNodeByName("hostInfo", document);

			// parse basic info from file
			hostName = XMLHelper.getSubNodeValueByName("hostName", hostInfoNode);
			checkDate = XMLHelper.getSubNodeValueByName("lastCheckDate", hostInfoNode);
			checkTime = XMLHelper.getSubNodeValueByName("lastCheckTime", hostInfoNode);
			detector = Detectors.fromString(
				XMLHelper.getSubNodeValueByName("detector", hostInfoNode)
			);

			try {
				// parse date and time from XML file
				SimpleDateFormat df = new SimpleDateFormat(HostManagerInterface.DEFAULT_DATE_TIME_FORMAT);

				lastCheck = df.parse(checkDate + " " + checkTime);
			} catch (Exception e) {
				// error parsing date
				throw new InputParseException("Error parsing check date and time.", e);
			}

			try {
				putProperty(Properties.HOST_NAME, new ValueString(hostName));
				putProperty(Properties.DETECTOR, new ValueString(detector.toString()));
				putProperty(Properties.CHECK_DATE, new ValueString(checkDate));
				putProperty(Properties.CHECK_TIME, new ValueString(checkTime));

				putProperty(Properties.DRIVES, new ValueInteger(0));
				putProperty(Properties.PROCESSORS, new ValueInteger(0));
				putProperty(Properties.ADAPTERS, new ValueInteger(0));
				putProperty(Properties.APPLICATIONS, new ValueInteger(0));
			} catch (Exception e) {
				e.printStackTrace();
				assert false : "Unable to add properties to HostInfo.";
			}

			loadOsNode(XMLHelper.getSubNodeByName("operatingSystem", hostInfoNode));
			loadProductsNode(XMLHelper.getSubNodeByName("installedProducts", hostInfoNode));
			setMemory(new Memory(XMLHelper.getSubNodeByName("memory", hostInfoNode)));
			loadProcessorsNode(XMLHelper.getSubNodeByName("processors", hostInfoNode));
			loadDrivesNode(XMLHelper.getSubNodeByName("diskDrives", hostInfoNode));
			loadAdaptersNode(XMLHelper.getSubNodeByName("network", hostInfoNode));
			setBeenDisk(new BeenDisk(XMLHelper.getSubNodeByName("beenDisk", hostInfoNode)));
			setJavaInfo(new JavaInfo(XMLHelper.getSubNodeByName("javaInfo", hostInfoNode)));
			loadUserProperties(hostInfoNode);
			loadAliasesNode(hostInfoNode);

		} catch (InputParseException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InputParseException("Other error: " + e.getMessage());
		}
	}

	/**
	 * Load node with Operating System data.
	 * 
	 * @param osNode Node containing OS data.
	 * 
	 * @throws InputParseException If an error occurred while parsing node data.
	 */
	private void loadOsNode(Node osNode) throws InputParseException {
		/*
		OperatingSystem os = null;

		if (detector.equals(Detectors.WINDOWS)) {
			os = new WindowsOperatingSystem(osNode);
		} else if (detector.equals(Detectors.LINUX)) {
			os = new LinuxOperatingSystem(osNode);
		} else if (detector.equals(Detectors.SOLARIS)) {
			os = new SolarisOperatingSystem(osNode);
		} else {
			os = new UnknownOperatingSystem(osNode);
		}

		setOperatingSystem( os );
		 */
		setOperatingSystem( detector.makeOSFromNode( osNode ) );
	}

	/**
	 * Load all installed products.
	 * 
	 * @param installedProducts Node containing products data.
	 * 
	 * @throws InputParseException  If an error occurred while parsing node data.
	 */
	private void loadProductsNode(Node installedProducts) throws InputParseException {

		ArrayList< Node > productList = XMLHelper.getChildNodesByName("product", installedProducts);

		for ( Node node : productList ) {
			addProduct( new Product( node ) );
		}
	}

	/**
	 * Load processors node.
	 * 
	 * @param processorsNode Node containing data about processors.
	 * 
	 * @throws InputParseException  If an error occurred while parsing node data.
	 */
	private void loadProcessorsNode(Node processorsNode) throws InputParseException {

		ArrayList< Node > procList = XMLHelper.getChildNodesByName("processor", processorsNode);

		for ( Node node : procList ) {		
			addProcessor( new Processor( node ) );
		}
	}

	/**
	 * Load node containing drive data.
	 * 
	 * @param drivesNode Node with drive data.
	 * 
	 * @throws InputParseException  If an error occurred while parsing node data.
	 */
	private void loadDrivesNode(Node drivesNode) throws InputParseException {

		ArrayList< Node > drives = XMLHelper.getChildNodesByName("diskDrive", drivesNode);

		for ( Node node : drives ) {
			addDiskDrive( new DiskDrive( node ) );
		}
	}

	/**
	 * Load network adapters node.
	 * 
	 * @param networkNode Network adapters node.
	 * 
	 * @throws InputParseException  If an error occurred while parsing node data.
	 */
	private void loadAdaptersNode(Node networkNode) throws InputParseException {

		ArrayList< Node > adapters = XMLHelper.getChildNodesByName("networkAdapter", networkNode);

		for ( Node node : adapters ) {
			addNetworkAdapter( new NetworkAdapter( node ) );
		}
	}

	/**
	 * Load all user-define properties (if any).
	 * 
	 * @param hostInfoNode Root node of the file.
	 * 
	 * @throws InputParseException  If an error occurred while parsing node data.
	 */
	private void loadUserProperties(Node hostInfoNode) throws InputParseException {

		Node userNode = null;

		try {
			userNode = XMLHelper.getSubNodeByName("user", hostInfoNode);
		} catch (Exception e) {
			// do nothing, we just do not have any user properties
			userNode = null;
		}

		if (userNode != null) {

			Node propTree = XMLHelper.getSubNodeByName(PropertyTree.XML_NODE_NAME, userNode);

			if (propTree == null) {
				userObject = new PropertyTree(Objects.USER_OBJECT);
				addObject(userObject);
			} else {
				userObject = new PropertyTree(propTree);
				addObject(userObject);
			}
		} else {
			userObject = new PropertyTree(Objects.USER_OBJECT);
			addObject(userObject);
		}
	}

	/**
	 * Load software aliases.
	 * 
	 * @param hostInfoNode Root node of the file.
	 * 
	 * @throws InputParseException If an error occurred while parsing node data.
	 */
	private void loadAliasesNode(Node hostInfoNode) throws InputParseException {

		Node aliasesNode = null;

		try {
			aliasesNode = XMLHelper.getSubNodeByName("aliases", hostInfoNode);
		} catch (Exception e) {
			// this is not an error, since we may not have any aliases present
			aliasesNode = null;
		}

		if (aliasesNode != null) {
			ArrayList< Node > nodes = 
				XMLHelper.getChildNodesByName(SoftwareAlias.ALIAS_XML_NODE_NAME, aliasesNode);

			for (Node n: nodes) {
				try {
					addAlias(new SoftwareAlias(n));
				} catch (HostManagerException e) {
					throw new InputParseException("Error parsing alias data.", e);
				}
			}
		}
	}

	/**
	 * Create new instance with data from given input.
	 * 
	 * @param input InputSource with data.
	 * @throws InputParseException If there was an error while parsing input.
	 */
	public HostInfo(InputSource input) throws InputParseException {

		super("host", null);

		try {
			putProperty(Properties.HOST_NAME, null);
			putProperty(Properties.DETECTOR, null);
			putProperty(Properties.CHECK_DATE, null);
			putProperty(Properties.CHECK_TIME, null);

			diskDrives = new ArrayList< DiskDrive >();
			processors = new ArrayList< Processor >();
			networkAdapters = new ArrayList< NetworkAdapter >();

			putProperty(Properties.DRIVES, new ValueInteger(0));
			putProperty(Properties.PROCESSORS, new ValueInteger(0));
			putProperty(Properties.ADAPTERS, new ValueInteger(0));

			products = new ArrayList< Product >();

			putProperty(Properties.APPLICATIONS, new ValueInteger(0));

			aliases = new ArrayList< SoftwareAlias >();
			putProperty(Properties.ALIASES, new ValueInteger(0));

			Collection<ValueString> groups = new ArrayList<ValueString>();
			groups.add(new ValueString(HostGroup.DEFAULT_GROUP_NAME));
			setMemberOf(groups);
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add properties to HostInfo.";
		}

		parse(input);
		
		long memorySize = null == memory ? 0 : memory.getPhysicalMemorySize();
		if (0 == memorySize) {
			memorySize = Memory.Properties.PHYSICAL_MEMORY_GUESS;									// Somewhat stupid...
		}
		putProperty(
			Properties.DEFAULT_LOAD_UNITS,
			new ValueInteger(memorySize / Memory.Properties.BYTES_PER_UNIT)
		);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {

		Element hostInfoElement = document.createElement("hostInfo");

		hostInfoElement.appendChild(XMLHelper.writeValueToElement(document, hostName, "hostName"));
		hostInfoElement.appendChild(XMLHelper.writeValueToElement(document, checkDate, "lastCheckDate"));
		hostInfoElement.appendChild(XMLHelper.writeValueToElement(document, checkTime, "lastCheckTime"));
		hostInfoElement.appendChild(XMLHelper.writeValueToElement(document, detector, "detector"));
		hostInfoElement.appendChild(operatingSystem.exportAsElement(document));

		Element networkElement = document.createElement("network");

		for (NetworkAdapter adapter: networkAdapters) {
			networkElement.appendChild(adapter.exportAsElement(document));
		}

		hostInfoElement.appendChild(networkElement);

		hostInfoElement.appendChild(memory.exportAsElement(document));

		Element processorsElement = document.createElement("processors");

		for (Processor processor: processors) {
			processorsElement.appendChild(processor.exportAsElement(document));
		}

		hostInfoElement.appendChild(processorsElement);

		Element productsElement = document.createElement("installedProducts");

		for (Product product: products) {
			productsElement.appendChild(product.exportAsElement(document));
		}

		hostInfoElement.appendChild(productsElement);

		Element diskDrivesElement = document.createElement("diskDrives");

		for (DiskDrive drive: diskDrives) {
			diskDrivesElement.appendChild(drive.exportAsElement(document));
		}

		hostInfoElement.appendChild(diskDrivesElement);

		hostInfoElement.appendChild(beenDisk.exportAsElement(document));
		hostInfoElement.appendChild(javaInfo.exportAsElement(document));

		Element userObjectElement = document.createElement("user");

		userObjectElement.appendChild(userObject.exportAsElement(document));

		hostInfoElement.appendChild(userObjectElement);

		Element aliasElement = document.createElement("aliases");
		hostInfoElement.appendChild(aliasElement);

		for (SoftwareAlias a: aliases) {
			aliasElement.appendChild(a.exportAsElement(document));
		}

		return hostInfoElement;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getHostName()
	 */
	public String getHostName() {

		return hostName;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getCheckDate()
	 */
	public String getCheckDate() {

		return checkDate;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getCheckTime()
	 */
	public String getCheckTime() {

		return checkTime;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getDetectorIDString()
	 */
	public String getDetectorIDString() {

		return detector.toString();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getDriveCount()
	 */
	public int getDriveCount() {

		return diskDrives.size();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getDiskDrive(int)
	 */
	public DiskDrive getDiskDrive(int index) throws IndexOutOfBoundsException {

		return diskDrives.get(index);
	}

	/**
	 * Adds new DiskDrive object to list of drives.
	 * 
	 * @param newDrive DiskDrive to add to list.
	 */
	private void addDiskDrive(DiskDrive newDrive) {

		newDrive.setParent(this);
		diskDrives.add(newDrive);
		addObject(newDrive);

		try {
			setPropertyValue(Properties.DRIVES, new ValueInteger(diskDrives.size()));
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add drive.";
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getProcessorCount()
	 */
	public int getProcessorCount() {

		return processors.size();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getProcessor(int)
	 */
	public Processor getProcessor(int index) throws IndexOutOfBoundsException {

		return processors.get(index);
	}

	/**
	 * Adds new processor to list of processors.
	 * 
	 * @param newProcessor Processor object to add to list.
	 */
	private void addProcessor(Processor newProcessor) {

		newProcessor.setParent(this);
		processors.add(newProcessor);
		addObject(newProcessor);

		try {
			setPropertyValue(Properties.PROCESSORS, new ValueInteger(processors.size()));
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add processor.";
		}
	}	

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getNetworkAdapterCount()
	 */
	public int getNetworkAdapterCount() {

		return networkAdapters.size();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getNetworkAdapter(int)
	 */
	public NetworkAdapter getNetworkAdapter(int index) throws IndexOutOfBoundsException {

		return networkAdapters.get(index);
	}

	/**
	 * Adds new network adapter to the list of adapters.
	 * 
	 * @param newAdapter Adapter to add.
	 */
	private void addNetworkAdapter(NetworkAdapter newAdapter) {

		newAdapter.setParent(this);
		addObject(newAdapter);
		networkAdapters.add(newAdapter);

		try {
			setPropertyValue(Properties.ADAPTERS, new ValueInteger(networkAdapters.size()));
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add network adapter.";
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getMemory()
	 */
	public Memory getMemory() {

		return memory;
	}

	/**
	 * Sets memory properties.
	 * 
	 * @param mm Memory properties. 
	 */
	private void setMemory(Memory mm) {

		memory = mm;
		memory.setParent(this);
		try {
			removeObject(Objects.MEMORY);
		} catch (Exception e) {
			/* ignore this */
		}
		addObject(mm);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getOperatingSystem()
	 */
	public OperatingSystem getOperatingSystem() {

		return operatingSystem;
	}

	/**
	 * Sets properties of operating system.
	 * 
	 * @param newOs Operating system properties.
	 */
	private void setOperatingSystem(OperatingSystem newOs) {

		operatingSystem = newOs;
		operatingSystem.setParent(this);
		try {
			removeObject(Objects.OPERATING_SYSTEM);
		} catch (Exception e) {
			/* ignore this */
		}

		addObject(operatingSystem);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getProductCount()
	 */
	public int getProductCount() {

		return products.size();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getProduct(int)
	 */
	public Product getProduct(int index) throws IndexOutOfBoundsException {

		return products.get(index);
	}

	/**
	 * Adds product to the list of products.
	 * 
	 * @param newProduct Product to add to the list.
	 */
	private void addProduct(Product newProduct) {

		newProduct.setParent(this);
		products.add(newProduct);
		addObject(newProduct);

		try {
			setPropertyValue(Properties.APPLICATIONS, new ValueInteger(products.size()));
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Unable to add product.";
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getJavaInfo()
	 */
	public JavaInfo getJavaInfo() {

		return javaInfo;
	}

	/**
	 *  Sets properties of Java.
	 *  
	 * @param ji Java properties.
	 */
	private void setJavaInfo(JavaInfo ji) {

		javaInfo = ji;
		javaInfo.setParent(this);
		try {
			removeObject(Objects.JAVA);
		} catch (Exception e) {
			/* ignore this */
		}
		addObject(javaInfo);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getBeenDisk()
	 */
	public BeenDisk getBeenDisk() {

		return beenDisk;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getSoftwareAlias(int)
	 */
	public SoftwareAlias getSoftwareAlias(int index) throws IndexOutOfBoundsException {

		return aliases.get(index);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getSoftwareAliasCount()
	 */
	public int getSoftwareAliasCount() {

		return aliases.size();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getSoftwareAliasList()
	 */
	public SoftwareAlias[] getSoftwareAliasList() {

		SoftwareAlias []a = new SoftwareAlias[aliases.size()];

		a = aliases.toArray(a);

		return a;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#hasSoftwareAlias(java.lang.String)
	 */
	public boolean hasSoftwareAlias(String aliasName) {

		for (SoftwareAlias a: aliases) {
			if (a.getAliasName().equals(aliasName)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getSoftwareAliasByName(java.lang.String)
	 */
	public List< SoftwareAlias > getSoftwareAliasByName(String name) {

		ArrayList< SoftwareAlias > result = new ArrayList< SoftwareAlias >();

		for (SoftwareAlias a: aliases) {
			if (a.getAliasName().equals(name)) {
				result.add(a);
			}
		}

		return result;
	}

	/**
	 * Add new alias to the list of aliases.
	 * 
	 * @param newAlias Alias to add.
	 * 
	 * @throws HostManagerException If some error occurred. 
	 */
	public void addAlias(SoftwareAlias newAlias) throws HostManagerException {

		try {
			aliases.add(newAlias);
			addObject(newAlias);

			setPropertyValue(Properties.ALIASES, new ValueInteger(aliases.size()));
		} catch (Exception e) {
			throw new HostManagerException("Error adding alias.", e);
		}
	}

	/**
	 * Remove all aliases from the host.
	 * 
	 * @throws HostManagerException If some error occurred.
	 */
	public void removeAllAliases() throws HostManagerException {

		if (aliases.size() == 0) {
			return;
		}

		try {
			removeAllOfType("alias");
			aliases.clear();
			setPropertyValue(Properties.ALIASES, new ValueInteger(0));
		} catch (Exception e) {
			throw new HostManagerException("Error removing aliases.", e);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getCheckDateTime()
	 */
	public Date getCheckDateTime() {

		return lastCheck;
	}

	/**
	 * Sets properties of BEEN disk.
	 * 
	 * @param newBD Class with properties of BEEN disk.
	 */
	private void setBeenDisk(BeenDisk newBD) {

		beenDisk = newBD;
		beenDisk.setParent(this);
		try {
			removeObject(Objects.BEEN_DISK);
		} catch (Exception e) {
			//assert false : "Unable to remove been disk info. This should not happen.";
		}
		addObject(beenDisk);
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return getHostName().hashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return hostName + " @ " + checkDate + " " + checkTime;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
	 */
	@Override
	public String getXMLNodeName() {

		return "hostInfo";
	}

	/**
	 * Set value of "memberof" property.
	 * 
	 * @param groups List of group names this host is member of.
	 */
	public void setMemberOf(Collection< ValueString > groups) {

		try {
			this.putProperty("memberof", new ValueList< ValueString >(groups, ValueType.STRING));
		} catch (InvalidArgumentException e) {
			// this will never happen
			e.printStackTrace();
			assert false : "Yer computah iz ded.";
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#addUserProperty(String, ValueCommonInterface)
	 */
	public void addUserProperty(String name, ValueCommonInterface value) throws InvalidArgumentException {

		userObject.addProperty(name, value);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#addUserProperty(NameValuePair)
	 */
	public void addUserProperty(NameValuePair property) throws InvalidArgumentException {

		userObject.addProperty(property);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getUserPropertiesIterator()
	 */
	@Deprecated
	public Iterator< NameValuePair > getUserPropertiesIterator() {

		return userObject.getProperties().iterator();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#putUserProperty(String, ValueCommonInterface)
	 */
	public ValueCommonInterface putUserProperty(String name, ValueCommonInterface value) 
	throws InvalidArgumentException {

		return userObject.putProperty(name, value);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#putUserProperty(NameValuePair)
	 */
	public ValueCommonInterface putUserProperty(NameValuePair property)
	throws InvalidArgumentException {

		return userObject.putProperty(property);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#removeUserProperty(String)
	 */
	public void removeUserProperty(String name) 
	throws InvalidArgumentException, ValueNotFoundException {

		userObject.removeProperty(name);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getUserPropertiesIterator()
	 */
	public ValueCommonInterface setUserProperty(String name, ValueCommonInterface value) 
	throws InvalidArgumentException, ValueNotFoundException {

		return userObject.setPropertyValue(name, value);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getUserPropertyValue(java.lang.String)
	 */
	public ValueCommonInterface getUserPropertyValue(String name) throws ValueNotFoundException, InvalidArgumentException {

		return userObject.getPropertyValue(name);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#hasUserProperty(java.lang.String)
	 */
	public boolean hasUserProperty(String name) throws InvalidArgumentException {

		return userObject.hasProperty(name);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#setUserProperty(cz.cuni.mff.been.hostmanager.database.NameValuePair)
	 */
	public ValueCommonInterface setUserProperty(NameValuePair property) 
	throws InvalidArgumentException, ValueNotFoundException {

		return userObject.setPropertyValue(property);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getUserPropertiesObject()
	 */
	public PropertyTreeInterface getUserPropertiesObject() {

		return userObject;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#setUserPropertiesObject(cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface)
	 */
	public void setUserPropertiesObject(PropertyTreeReadInterface props) {

		int oc = 0;

		try {
			oc = getObjectCount("user");
		} catch (Exception e) {
			// do nothing
		}

		if (oc > 0) {
			try {
				removeObject("user");
			} catch (Exception e) {
				// do nothing
			}
		}

		userObject = (PropertyTree) props;
		addObject((PropertyTree) props);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.HostInfoInterface#getUserProperties()
	 */
	public NameValuePair[] getUserProperties() {
		List<NameValuePair> result = new LinkedList<NameValuePair>();
		for (
			Iterator<NameValuePair> iterator = userObject.getProperties().iterator();
			iterator.hasNext();
		) {
			result.add(iterator.next());
		}
		return result.toArray(new NameValuePair[result.size()]);
	}

}
