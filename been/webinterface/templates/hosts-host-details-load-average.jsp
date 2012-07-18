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
	import="cz.cuni.mff.been.hostmanager.load.*"
	import="cz.cuni.mff.been.common.*"
	import="java.text.*"
%><%
	HostDataStatisticianInterface loadData = (HostDataStatisticianInterface)application.getAttribute("loadData");
	long start = ((Long)application.getAttribute("start")).longValue();
	long end = ((Long)application.getAttribute("end")).longValue();
	int maximumFractionDigits = ((Integer)application.getAttribute("maximumFractionDigits")).intValue();

	NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
	numberFormat.setMaximumFractionDigits(maximumFractionDigits);
%>
<tr>
	<th>Memory free:</th>
	<td><%=Routines.formatNumberWithPrefixedUnit((long)loadData.getFreeMemoryStats(start, end).getAverage(), "B")%></td>
</tr>
<tr>
	<th>Process count:</th>
	<td><%=numberFormat.format(loadData.getProcessCountStats(start, end).getAverage())%></td>
</tr>
<tr>
	<th>Processor queue length:</th>
	<td><%=numberFormat.format(loadData.getProcessQueueLengthStats(start, end).getAverage())%></td>
</tr>
<tr>
	<th>Processors:</th>
	<td>
		<% List processorStats = loadData.getProcessorStats(start, end); %>
		<% if (processorStats.size() > 0) { %>
			<table class="real">
				<tr>
					<th>Name</th>
					<th>Load</th>
				</tr>
				<% for (int i = 0; i < processorStats.size(); i++) { %>
					<% ValueStatistics stats = (ValueStatistics)processorStats.get(i); %>
					<tr>
						<td><%=Routines.htmlspecialchars(stats.getName())%></td>
						<td class="right"><%=numberFormat.format(stats.getAverage())%> %</td>
					</tr>
				<% } %>
			</table>
		<% } else { %>
			No processor data available.
		<% } %>
	</td>
</tr>
<tr>
	<th>Drives:</th>
	<td>
		<% List driveStats = loadData.getDriveStats(start, end); %>
		<% if (driveStats.size() > 0) { %>
			<table class="real">
				<tr>
					<th>Name</th>
					<th>Read</th>
					<th>Write</th>
				</tr>
				<% for (int i = 0; i < driveStats.size(); i++) { %>
					<% Pair stats = (Pair)driveStats.get(i); %>
					<tr>
						<td><%=Routines.htmlspecialchars(((ValueStatistics)stats.getKey()).getName())%></td>
						<td class="right"><%=Routines.formatNumberWithPrefixedUnit((long)((ValueStatistics)stats.getKey()).getAverage(), "B/s")%></td>
						<td class="right"><%=Routines.formatNumberWithPrefixedUnit((long)((ValueStatistics)stats.getValue()).getAverage(), "B/s")%></td>
					</tr>
				<% } %>
			</table>
		<% } else { %>
			No drive data available.
		<% } %>
	</td>
</tr>
<tr>
	<th>Network:</th>
	<td>
		<% List networkStats = loadData.getNetworkStats(start, end); %>
		<% if (networkStats.size() > 0) { %>
			<table class="real">
				<tr>
					<th>Name</th>
					<th>Read</th>
					<th>Write</th>
				</tr>
				<% for (int i = 0; i < networkStats.size(); i++) { %>
					<% Pair stats = (Pair)networkStats.get(i); %>
					<tr>
						<td><%=Routines.htmlspecialchars(((ValueStatistics)stats.getKey()).getName())%></td>
						<td class="right"><%=Routines.formatNumberWithPrefixedUnit((long)((ValueStatistics)stats.getKey()).getAverage(), "B/s")%></td>
						<td class="right"><%=Routines.formatNumberWithPrefixedUnit((long)((ValueStatistics)stats.getValue()).getAverage(), "B/s")%></td>
					</tr>
				<% } %>
			</table>
		<% } else { %>
			No network data available.
		<% } %>
	</td>
</tr>
