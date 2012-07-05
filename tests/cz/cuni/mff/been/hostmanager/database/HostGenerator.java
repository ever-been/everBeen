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

import java.io.BufferedWriter;
import java.io.File;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.been.common.Pair;

import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This class encapsulates generator which is able to create testing database for Host Manager.
 * When run, XML files with host configuration, group data and index data will be created in the
 * ${BEEN_HOME}/data/temp directory. To use database you will have to copy all files to the database
 * directory of the Host Manager.
 * Host configuration files are generated according to parameters specified in the GENERATORS
 * array. Please, only edit those to get another test database.
 *
 * @author Branislav Repcek
 */
public class HostGenerator {

	/**
	 * Generators used when generating files in database.
	 * 
	 * Each line will generate 8 hosts with same configurations. Every host will have 5 history 
	 * entries. To generate smaller database comment out unwanted lines. (Damn you Java with no
	 * conditional compilation support ;) ).
	 */
	private static final HostGenerator []GENERATORS = {
		//  Names:        post   OS  net   M C   D   apps    J  h   a         date-time
		//  Limits:       ******  4  A~C   5 10  3   A~L     2  *   *   yyyy-MM-dd-HH-mm-ss
		
		new HostGenerator("comp", 0, "AB", 0, 0, 0, "ADEGL", 0, 8, 5, "2006-07-01-12-53-08"),
		new HostGenerator("comp", 1, "AB", 4, 6, 1, "BDEGL", 0, 8, 5, "2006-07-01-02-53-08"),
		new HostGenerator("comp", 2, "AB", 3, 4, 0, "CDEGL", 1, 8, 5, "2006-07-01-12-35-08"),
		new HostGenerator("comp", 2, "AC", 4, 9, 0, "ADEGHIL", 1, 8, 5, "2006-07-01-12-53-08"),
		new HostGenerator("comp", 0, "AC", 0, 3, 1, "ADEGHI", 0, 8, 5, "2006-07-01-12-53-08"),
		new HostGenerator("comp", 1, "AC", 0, 2, 2, "BDGEF", 0, 8, 5, "2006-07-01-02-53-08"),
		new HostGenerator("comp", 2, "BC", 0, 3, 0, "CDEGL", 1, 8, 5, "2006-07-01-12-35-08"),

		new HostGenerator("comp", 0, "AAA", 4, 3, 0, "CDGIJ", 0, 8, 5, "2006-07-01-12-53-08"),
		new HostGenerator("comp", 1, "BA", 4, 4, 1, "AEF", 0, 8, 5, "2006-07-01-13-53-08"),
		new HostGenerator("comp", 2, "CA", 3, 5, 0, "ADGI", 1, 8, 5, "2006-07-01-14-35-08"),
		new HostGenerator("comp", 2, "CCC", 4, 7, 2, "JAEDBFH", 1, 8, 5, "2006-07-01-15-53-08"),
		new HostGenerator("comp", 0, "A", 2, 6, 1, "ADCKGI", 0, 8, 5, "2006-07-01-16-53-08"),
		new HostGenerator("comp", 1, "C", 1, 8, 2, "KDEF", 0, 8, 5, "2006-07-01-17-53-08"),
		new HostGenerator("comp", 2, "B", 1, 1, 0, "L", 1, 8, 5, "2006-07-01-18-35-08"),
		
		new HostGenerator("comp", 0, "A", 2, 1, 0, "CDF", 1, 8, 5, "2006-07-02-12-53-08"),
		new HostGenerator("comp", 1, "BB", 4, 2, 1, "B", 1, 8, 5, "2006-07-02-02-53-08"),
		new HostGenerator("comp", 2, "CB", 3, 3, 2, "CL", 1, 8, 5, "2006-07-02-12-35-08"),
		new HostGenerator("comp", 2, "AC", 4, 4, 1, "EGHIL", 0, 8, 5, "2006-07-02-12-53-08"),
		new HostGenerator("comp", 3, "AB", 1, 5, 2, "IADE", 0, 8, 5, "2006-07-02-12-53-08"),
		new HostGenerator("comp", 2, "BC", 2, 6, 2, "DBGFA", 1, 8, 5, "2006-07-02-02-53-08"),
		new HostGenerator("comp", 1, "CC", 3, 7, 1, "ABC", 0, 8, 5, "2006-07-02-12-35-08"),

		new HostGenerator("comp", 1, "AA", 1, 8, 1, "DEAI", 0, 8, 5, "2006-07-02-12-53-08"),
		new HostGenerator("comp", 2, "BB", 4, 9, 0, "ACHIL", 1, 8, 5, "2006-07-02-13-53-08"),
		new HostGenerator("comp", 3, "BA", 3, 2, 0, "BACH", 1, 8, 5, "2006-07-02-14-35-08"),
		new HostGenerator("comp", 3, "BCC", 4, 3, 0, "DGIJ", 0, 8, 5, "2006-07-02-15-53-08"),
		new HostGenerator("comp", 2, "A", 2, 3, 1, "HJKLAC", 1, 8, 5, "2006-07-02-16-53-08"),
		new HostGenerator("comp", 1, "BB", 0, 5, 0, "DALEK", 0, 8, 5, "2006-07-02-17-53-08"),
		new HostGenerator("comp", 1, "B", 0, 6, 0, "ABCDEFGHIJKL", 0, 8, 5, "2006-07-02-18-35-08")
	};
	
	private static final String OUTPUT_DIRECTORY = System.getenv("BEEN_HOME") + "/data/temp/";
	
	/**
	 * Generate all database files.
	 * 
	 * @param args Command-line arguments (none).
	 * 
	 * @throws Exception If an error occured.
	 */
	public static void main(String[] args) throws Exception {
		
		{
			System.out.println(OUTPUT_DIRECTORY);
			File od = new File(OUTPUT_DIRECTORY);
		
			od.mkdirs();
		}
		
		Document document = XMLHelper.createDocument();
		
		Element root = document.createElement("index");
		Element hosts = document.createElement("hosts");
		Element groups = document.createElement("groups");
		
		document.appendChild(root);
		root.appendChild(hosts);
		root.appendChild(groups);
		
		GroupIndexEntry defaultEntry = new GroupIndexEntry(HostGroup.DEFAULT_GROUP_NAME, "0.group");
		ArrayList< Pair< HostGroup, GroupIndexEntry > > groupList = 
			new ArrayList< Pair < HostGroup, GroupIndexEntry > >();
		
		HostGroup def = new HostGroup(HostGroup.DEFAULT_GROUP_NAME);
		
		int i = 0;
		for (HostGenerator current: GENERATORS) {
			
			ArrayList< HostIndexEntry > result = current.generateAll(OUTPUT_DIRECTORY);
			HostGroup g = new HostGroup("Group " + l2(i));
			GroupIndexEntry e = new GroupIndexEntry(g.getName(), l2(i) + ".group");

			for (HostIndexEntry entry: result) {
				
				hosts.appendChild(entry.exportAsElement(document));
				def.addHost(entry.getHostName());
				g.addHost(entry.getHostName());
			}
			
			String desc = "Host name mask: " + current.getHostNameMask()
			              + "\nOS index: " + current.getOsIndex()
			              + "\nNetwork adapters: " + current.getNet()
			              + "\nMemory index: " + current.getMemIndex()
			              + "\nCPU index: " + current.getCpuIndex()
			              + "\nDrive index: " + current.getDriveIndex()
			              + "\nApplications: " + current.getApps()
			              + "\nJava info index: " + current.getJavaIndex();
			g.setDescription(desc);
			
			groupList.add(new Pair< HostGroup, GroupIndexEntry >(g, e));
			i += 1;
		}

		groups.appendChild(defaultEntry.exportAsElement(document));
		
		for (Pair< HostGroup, GroupIndexEntry > p: groupList) {
			
			XMLHelper.saveXMLSerializable(p.getKey(), OUTPUT_DIRECTORY + p.getValue().getDataFileName(), 
					true, "UTF-16");
			groups.appendChild(p.getValue().exportAsElement(document));
		}
		
		XMLHelper.saveDocument(document, OUTPUT_DIRECTORY + "db.index");

		Document groupdoc = XMLHelper.createDocument();
		groupdoc.appendChild(def.exportAsElement(groupdoc));
		
		XMLHelper.saveDocument(groupdoc, OUTPUT_DIRECTORY + "0.group");
	}
	
	private String postfix;
	
	private int osIndex;
	
	private String net;
	
	private int memIndex;
	
	private int cpuIndex;
	
	private int driveIndex;
	
	private String apps;
	
	private int hosts;
	
	private int hist;
	
	private int javaIndex;
	
	private String date;
	
	private Date realDate;
	
	private GregorianCalendar cal;
	
	private SimpleDateFormat df;
	
	/**
	 * Create host id.
	 * 
	 * @param postfix Host name prostfix.
	 * @param osIndex Index od OS node.
	 * @param net Network adapters. (*)
	 * @param memIndex Index of memory node.
	 * @param cpuIndex Index of processors node.
	 * @param driveIndex Index of disk node.
	 * @param apps Applications. (*)
	 * @param javaIndex Index of JAVA info node.
	 * @param hosts Number of hosts to generate.
	 * @param hist Number of history entries for each host.
	 * @param date Date of the host detection (format: yyyy-MM-dd-HH-mm-ss).
	 * 
	 * (*) Each character of string is index into the corresponding array. Radix 26 is used. 
	 * Each letter can appear multiple times.
	 * [eg. "ACFBF" are nodes 0, 2, 5, 2, and 5 in that order.]
	 */
	public HostGenerator(String postfix, int osIndex, String net, int memIndex, int cpuIndex, 
	                 int driveIndex, String apps, int javaIndex, int hosts, int hist, String date)
	{
		try {
			this.postfix = postfix;
			this.osIndex = osIndex;
			this.net = net;
			this.memIndex = memIndex;
			this.cpuIndex = cpuIndex;
			this.driveIndex = driveIndex;
			this.apps = apps;
			this.hosts = hosts;
			this.hist = hist;
			this.date = date;
			this.javaIndex = javaIndex;
			
			df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
			realDate = df.parse(this.date);
			
			cal = new GregorianCalendar();
			cal.setTime(realDate);
		} catch (Exception e) {
			assert false : "bummer";
		}
	}
	
	/**
	 * Minutes added to the specified time when creating new host.
	 */
	private static final int HOST_ADD_DELTA_MIN = 10;
	
	/**
	 * Delay between dates of history files.
	 */
	private static final int HISTORY_DELTA_HOURS = 48;
	
	/**
	 * Generate all host based on the parameters set when creating instance of the class.
	 * 
	 * @param outDir Output directory. Directory has to exist before calling this function. Name also
	 *        has to contain separator character at the end of the directory path.
	 * 
	 * @return Index entries for all generated hosts.
	 * 
	 * @throws Exception If some error occured.
	 */
	public ArrayList< HostIndexEntry > generateAll(String outDir) throws Exception {
	
		ArrayList< HostIndexEntry > entries = new ArrayList< HostIndexEntry >();
		
		for (int i = 0; i < hosts; ++i)
		{
			String hostName = baseHostName(i);

			GregorianCalendar curCal = (GregorianCalendar) cal.clone();
			curCal.add(GregorianCalendar.MINUTE, i * HOST_ADD_DELTA_MIN);
			
			Date dd = curCal.getTime();
			String baseFileName = hostName + "-" + df.format(dd);
	
			HostIndexEntry e = new HostIndexEntry(hostName, 
					baseFileName + ".host", 
					baseFileName + ".load", 
					baseFileName + ".loadmap", dd);

			entries.add(e);
			
			System.out.println("Generating primary file for \"" + hostName + "\""); 
			generate(hostName, outDir + baseFileName + ".host", dd);

			for (int j = 0; j < hist; ++j)
			{
				curCal.add(GregorianCalendar.HOUR, -j * HISTORY_DELTA_HOURS);
				
				Date d = curCal.getTime();
				
				String fileName = hostName + "-" + df.format(d) + ".host";
				
				System.out.println("  Generating history file, date: " + df.format(d));
				generate(hostName, outDir + fileName, d);
				
				e.addHistoryEntry(d, fileName);
			}
		}
		
		return entries;
	}
	
	/**
	 * Generate one host file.
	 * 
	 * @param hostName Name of the host.
	 * @param fileName Output file name.
	 * @param curDate Date which will be stored in the file.
	 *  
	 * @throws Exception If some error occured.
	 */
	private void generate(String hostName, String fileName, Date curDate) throws Exception {

		String dateString = MiscUtils.formatDate(curDate, HostManagerInterface.DEFAULT_DATE_FORMAT);
		String timeString = MiscUtils.formatDate(curDate, HostManagerInterface.DEFAULT_TIME_FORMAT);
		
		String result = XML_HEADER
			            + "<hostName>" + hostName + "</hostName>\n"
		                + "<lastCheckDate>" + dateString + "</lastCheckDate>\n"
		                + "<lastCheckTime>" + timeString + "</lastCheckTime>\n"
		                + "<detector>" + "hwdet3_windows" + "</detector>\n";
		
		result += OS_INFO_NODES[this.osIndex];
		result += PROCESSOR_NODES[this.cpuIndex];
		result += MEMORY_NODES[this.memIndex];
		
		result += "<network>\n";
		
		for (int i = 0; i < net.length(); ++i) {
			int index = net.charAt(i) - 'A';
			result += NETWORK_ADAPTER_NODES[index];
		}
		
		result += "</network>\n";
		
		result += ALL_DRIVES[this.driveIndex];

		result += "<installedProducts>\n";
		
		for (int i = 0; i < this.apps.length(); ++i) {
			int index = this.apps.charAt(i) - 'A';
			result += APPLICATIONS[index];
		}
		
		result += MANDATORY_APPS;
		
		result += "</installedProducts>\n";
		
		result += JAVA_INFO[this.javaIndex];
		
		result += XML_FOOTER;
		
		BufferedWriter bw = MiscUtils.openFileForWritingWithEncoding(fileName, "UTF-16");
		
		bw.write(result);
		
		bw.close();
	}	
	
	/**
	 * Get mask of the host name. Index of the host is replaced with asterisks.
	 * 
	 * @return Host name mask. 
	 */
	public String getHostNameMask() {
		
		return l1(osIndex) + "-" + l2(memIndex) + "-" + l2(cpuIndex) + "-"
		       + l2(driveIndex) + "-" + net + "-" + apps + ".***." + postfix;
	}
	
	/**
	 * Get applications config string.
	 * 
	 * @return Application config string.
	 */
	public String getApps() {
		return apps;
	}

	/**
	 * Get CPU index.
	 * 
	 * @return CPU index.
	 */
	public int getCpuIndex() {
		return cpuIndex;
	}

	/**
	 * Get drive index.
	 * 
	 * @return Drive index.
	 */
	public int getDriveIndex() {
		return driveIndex;
	}

	/**
	 * Get number of history entries created.
	 * 
	 * @return Number of history entries created.
	 */
	public int getHist() {
		return hist;
	}

	/**
	 * Get number of hosts created with current configuration.
	 * 
	 * @return Number of hosts with current configuration.
	 */
	public int getHosts() {
		return hosts;
	}

	/**
	 * Get index of java info.
	 * 
	 * @return Java info index.
	 */
	public int getJavaIndex() {
		return javaIndex;
	}

	/**
	 * Get memory index.
	 * 
	 * @return Memory index.
	 */
	public int getMemIndex() {
		return memIndex;
	}

	/**
	 * Get network adapters configuration string.
	 * 
	 * @return Network adapters configuration string.
	 */
	public String getNet() {
		return net;
	}

	/**
	 * Get OS node index.
	 * 
	 * @return OS node index.
	 */
	public int getOsIndex() {
		return osIndex;
	}

	/**
	 * Get string appended to generated host name.
	 * 
	 * @return String appended to generated host name.
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * Get name of the host without extension.
	 * 
	 * @param index Index of the host in the list of hosts with same config.
	 * 
	 * @return Name of the host. 
	 */
	private String baseHostName(int index) {
		
		String result = l1(osIndex) + "-" + l2(memIndex) + "-" + l2(cpuIndex) + "-" 
		                + l2(driveIndex) + "-" + net + "-" + apps + "." + ln(index, 3);
			
		return result + "." + postfix;
	}
	
	/**
	 * Convert number to string.
	 * 
	 * @param i Number to convert.
	 * 
	 * @return String representing given number.
	 */
	private static String l1(int i) {
		
		return String.valueOf(i);
	}
	
	/**
	 * Convert number to string and add leading zeros so the result is 2 characters long.
	 * 
	 * @param i Number to convert.
	 * 
	 * @return String representing givne number.
	 */
	private static String l2(int i) {
		
		String r = String.valueOf(i);
		
		if (i <= 9) {
			r = "0" + r;
		}
		
		return r;
	}
	
	/**
	 * Convert number to string and add leading zeros so the result has specified length.
	 * 
	 * @param i Number to convert.
	 * @param l Desired length of the resulting string.
	 * 
	 * @return String representing given number.
	 */
	private static String ln(int i, int l) {
		
		String r = String.valueOf(i);
		
		while (r.length() < l) {
			r = "0" + r;
		}
		
		return r;
	}
	
	/**
	 * Prolog of the XML file.
	 */
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<hostInfo>\n";
	
	/**
	 * Epilog of the XML file.
	 */
	private static final String XML_FOOTER = "</hostInfo>";
	
	/**
	 * Array containing OS nodes.
	 */
	private static final String []OS_INFO_NODES = {
		"<operatingSystem>\n" + 
		"  <basicInfo>\n" + 
		"    <name>Microsoft Windows XP Professional</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <arch>x86</arch>\n" +
		"  </basicInfo>\n" + 
		"  <advancedInfo>\n" +
		"    <version>5.1.2600</version>\n" + 
		"    <buildType>Uniprocessor Free</buildType>\n" + 
		"    <servicePackVersion>2.0</servicePackVersion>\n" + 
		"    <windowsDirectory>C:\\WINDOWS</windowsDirectory>\n" + 
		"    <systemDirectory>C:\\WINDOWS\\system32</systemDirectory>\n" + 
		"    <encryptionLevel>168</encryptionLevel>\n" + 
		"  </advancedInfo>\n" + 
		"</operatingSystem>\n",

		"<operatingSystem>\n" + 
		"  <basicInfo>\n" + 
		"    <name>Microsoft Windows XP Professional</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <arch>x86</arch>\n" +
		"  </basicInfo>\n" + 
		"  <advancedInfo>\n" +
		"    <version>5.1.2600</version>\n" + 
		"    <buildType>Uniprocessor Free</buildType>\n" + 
		"    <servicePackVersion>0.0</servicePackVersion>\n" + 
		"    <windowsDirectory>C:\\WINDOWS</windowsDirectory>\n" + 
		"    <systemDirectory>C:\\WINDOWS\\system32</systemDirectory>\n" + 
		"    <encryptionLevel>168</encryptionLevel>\n" +
		"  </advancedInfo>\n" + 
		"</operatingSystem>\n",
		
		"<operatingSystem>\n" + 
		"  <basicInfo>\n" + 
		"    <name>Microsoft(R) Windows(R) Server 2003, Standard Edition</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <arch>x86</arch>\n" + 
		"  </basicInfo>\n" + 
		"  <advancedInfo>\n" + 
		"    <version>5.2.3790</version>\n" + 
		"    <buildType>Uniprocessor Free</buildType>\n" + 
		"    <servicePackVersion>1.0</servicePackVersion>\n" + 
		"    <windowsDirectory>G:\\WINDOWS</windowsDirectory>\n" + 
		"    <systemDirectory>G:\\WINDOWS\\system32</systemDirectory>\n" + 
		"    <encryptionLevel>168</encryptionLevel>\n" + 
		"  </advancedInfo>\n" + 
		"</operatingSystem>\n",
		
		"<operatingSystem>\n" + 
		"<basicInfo>\n" + 
		"<name>Microsoft Windows Vista\" Ultimate </name>\n" + 
		"<vendor>Microsoft Corporation</vendor>\n" + 
		"<arch>x86</arch>\n" + 
		"</basicInfo>\n" + 
		"<advancedInfo>\n" + 
		"<version>6.0.5384</version>\n" + 
		"<buildType>Multiprocessor Free</buildType>\n" + 
		"<servicePackVersion>0.0</servicePackVersion>\n" + 
		"<windowsDirectory>C:\\Windows</windowsDirectory>\n" + 
		"<systemDirectory>C:\\Windows\\system32</systemDirectory>\n" + 
		"<encryptionLevel>256</encryptionLevel>\n" + 
		"</advancedInfo>\n" + 
		"</operatingSystem>\n"
	};
	
	/**
	 * Array containing network adapter nodes.
	 */
	private static final String []NETWORK_ADAPTER_NODES = {
		"  <networkAdapter>\n" +
		"    <name>Realtek RTL8169/8110 Family Gigabit Ethernet NIC</name>\n" + 
		"    <vendor>Realtek Semiconductor Corp.</vendor>\n" + 
		"    <adapterType>Ethernet 802.3</adapterType>\n" + 
		"    <macAddress>00:11:0B:00:B1:E5</macAddress>\n" +
		"  </networkAdapter>\n",

		"  <networkAdapter>\n" +
		"    <name>Realtek RTL8139/810x Family Fast Ethernet NIC</name>\n" + 
		"    <vendor>Realtek Semiconductor Corp.</vendor>\n" + 
		"    <adapterType>Ethernet 802.3</adapterType>\n" + 
		"    <macAddress>00:11:19:CE:59:AE</macAddress>\n" +
		"  </networkAdapter>\n",
		
		"  <networkAdapter>\n" + 
		"    <name>VMware Accelerated AMD PCNet Adapter</name>\n" + 
		"    <vendor>VMware, Inc.</vendor>\n" + 
		"    <adapterType>Ethernet 802.3</adapterType>\n" + 
		"    <macAddress>00:0C:29:0C:F6:C0</macAddress>\n" + 
		"  </networkAdapter>\n"
	};
	
	/**
	 * Array containing memory info nodes.
	 */
	private static final String []MEMORY_NODES = {
		// 256M
		"<memory>\n" + 
		"  <physicalMemorySize>268435456</physicalMemorySize>\n" + 
		"  <virtualMemorySize>2147352576</virtualMemorySize>\n" + 
		"  <swapSize>0</swapSize>\n" + 
		"  <pagingFileSize>2042630144</pagingFileSize>\n" + 
		"</memory>\n",

		// 512M
		"<memory>\n" + 
		"  <physicalMemorySize>536870912</physicalMemorySize>\n" + 
		"  <virtualMemorySize>2147352576</virtualMemorySize>\n" + 
		"  <swapSize>0</swapSize>\n" + 
		"  <pagingFileSize>2042630144</pagingFileSize>\n" + 
		"</memory>\n",

		// 1G
		"<memory>\n" + 
		"  <physicalMemorySize>1073741824</physicalMemorySize>\n" + 
		"  <virtualMemorySize>2147352576</virtualMemorySize>\n" + 
		"  <swapSize>0</swapSize>\n" + 
		"  <pagingFileSize>2042630144</pagingFileSize>\n" + 
		"</memory>\n",

		// 1.5G
		"<memory>\n" + 
		"  <physicalMemorySize>1610612736</physicalMemorySize>\n" + 
		"  <virtualMemorySize>2147352576</virtualMemorySize>\n" + 
		"  <swapSize>0</swapSize>\n" + 
		"  <pagingFileSize>2042630144</pagingFileSize>\n" + 
		"</memory>\n",

		// 2G
		"<memory>\n" + 
		"  <physicalMemorySize>2147483648</physicalMemorySize>\n" + 
		"  <virtualMemorySize>2147352576</virtualMemorySize>\n" + 
		"  <swapSize>0</swapSize>\n" + 
		"  <pagingFileSize>2042630144</pagingFileSize>\n" + 
		"</memory>\n"
	};
	
	/**
	 * Array containing processor info nodes.
	 */
	private static final String []PROCESSOR_NODES = {
		// AMD Athlon 64 3000+, 512 cache
		"<processors>\n" + 
		"  <processor>\n" + 
		"    <model>AMD Athlon(tm) 64 Processor 3000+</model>\n" + 
		"    <vendor>AuthenticAMD</vendor>\n" + 
		"    <speed>1800</speed>\n" + 
		"    <l2CacheSize>512</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// AMD Athlon XP 2500+, 512 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>AMD Athlon(TM) XP 2500+</model>\n" + 
		"    <vendor>AuthenticAMD</vendor>\n" + 
		"    <speed>1822</speed>\n" + 
		"    <l2CacheSize>512</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// AMD Athlon XP 1800+, 256 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>AMD Athlon(tm) XP 1800+</model>\n" + 
		"    <vendor>AuthenticAMD</vendor>\n" + 
		"    <speed>1533</speed>\n" + 
		"    <l2CacheSize>256</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// AMD Athlon 64 3200+, 512 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>AMD Athlon(tm) 64 Processor 3200+</model>\n" + 
		"    <vendor>AuthenticAMD</vendor>\n" + 
		"    <speed>2197</speed>\n" + 
		"    <l2CacheSize>512</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// AMD Opteron 144, 512 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>AMD Opteron(tm) Processor 144</model>\n" + 
		"    <vendor>AuthenticAMD</vendor>\n" + 
		"    <speed>1802</speed>\n" + 
		"    <l2CacheSize>512</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// Intel P4 2.8, 1024 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>Intel(R) Pentium(R) 4 CPU 2.80GHz</model>\n" + 
		"    <vendor>GenuineIntel</vendor>\n" + 
		"    <speed>2801</speed>\n" + 
		"    <l2CacheSize>1024</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",
		
		// Intel P4 3.0, 1024 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>Intel(R) Pentium(R) 4 CPU 3.00GHz</model>\n" + 
		"    <vendor>GenuineIntel</vendor>\n" + 
		"    <speed>2993</speed>\n" + 
		"    <l2CacheSize>1024</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// Intel P4 2.0, 512 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>Intel(R) Pentium(R) 4 CPU 2.00GHz</model>\n" + 
		"    <vendor>GenuineIntel</vendor>\n" + 
		"    <speed>2019</speed>\n" + 
		"    <l2CacheSize>512</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",

		// Intel P4 1.5, 256 cache
		"<processors>\n" +
		"  <processor>\n" + 
		"    <model>Intel(R) Pentium(R) 4 CPU 1.50GHz</model>\n" + 
		"    <vendor>GenuineIntel</vendor>\n" + 
		"    <speed>1495</speed>\n" + 
		"    <l2CacheSize>256</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>",
		
		// Dual-Core AMD Opteron 880, 2400 MHz, 1024 KB cache
		"<processors>\n" +
		"  <processor>\n" +
		"    <model>AMD Opteron(tm) Processor 880</model>\n" +
		"    <vendor>AuthenticAMD</vendor>\n" +
		"    <speed>2401</speed>\n" +
		"    <l2CacheSize>1024</l2CacheSize>\n" +
		"  </processor>\n" +
		"  <processor>\n" +
		"    <model>AMD Opteron(tm) Processor 880</model>\n" +
		"    <vendor>AuthenticAMD</vendor>\n" +
		"    <speed>2401</speed>\n" +
		"    <l2CacheSize>1024</l2CacheSize>\n" +
		"  </processor>\n" +
		"</processors>\n"
	};
	
	/**
	 * Array containing disk drive nodes.
	 */
	private static final String []DISK_DRIVE_NODES = {
		"<diskDrives>\n" +
		"  <diskDrive>\n" + 
		"    <model>Maxtor 6Y120P0</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE0</deviceName>\n" + 
		"    <size>122935034880</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #0</deviceName>\n" + 
		"      <name>C:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>6292303872</size>\n" + 
		"      <freeSpace>1770876928</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #1</deviceName>\n" + 
		"      <name>D:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>18876981248</size>\n" + 
		"      <freeSpace>10281259008</freeSpace>\n" + 
		"    </diskPartition>\n" +
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #2</deviceName>\n" + 
		"      <name>E:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>23071875072</size>\n" + 
		"      <freeSpace>14657601536</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #3</deviceName>\n" + 
		"      <name>F:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>4194856960</size>\n" + 
		"      <freeSpace>944488448</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #4</deviceName>\n" + 
		"      <name>G:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>70498840576</size>\n" + 
		"      <freeSpace>9382170624</freeSpace>\n" + 
		"    </diskPartition>\n" + 	
		"  </diskDrive>\n" +
		"  <diskDrive>\n" + 
		"    <model>Maxtor 6B200P0</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE1</deviceName>\n" + 
		"    <size>203921141760</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" +
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #0</deviceName>\n" + 
		"      <name>H:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>52427898880</size>\n" + 
		"      <freeSpace>13749219328</freeSpace>\n" + 
		"    </diskPartition>\n" + 	
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #1</deviceName>\n" + 
		"      <name>I:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>68154634240</size>\n" + 
		"      <freeSpace>9131630592</freeSpace>\n" + 
		"    </diskPartition>\n" + 	
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #2</deviceName>\n" + 
		"      <name>J:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>67110023168</size>\n" + 
		"      <freeSpace>26168156160</freeSpace>\n" + 
		"    </diskPartition>\n" + 	
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #3</deviceName>\n" + 
		"      <name>K:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>16220217344</size>\n" + 
		"      <freeSpace>4785872896</freeSpace>\n" + 
		"    </diskPartition>\n" + 	
		"  </diskDrive>\n" +
		"</diskDrives>\n",
		
		"<diskDrives>\n" +
		"  <diskDrive>\n" + 
		"    <model>Maxtor 6B200P0</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE1</deviceName>\n" + 
		"    <size>203921141760</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" + 
		"  </diskDrive>\n" + 
		"  <diskDrive>\n" + 
		"    <model>Maxtor 6Y120P0</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE0</deviceName>\n" + 
		"    <size>122935034880</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" + 
		"  </diskDrive>\n" +
		"</diskDrives>\n",
		
		"<diskDrives>\n" + 
		"  <diskDrive>\n" + 
		"    <model>VMware Virtual IDE Hard Drive</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE0</deviceName>\n" + 
		"    <size>3220439040</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #0, Partition #0</deviceName>\n" + 
		"      <name>G:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>3216277504</size>\n" + 
		"      <freeSpace>1063141376</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"  </diskDrive>\n" + 
		"  <diskDrive>\n" + 
		"    <model>VMware, VMware Virtual S SCSI Disk Device</model>\n" + 
		"    <deviceName>\\\\.\\PHYSICALDRIVE1</deviceName>\n" + 
		"    <size>106954752</size>\n" + 
		"    <mediaType>HDD</mediaType>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #0</deviceName>\n" + 
		"      <name>C:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>41926144</size>\n" + 
		"      <freeSpace>39343104</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"    <diskPartition>\n" + 
		"      <deviceName>Disk #1, Partition #1</deviceName>\n" + 
		"      <name>E:</name>\n" + 
		"      <fileSystem>NTFS</fileSystem>\n" + 
		"      <size>41926144</size>\n" + 
		"      <freeSpace>39343104</freeSpace>\n" + 
		"    </diskPartition>\n" + 
		"  </diskDrive>\n" +
		"</diskDrives>\n"
	};

	/**
	 * Array containing all drive nodes from one config file (including Been Disk).
	 */
	private static final String []ALL_DRIVES = {

		DISK_DRIVE_NODES[2] +
		"<beenDisk>\n" + 
		"  <path>g:\\Progs\\Eclipse\\Been</path>\n" + 
		"  <size>3216277504</size>\n" + 
		"  <freeSpace>1063145472</freeSpace>\n" + 
		"</beenDisk>\n",
		
		DISK_DRIVE_NODES[0] + 
		"<beenDisk>\n" + 
		"<path>f:\\progs\\eclipse\\been</path>\n" + 
		"<size>4194856960</size>\n" + 
		"<freeSpace>951934976</freeSpace>\n" + 
		"</beenDisk>\n",
		
		DISK_DRIVE_NODES[1] +
		"<beenDisk>\n" + 
		"<path>f:\\progs\\eclipse\\been</path>\n" + 
		"<size>4194856960</size>\n" + 
		"<freeSpace>951934976</freeSpace>\n" + 
		"</beenDisk>\n"
	};
	
	/**
	 * Array containing example application nodes.
	 */
	private static final String []APPLICATIONS = {
		
		"  <product>\n" + 
		"    <name>Application 1</name>\n" + 
		"    <vendor>Rich company</vendor>\n" + 
		"    <version>10.5-beta</version>\n" + 
		"  </product>\n",
		
		"  <product>\n" + 
		"    <name>Application 1</name>\n" + 
		"    <vendor>Rich Company(r)</vendor>\n" + 
		"    <version>10.6</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Application 1</name>\n" + 
		"    <vendor>Rich company</vendor>\n" + 
		"    <version>4.6</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Cool Game</name>\n" + 
		"    <vendor>INDIE games</vendor>\n" + 
		"    <version>1.0.0.1</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Useful tool(tm) trial version</name>\n" + 
		"    <vendor>Tool producers</vendor>\n" + 
		"    <version>2.6</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>My beloved vaporware</name>\n" + 
		"    <vendor>PHANTOM computing INC (tm) (r)</vendor>\n" + 
		"    <version>0.1.7</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Another useful tool</name>\n" + 
		"    <vendor>Tool producers</vendor>\n" + 
		"    <version>20061403-247a</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Spyware crap</name>\n" + 
		"    <vendor>LOL A01!!</vendor>\n" + 
		"    <version>1.1</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Some(r) cool(r) editor (tm)</name>\n" + 
		"    <vendor>We r teh cool ones</vendor>\n" + 
		"    <version>6.4</version>\n" + 
		"  </product>\n",

		"  <product>\n" + 
		"    <name>Hotfix ABCDEFGH987</name>\n" + 
		"    <vendor>MS</vendor>\n" + 
		"    <version>1.0.0.1ac</version>\n" + 
		"  </product>\n",
		
		"  <product>\n" + 
		"    <name>Microsoft .NET Framework 2.0</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>2.0.50727</version>\n" + 
		"  </product>\n",
		
		"  <product>\n" + 
		"    <name>WPF Beta 2 Lang Pack (DEU) v3.0.6327.0</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>3.0.6327.0</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Professional 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Access MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Excel MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office PowerPoint MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Publisher MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Outlook MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Word MUI (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Microsoft Office Proof (English) 2007 (Beta)</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>12.0.4017.1006</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Windows Workflow Foundation JA Language Pack</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>3.0.3807.7</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>Windows Workflow Foundation DE Language Pack</name>\n" + 
		"    <vendor>Microsoft Corporation</vendor>\n" + 
		"    <version>3.0.3807.7</version>\n" + 
		"  </product>\n"
	};
	
	/**
	 * Mandatory application which have to be installed for BEEN to work. These are
	 * appended at the end of the app list of every host.
	 */
	private static final String MANDATORY_APPS = 
		"  <product>\n" + 
		"    <name>J2SE Runtime Environment 5.0 Update 5</name>\n" + 
		"    <vendor>Sun Microsystems, Inc.</vendor>\n" + 
		"    <version>1.5.0.50</version>\n" + 
		"  </product>\n" + 
		"  <product>\n" + 
		"    <name>J2SE Development Kit 5.0 Update 5</name>\n" + 
		"    <vendor>Sun Microsystems, Inc.</vendor>\n" + 
		"    <version>1.5.0.50</version>\n" + 
		"  </product>\n" +
		"  <product>\n" + 
		"    <name>Apache Tomcat 5.5 (remove only)</name>\n" + 
		"    <vendor>(unknown)</vendor>\n" + 
		"    <version>(unknown)</version>\n" + 
		"  </product>\n";
	
	/**
	 * Java info nodes.
	 */
	private static final String []JAVA_INFO = {
		
		"<javaInfo>\n" + 
		"  <version>1.5.0_05</version>\n" + 
		"  <vendor>Sun Microsystems Inc.</vendor>\n" + 
		"  <runtimeName>Java(TM) 2 Runtime Environment, Standard Edition</runtimeName>\n" + 
		"  <vmVersion>1.5.0_05-b05</vmVersion>\n" + 
		"  <vmVendor>Sun Microsystems Inc.</vmVendor>\n" + 
		"  <runtimeVersion>1.5.0_05-b05</runtimeVersion>\n" + 
		"  <specification>1.5</specification>\n" + 
		"</javaInfo>\n",
		
		"<javaInfo>\n" + 
		"  <version>1.5.0_07</version>\n" + 
		"  <vendor>Sun Microsystems Inc.</vendor>\n" + 
		"  <runtimeName>Java(TM) 2 Runtime Environment, Standard Edition</runtimeName>\n" + 
		"  <vmVersion>1.5.0_07-b03</vmVersion>\n" + 
		"  <vmVendor>Sun Microsystems Inc.</vmVendor>\n" + 
		"  <runtimeVersion>1.5.0_07-b03</runtimeVersion>\n" + 
		"  <specification>1.5</specification>\n" + 
		"</javaInfo>\n"
	};	
}
