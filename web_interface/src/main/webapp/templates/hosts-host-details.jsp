<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: David Majda

  GNU Lesser General Public License Version 2.1
  ---------------------------------------------
  Copyright (C) 2004-2006 Distributed Systems Research Group,
  Faculty of Mathematics and Physics, Charles University in Prague

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1, as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
  MA  02111-1307  USA

--%><%@
	include file="includes.jsp"
%><%@ page
	import="cz.cuni.mff.been.hostmanager.*"
	import="cz.cuni.mff.been.hostmanager.database.*"
	import="cz.cuni.mff.been.hostmanager.load.*"
	import="cz.cuni.mff.been.taskmanager.*"
	import="cz.cuni.mff.been.taskmanager.data.*"
	import="cz.cuni.mff.been.webinterface.tasks.*"
	import="java.text.*"
	import="cz.cuni.mff.been.logging.*"
	import="java.util.EnumSet"
%><%page_.writeInfoMessages();
	page_.writeErrorMessages();

	Date date = (Date)application.getAttribute("date");
	HostInfoInterface hostInfo = (HostInfoInterface)application.getAttribute("hostInfo");
	Date[] dates = (Date[])application.getAttribute("dates");
	Map userProperties = (Map)application.getAttribute("userProperties");
	String newUserPropertyName = (String)application.getAttribute("newUserPropertyName");
	String newUserPropertyType = (String)application.getAttribute("newUserPropertyType");
	String newUserPropertyValue = (String)application.getAttribute("newUserPropertyValue");
	HostDataStatisticianInterface loadData = (HostDataStatisticianInterface)application.getAttribute("loadData");
	TaskEntryImplementation[] tasks = (TaskEntryImplementation[])application.getAttribute("tasks");
	Map checkpoints = (Map)application.getAttribute("checkpoints");
	LogRecord[] logRecords = (LogRecord[])application.getAttribute("logRecords");
	EnumSet logFields = (EnumSet)application.getAttribute("logFields");
	String activeSheet = (String)application.getAttribute("activeSheet");

	NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
	numberFormat.setMaximumFractionDigits(1);

	HashMap data = new HashMap();%>
<div class="tabsheet">
	<ul class="tabsheet-tabs">
		<li class="active"><a href="#" onclick="tabsheetActivate(this, 'configuration-sheet'); return false;">Configuration</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'load-sheet'); return false;">Load</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'tasks-sheet'); return false;">Tasks</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'logs-sheet'); return false;">Logs</a></li>
	</ul>
	<div class="tabsheet-sheets">
		<div id="configuration-sheet" class="tabsheet-sheet-visible">
			<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
				<table class="full-width">
					<tr>
						<td class="left">
							<form action="<%=page_.actionURL("host-refresh")%>">
								<input type="hidden" name="hostname"
									value="<%=Routines.htmlspecialchars(hostInfo.getHostName())%>" />
								<input type="submit" class="type-submit" name="refresh" value="Refresh" />
							</form>
						</td>
						<td class="center">
							<form action="<%=page_.currentActionURL()%>">
								<input type="hidden" name="hostname"
									value="<%=Routines.htmlspecialchars(hostInfo.getHostName())%>" />
								<select name="date">
									<% for (int i = 0; i < dates.length; i++) { %>
										<option
											value="<%=Long.toString(dates[i].getTime())%>"
											<%=dates[i].equals(date) ? " selected=\"selected\"" : ""%>
										><%=Routines.htmlspecialchars(dates[i].toString())%></option>
									<% } %>
								</select>
								<input type="submit" class="type-submit" name="select" value="Select" />
							</form>
						</td>
					</tr>
				</table>
			</div></div></div></div></div></div>
			<div class="tabsheet">
				<ul class="tabsheet-tabs">
					<li<%=activeSheet.equals("general-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'general-sheet'); return false;">General</a></li>
					<li<%=activeSheet.equals("operating-system-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'operating-system-sheet'); return false;">Operating System</a></li>
					<li<%=activeSheet.equals("java-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'java-sheet'); return false;">Java</a></li>
					<li<%=activeSheet.equals("processors-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'processors-sheet'); return false;">Processors</a></li>
					<li<%=activeSheet.equals("memory-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'memory-sheet'); return false;">Memory</a></li>
					<li<%=activeSheet.equals("disk-drives-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'disk-drives-sheet'); return false;">Disk drives</a></li>
					<li<%=activeSheet.equals("network-adapters-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'network-adapters-sheet'); return false;">Network adapters</a></li>
					<li<%=activeSheet.equals("applications-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'applications-sheet'); return false;">Applications</a></li>
					<li<%=activeSheet.equals("user-properties-sheet") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'user-properties-sheet'); return false;">User-defined properties</a></li>
				</ul>
				<div class="tabsheet-sheets">
					<div id="general-sheet"<%=activeSheet.equals("general-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<table class="form">
							<tr>
								<th>Hostname:</th>
								<td><%=Routines.htmlspecialchars(hostInfo.getHostName())%></td>
							</tr>
							<tr>
								<th>Detector:</th>
								<td><%=Routines.htmlspecialchars(hostInfo.getDetectorIDString())%></td>
							</tr>
							<tr>
								<th>Last checked:</th>
								<td><%=Routines.htmlspecialchars(hostInfo.getCheckDate())%> <%=Routines.htmlspecialchars(hostInfo.getCheckTime())%></td>
							</tr>
						</table>
					</div>

					<div id="operating-system-sheet"<%=activeSheet.equals("operating-system-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<% OperatingSystem os = hostInfo.getOperatingSystem(); %>
						<table class="form">
							<tr>
								<th>Name:</th>
								<td><%=Routines.htmlspecialchars(os.getName())%></td>
							</tr>
							<tr>
								<th>Vendor:</th>
								<td><%=Routines.htmlspecialchars(os.getVendor())%></td>
							</tr>
							<tr>
								<th>Architecture:</th>
								<td><%=Routines.htmlspecialchars(os.getArchitecture())%></td>
							</tr>
							<% if (os instanceof WindowsOperatingSystem) { %>
								<tr>
									<th>Version:</th>
									<td><%=Routines.htmlspecialchars(((WindowsOperatingSystem)os).getVersion())%></td>
								</tr>
								<tr>
									<th>Service pack version:</th>
									<td><%=Routines.htmlspecialchars(((WindowsOperatingSystem)os).getServicePackVersion())%></td>
								</tr>
								<tr>
									<th>Build type:</th>
									<td><%=Routines.htmlspecialchars(((WindowsOperatingSystem)os).getBuildType())%></td>
								</tr>
								<tr>
									<th>Encryption level:</th>
									<td><%=Integer.toString(((WindowsOperatingSystem)os).getEncryptionLevel())%>&nbsp;bits</td>
								</tr>
								<tr>
									<th>Windows directory:</th>
									<td><%=Routines.htmlspecialchars(((WindowsOperatingSystem)os).getWindowsDirectory())%></td>
								</tr>
								<tr>
									<th>System directory:</th>
									<td><%=Routines.htmlspecialchars(((WindowsOperatingSystem)os).getSystemDirectory())%></td>
								</tr>
							<% }%>
							<% if (os instanceof LinuxOperatingSystem) { %>
								<tr>
									<th>Distribution name:</th>
									<td><%=Routines.htmlspecialchars(((LinuxOperatingSystem)os).getDistributionName())%></td>
								</tr>
								<tr>
									<th>Distribution version:</th>
									<td><%=Routines.htmlspecialchars(((LinuxOperatingSystem)os).getDistributionVersion())%></td>
								</tr>
								<tr>
									<th>Kernel version:</th>
									<td><%=Routines.htmlspecialchars(((LinuxOperatingSystem)os).getKernelVersion())%></td>
								</tr>
								<tr>
									<th>OS release:</th>
									<td><%=Routines.htmlspecialchars(((LinuxOperatingSystem)os).getOSRelease())%></td>
								</tr>
								<tr>
									<th>OS version:</th>
									<td><%=Routines.htmlspecialchars(((LinuxOperatingSystem)os).getOSVersion())%></td>
								</tr>
							<% }%>
						</table>
					</div>
		
					<div id="java-sheet"<%=activeSheet.equals("java-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<% JavaInfo javaInfo = hostInfo.getJavaInfo(); %>
						<table class="form">
							<tr>
								<th>Vendor:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getJavaVendor())%></td>
							</tr>
							<tr>
								<th>Version:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getJavaVersion())%></td>
							</tr>
							<tr>
								<th>Specification version:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getSpecificationVersion())%></td>
							</tr>
							<tr>
								<th>Runtime name:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getRuntimeName())%></td>
							</tr>
							<tr>
								<th>Runtime version:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getRuntimeVersion())%></td>
							</tr>
							<tr>
								<th>Virtual machine vendor:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getVMVendor())%></td>
							</tr>
							<tr>
								<th>Virtual machine version:</th>
								<td><%=Routines.htmlspecialchars(javaInfo.getVMVersion())%></td>
							</tr>
						</table>
					</div>

					<div id="processors-sheet"<%=activeSheet.equals("processors-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<table class="form">
							<tr>
								<td colspan="2">
									<%
										int processorCount = hostInfo.getProcessorCount();
										if (processorCount > 0) {
									%>
										<table class="real">
											<tr>
												<th>Model</th>
												<th>Vendor</th>
												<th>Speed</th>
												<th>Cache size</th>
											</tr>
											<%
												for (int i = 0; i < processorCount; i++) {
												  Processor processor = hostInfo.getProcessor(i);
											%>
												<tr>
													<td><%=Routines.htmlspecialchars(processor.getModelName())%></td>
													<td><%=Routines.htmlspecialchars(processor.getVendorName())%></td>
													<td class="right"><%=numberFormat.format((double)processor.getSpeed() / (double)1000000)%> MHz</td>
													<td class="right"><%=Routines.formatNumberWithPrefixedUnit(processor.getCacheSize(), "B")%></td>
												</tr>
											<% } %>
										</table>
									<% } else { %>
										No processors found.
									<% } %>
								</td>
							</tr>
						</table>
					</div>

					<div id="memory-sheet"<%=activeSheet.equals("memory-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<% Memory memory = hostInfo.getMemory(); %>
						<table class="form">
							<tr>
								<th>Physical size:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(memory.getPhysicalMemorySize(), "B")%></td>
							</tr>
							<tr>
								<th>Virtual memory size:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(memory.getVirtualMemorySize(), "B")%></td>
							</tr>
							<tr>
								<th>Swap space:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(memory.getSwapSize(), "B")%></td>
							</tr>
							<tr>
								<th>Paging files size:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(memory.getPagingSize(), "B")%></td>
							</tr>
						</table>
					</div>

					<div id="disk-drives-sheet"<%=activeSheet.equals("disk-drives-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<%
							int diskDriveCount = hostInfo.getDriveCount();
							if (diskDriveCount > 0) {
						%>
							<table class="form">
								<%
									for (int i = 0; i < diskDriveCount; i++) {
									  DiskDrive diskDrive = hostInfo.getDiskDrive(i);
								%>
									<tr>
										<td colspan="2"><h3>Drive <%=i+1%></h3></td>
									</tr>
									<tr>
										<th class="indented">Model name:</th>
										<td><%=Routines.htmlspecialchars(diskDrive.getModelName())%></td>
									</tr>
									<tr>
										<th class="indented">Device name:</th>
										<td><%=Routines.htmlspecialchars(diskDrive.getDeviceName())%></td>
									</tr>
									<tr>
										<th class="indented">Media type:</th>
										<td><%=Routines.htmlspecialchars(diskDrive.getMediaType())%></td>
									</tr>
									<tr>
										<th class="indented">Size:</th>
										<td><%=Routines.formatNumberWithPrefixedUnit(diskDrive.getSize(), "B")%></td>
									</tr>
									<tr>
										<th class="indented">Partitions:</th>
										<td>
											<%
												int diskPartitionCount = diskDrive.getPartitionCount();
												if (diskPartitionCount > 0) {
											%>
												<table class="real">
													<tr>
														<th>Name</th>
														<th>Device name</th>
														<th>Filesystem</th>
														<th>Size</th>
														<th>Free space</th>
													</tr>
													<%
														for (int j = 0; j < diskPartitionCount; j++) {
														  DiskPartition diskPartition = diskDrive.getPartition(j);
													%>
														<tr>
															<td><%=Routines.htmlspecialchars(diskPartition.getName())%></td>
															<td><%=Routines.htmlspecialchars(diskPartition.getDeviceName())%></td>
															<td><%=Routines.htmlspecialchars(diskPartition.getFileSystemName())%></td>
															<td class="right"><%=Routines.formatNumberWithPrefixedUnit(diskPartition.getSize(), "B")%></td>
															<td class="right"><%=Routines.formatNumberWithPrefixedUnit(diskPartition.getFreeSpace(), "B")%></td>
														</tr>
													<% } %>
												</table>
											<% } else { %>
												No partition data available.
											<% } %>
										</td>
									</tr>
								<% } %>
							<% } else { %>
								No disk drives found.
							<% } %>

							<tr>
								<td colspan="2"><hr /></td>
							</tr>
							<tr>
								<td colspan="2"><h3>BEEN disk drive</h3></td>
							</tr>
							<% BeenDisk beenDisk = hostInfo.getBeenDisk(); %>
							<tr>
								<th class="indented">BEEN home directory:</th>
								<td><%=Routines.htmlspecialchars(beenDisk.getBeenHomePath())%></td>
							</tr>
							<tr>
								<th class="indented">Size:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(beenDisk.getDiskSize(), "B")%></td>
							</tr>
							<tr>
								<th class="indented">Free space:</th>
								<td><%=Routines.formatNumberWithPrefixedUnit(beenDisk.getDiskFree(), "B")%></td>
							</tr>
						</table>
					</div>

					<div id="network-adapters-sheet"<%=activeSheet.equals("network-adapters-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<table class="form">
							<tr>
								<td colspan="2">
									<%
										int networkAdapterCount = hostInfo.getNetworkAdapterCount();
										if (networkAdapterCount > 0) {
									%>
										<table class="real">
											<tr>
												<th>Name</th>
												<th>Vendor</th>
												<th>Type</th>
												<th>MAC address</th>
											</tr>
											<%
												for (int i = 0; i < networkAdapterCount; i++) {
													NetworkAdapter networkAdapter = hostInfo.getNetworkAdapter(i);
											%>
												<tr>
													<td><%=Routines.htmlspecialchars(networkAdapter.getName())%></td>
													<td><%=Routines.htmlspecialchars(networkAdapter.getVendor())%></td>
													<td><%=Routines.htmlspecialchars(networkAdapter.getType())%></td>
													<td><%=Routines.htmlspecialchars(networkAdapter.getMacAddress())%></td>
												</tr>
											<% } %>
										</table>
									<% } else { %>
										No network adapters found.
									<% } %>
								</td>
							</tr>
						</table>
					</div>

					<div id="applications-sheet"<%=activeSheet.equals("applications-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<h2>Applications</h2>
						<table class="form">
							<tr>
								<td colspan="2">
									<%
										int productCount = hostInfo.getProductCount();
										if (productCount > 0) {
									%>
										<table class="real">
											<tr>
												<th>Name</th>
												<th>Vendor</th>
												<th>Version</th>
											</tr>
											<%
												for (int i = 0; i < productCount; i++) {
												  Product product = hostInfo.getProduct(i);
											%>
												<tr>
													<td><%=Routines.htmlspecialchars(product.getName())%></td>
													<td><%=Routines.htmlspecialchars(product.getVendor())%></td>
													<td><%=Routines.htmlspecialchars(product.getVersion())%></td>
												</tr>
											<% } %>
										</table>
									<% } else { %>
										No applications found.
									<% } %>
								</td>
							</tr>
						</table>
						<h2>Software aliases</h2>
						<table class="form">
							<tr>
								<td colspan="2">
									<%
										int aliasCount = hostInfo.getSoftwareAliasCount();
										if (aliasCount > 0) {
									%>
										<table class="real">
											<tr>
												<th>Alias name</th>
												<th>Application name</th>
												<th>Vendor</th>
												<th>Version</th>
											</tr>
											<%
												for (int i = 0; i < aliasCount; i++) {
												  SoftwareAlias alias = hostInfo.getSoftwareAlias(i);
											%>
												<tr>
													<td><%=Routines.htmlspecialchars(alias.getAliasName())%></td>
													<td><%=Routines.htmlspecialchars(alias.getProductName())%></td>
													<td><%=Routines.htmlspecialchars(alias.getProductVendor())%></td>
													<td><%=Routines.htmlspecialchars(alias.getProductVersion())%></td>
												</tr>
											<% } %>
										</table>
									<% } else { %>
										No software aliases found.
									<% } %>
								</td>
							</tr>
						</table>
					</div>

					<div id="user-properties-sheet"<%=activeSheet.equals("user-properties-sheet") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
						<form id="user-properties-form" action="<%=page_.currentActionURL()%>" method="post">
							<input type="hidden" name="hostname"
								value="<%=Routines.htmlspecialchars(hostInfo.getHostName())%>" />
							<table class="form">
								<tr>
									<td colspan="2">
										<table class="list">
											<tr class="header"><td colspan="3"><h2>User property values</h2></td></tr>
											<%
												Iterator it = hostInfo.getUserPropertiesIterator();
												if (it.hasNext()) {
											%>
												<tr>
													<th>Name</th>
													<th>Value</th>
													<th>&nbsp;</th>
												</tr>
												<%
													while (it.hasNext()) {
													  NameValuePair property = (NameValuePair)it.next();
												%>
													<tr>
														<td><%=Routines.htmlspecialchars(property.getName())%>:</td>
														<td>
															<%
																data.clear();
																data.put("name", property.getName());
																data.put("klass", property.getValue().getClass());
																data.put("value", userProperties.get(property.getName()));
																out.flush(); // unforunately can't be inside writeTemplate method
																page_.writeTemplate("hosts-value-widget", data);
															%>
														</td>
														<td>
															<input type="submit" class="type-submit"
																name="delete[<%=property.getName()%>]"
																value="Delete"
															/>
														</td>
													</tr>
												<% } %>
												<tr>
													<td colspan="3" class="buttons">
														<input type="submit" class="type-submit" name="edit-all" value="Save values" />
													</td>
													
												</tr>
											<% } else { %>
												<tr>
													<td colspan="3" class="center">No user-defined properties set.</td>
												</tr>
											<% } %>
											<tr class="header"><td colspan="3"><h2>Add new value</h2></td></tr>
											<tr>
												<th>Name</th>
												<th>Value</th>
												<th>&nbsp;</th>
											</tr>
											<tr class="add">
												<td>
													<input type="text" class="type-text"
														name="new-name"
														value="<%=Routines.htmlspecialchars(newUserPropertyName)%>"
													/>
												</td>
												<td id="new-value-cell">
													<select name="new-type" onchange="userPropertiesFormNewTypeChange();">
														<option value="boolean"<%=newUserPropertyType.equals("boolean") ? "selected=\"selected\"" : ""%>>boolean</option>
														<option value="integer"<%=newUserPropertyType.equals("integer") ? "selected=\"selected\"" : ""%>>integer</option>
														<option value="double"<%=newUserPropertyType.equals("double") ? "selected=\"selected\"" : ""%>>double</option>
														<option value="string"<%=newUserPropertyType.equals("string") ? "selected=\"selected\"" : ""%>>string</option>
														<option value="regexp"<%=newUserPropertyType.equals("regexp") ? "selected=\"selected\"" : ""%>>regexp</option>
														<option value="version"<%=newUserPropertyType.equals("version") ? "selected=\"selected\"" : ""%>>version</option>
													</select>
													<span id="new-text-value-container">
														<input type="text" class="type-text"
															name="new-value-text"
															value="<%=newUserPropertyType.equals("boolean") ? "" : Routines.htmlspecialchars(newUserPropertyValue)%>"
														/>
													</span>
													<span id="new-boolean-value-container">
														<label><input type="radio"
															name="new-value-boolean"
															value="true"
															<%=(newUserPropertyType.equals("boolean") && newUserPropertyValue.equals("true")) ? "checked=\"checked\"" : "" %>
														/>true</label>
														<label><input type="radio"
															name="new-value-boolean"
															value="false"
															<%=!(newUserPropertyType.equals("boolean") && newUserPropertyValue.equals("true")) ? "checked=\"checked\"" : "" %>
														/>false</label>
													</span>
												</td>
												<td>
													<input type="submit" class="type-submit" name="add" value="Add" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div id="load-sheet" class="tabsheet-sheet-invisible">
			<% if (loadData != null && loadData.getLastTimestamp(LoadMonitorEvent.EventType.MONITOR_SAMPLE) != null) { %>
				<table class="form">
					<%
						long now = loadData.getLastTimestamp(LoadMonitorEvent.EventType.MONITOR_SAMPLE).longValue();
						long tenMinutesAgo = now - 10 * 60 * 1000 * 10000;
						long hourAgo = now - 60 * 60 * 1000  * 10000;
					%>
					<tr>
						<td colspan="2"><h2>Current load</h2></td>
					</tr>
					<%
						data.clear();
						data.put("loadData", loadData);
						data.put("start", now);
						data.put("end", now);
						data.put("maximumFractionDigits", 0);
						out.flush(); // unforunately can't be inside writeTemplate method
						page_.writeTemplate("hosts-host-details-load-average", data);
					%>
					<tr>
						<td colspan="2"><h2>Last 10 minutes average</h2></td>
					</tr>
					<%
						data.clear();
						data.put("loadData", loadData);
						data.put("start", tenMinutesAgo);
						data.put("end", now);
						data.put("maximumFractionDigits", 2);
						out.flush(); // unforunately can't be inside writeTemplate method
						page_.writeTemplate("hosts-host-details-load-average", data);
					%>
					<tr>
						<td colspan="2"><h2>Last hour average</h2></td>
					</tr>
					<%
						data.clear();
						data.put("loadData", loadData);
						data.put("start", hourAgo);
						data.put("end", now);
						data.put("maximumFractionDigits", 2);
						out.flush(); // unforunately can't be inside writeTemplate method
						page_.writeTemplate("hosts-host-details-load-average", data);
					%>
				</table>
			<% } else { %>
				<p class="center">Load information not available.</p>
			<% } %>
		</div>
		<div id="tasks-sheet" class="tabsheet-sheet-invisible">
			<%
				data.clear();
				data.put("tasks", tasks);
				data.put("checkpoints", checkpoints);
				data.put("showHost", false);
				data.put("showContext", true);
				data.put("mode", TaskListMode.NORMAL);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("tasks-task-list", data);
			%>
		</div>
		<div id="logs-sheet" class="tabsheet-sheet-invisible">
			<%
				data.clear();
				data.put("logRecords", logRecords);
				data.put("logFields", logFields);
				data.put("logEntityColumns", new LogEntityColumn[] {});
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("log-view", data);
			%>
		</div>
	</div>
</div>
<script type="text/javascript">
	userPropertiesFormNewTypeChange();
</script>