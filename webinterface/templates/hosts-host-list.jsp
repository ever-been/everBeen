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
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();

	List hosts = (List)application.getAttribute("hosts");
	Map hostStatusMap = (Map)application.getAttribute("hostStatusMap");
%>
<% if (hosts.size() > 0) { %>
	<table class="list">
		<tr>
			<th>Host</th>
			<th>Status</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		<% for (int i = 0; i < hosts.size(); i++) { %>
			<% HostInfoInterface host = (HostInfoInterface)hosts.get(i); %>
			<tr>
				<th>
					<a href="<%=page_.actionURL("host-details")%>?hostname=<%=Routines.htmlspecialchars(host.getHostName())%>"
						><%=Routines.htmlspecialchars(host.getHostName())%>
					</a>
				</th>
				<% HostStatus status = (HostStatus)hostStatusMap.get(host.getHostName()); %>
				<td
					class="host-status-<%=status != null ? Routines.htmlspecialchars(status.toString()) : "null"%>"
				><%=status != null ? Routines.htmlspecialchars(status.toString()) : "N/A" %></td>
				<td class="button-spacer">&nbsp;</td>
				<td>
					<form action="<%=page_.actionURL("host-delete")%>"
						onsubmit="return confirm('Do you really want to delete host &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(host.getHostName()))%>&quot;?');">
						<input type="hidden" name="hostname" value="<%=Routines.htmlspecialchars(host.getHostName())%>" />
						<input type="submit" class="type-submit" value="Delete" />
					</form>
				</td>
			</tr>
		<% } %>
	</table>
<% } else { %>
	<p class="center">No hosts entered.  You can add hosts using
	<a href="<%=page_.actionURL("host-add")%>">Add host</a> page.</p>
<% } %>