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
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();
	
	String hostname = (String)application.getAttribute("hostname");
	OperationHandle handle = (OperationHandle)application.getAttribute("handle");
%>
<table class="form center-block">
	<tr>
		<td colspan="2"><h2>Refreshing host...</h2></td>
	</tr>
	<tr>
		<td><img id="status-image" src="../../img/loading.gif" width="64" height="64" /></td>
		<td>
			<table class="form">
				<tr>
					<th>Hostname:</th>
					<td><%=Routines.htmlspecialchars(hostname)%></td>
				</tr>
				<tr>
					<th>Status:</th>
					<td id="status-message-container">
						<span id="status-message">Loading status information...</span>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">hostsHostRefreshingLoad("<%=Routines.htmlspecialchars(Routines.javaScriptEscape(hostname))%>", <%=handle.toString()%>);</script>