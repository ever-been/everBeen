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
	import="cz.cuni.mff.been.task.*"
	import="cz.cuni.mff.been.webinterface.services.*"
	import="cz.cuni.mff.been.webinterface.services.ServiceInfo.Status"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();
%><%
	ServiceInfo[] services = (ServiceInfo[])application.getAttribute("services");
	boolean showDebugOptions = ((Boolean)application.getAttribute("showDebugOptions")).booleanValue();

	String taskManagerHostname = null;
	if (showDebugOptions) {
		taskManagerHostname = (String)application.getAttribute("taskManagerHostname");
	}
%>
<table class="spacing center-block">
	<%
		boolean canStartSomeService = false;
		boolean canStopSomeService = false;
	%>
	<% for (int i = 0; i < services.length; i++) { %>
	 	<% if (i == 0) { %><tr><td><h2>Execution Framework</h2></td></tr><% } %>
	 	<% if (i == 3) { %><tr><td><h2>Benchmarking and Testing Framework</h2></td></tr><% } %>
	 	<% if (i == 5) { %><tr><td><h2>Command Line Framework</h2></td></tr><% } %>
		<% if (i == 0 || i == 3 || i == 5) { %>
			<tr>
				<td>
					<table class="list">
						<tr>
							<th>Service</th>
							<th>Host</th>
							<th>Status</th>
							<th>&nbsp;</th>
							<% if (showDebugOptions) { %>
								<th>&nbsp;</th>
							<% } %>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
						</tr>
		<% } %>
	 	<%
	 		ServiceInfo service = services[i];
	 		Status status = services[i].getStatus();
	 	
	 		if (status == null) {
	 			canStartSomeService = true;
	 		}
	 		if (status == Status.RUNNING) {
	 			canStopSomeService = true;
	 		}
	 	%>
		<tr>
			<th><%=Routines.htmlspecialchars(service.getHumanName())%></th>
			<td>
				<% if (status != null) { %>
					<%=Routines.htmlspecialchars(service.getHost())%>
				<% } else { %>
					<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-host-form" action="<%=page_.currentActionURL()%>">
						<input type="text" name="host" value="<%=showDebugOptions ? "localhost" : ""%>"
							id="<%=Routines.htmlspecialchars(service.getName())%>-host"
							onchange="servicesListHostChange(this, 'list-<%=Routines.htmlspecialchars(service.getName())%>-start-form');<% if (showDebugOptions) { %>servicesListHostChange(this, 'list-<%=Routines.htmlspecialchars(service.getName())%>-start-debug-form');<% } %>"
						/>
						<div id="<%=Routines.htmlspecialchars(service.getName())%>-autocomplete" class="autocomplete"></div>
					</form>
				<% } %>
			</td>
			<td><%=status != null ? "<span class=\"service-status-" + Routines.htmlspecialchars(status.toString()) + "\">" + Routines.htmlspecialchars(status.toString()) + "</span>" : "<span class=\"service-status-null\">N/A</span>"%></td>
			<td>
				<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-start-form" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="host" value="<%=showDebugOptions ? "localhost" : ""%>" />
					<input type="hidden" name="service" value="<%=Routines.htmlspecialchars(service.getName())%>" />
					<input type="submit" class="type-submit" name="start" value="Start"<%=status != null ? "disabled='disabled'" : ""%> />
				</form>
			</td>
			<% if (showDebugOptions) { %>
				<td>
					<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-start-debug-form" action="<%=page_.currentActionURL()%>">
						<input type="hidden" name="host" value="<%=showDebugOptions ? "localhost" : ""%>" />
						<input type="hidden" name="service" value="<%=Routines.htmlspecialchars(service.getName())%>" />
						<input type="submit" class="type-submit-wide" name="start-debug" value="Start (remote debug)"<%=status != null ? "disabled='disabled'" : ""%> />
					</form>
				</td>
			<% } %>
			<td>
				<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-stop-form" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="service" value="<%=Routines.htmlspecialchars(service.getName())%>" />
					<input type="submit" class="type-submit" name="stop" value="Stop"<%=status != Status.RUNNING ? "disabled='disabled'" : ""%> />
				</form>
			</td>
			<td>
				<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-restart-form" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="service" value="<%=Routines.htmlspecialchars(service.getName())%>" />
					<input type="submit" class="type-submit" name="restart" value="Restart"<%=status != Status.RUNNING ? "disabled='disabled'" : ""%> />
				</form>
			</td>
			<td>
				<form id="list-<%=Routines.htmlspecialchars(service.getName())%>-logs-form" action="<%=page_.actionURL("logs")%>">
					<input type="hidden" name="service" value="<%=Routines.htmlspecialchars(service.getName())%>" />
					<input type="submit" class="type-submit" name="logs" value="Logs" />
				</form>
			</td>
		</tr>
		<% if (i == 2 || i == 4 || i == 5) { %>
					</table>
				</td>
			</tr>
		<% } %>
	<% } %>
	<% if (showDebugOptions) { %>
		<tr>
			<td colspan="8" class="right">
				<form action="<%=page_.currentActionURL()%>">
					<input type="submit" class="type-submit" name="start-all-on-localhost"
						value="Start all services on <%=taskManagerHostname%>"
						<%=!canStartSomeService ? "disabled='disabled'" : ""%>
					/>
					<input type="submit" class="type-submit" name="stop-all"
						value="Stop all services"
						<%=!canStopSomeService ? "disabled='disabled'" : ""%>
					/>
				</form>
			</td>
		</tr>
	<% } %>
</table>
