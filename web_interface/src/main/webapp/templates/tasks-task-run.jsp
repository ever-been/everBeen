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
	import="cz.cuni.mff.been.taskmanager.*"
	import="cz.cuni.mff.been.taskmanager.data.*"
	import="cz.cuni.mff.been.softwarerepository.*"
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();
	
	String taskRunType = (String)application.getAttribute("taskRunType");
	String taskName = (String)application.getAttribute("taskName");
	String host = (String)application.getAttribute("host");
	String contextId = (String)application.getAttribute("contextId");
	String properties = (String)application.getAttribute("properties");
	String xml = (String)application.getAttribute("xml");
	PackageMetadata[] packages = (PackageMetadata[])application.getAttribute("packages");
	String[] hosts = (String[])application.getAttribute("hosts");
	ContextEntry[] contexts = (ContextEntry[])application.getAttribute("contexts");
%>
<div class="tabsheet tasks-run-task">
	<ul class="tabsheet-tabs">
		<li<%=taskRunType.equals("gui") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'gui-sheet'); return false;">Describe task using form</a></li>
		<li<%=taskRunType.equals("xml") ? " class=\"active\"" : "" %>><a href="#" onclick="tabsheetActivate(this, 'xml-sheet'); return false;">Describe task using task descriptor XML</a></li>
	</ul>
	<div class="tabsheet-sheets">
		<div id="gui-sheet"<%=taskRunType.equals("gui") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
			<form id="tasks-run-task-gui-form" action="<%=page_.currentActionURL()%>" method="post">
				<input type="hidden" name="task-run-type" value="gui" />
				<table class="form center-block">
					<tr>
						<th class="indented">Task name:</th>
						<td>
							<select name="task-name" class="name-task-name"
								size="<%=Routines.bounded(packages.length, 2, 10)%>"
							>
								<% for (int i = 0; i < packages.length; i++) { %>
									<% PackageMetadata package_ = packages[i]; %>
									<option
										value="<%=Routines.htmlspecialchars(package_.getName())%>"
										<%=package_.getName().equals(taskName) ? " selected=\"selected\"" : ""%>
									><%=Routines.htmlspecialchars(package_.getName())%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<th class="indented">Host:</th>
						<td>
							<select name="host" class="name-host"
								size="<%=Routines.bounded(hosts.length, 2, 10)%>"
							>
								<% for (int i = 0; i < hosts.length; i++) { %>
									<option
										value="<%=Routines.htmlspecialchars(hosts[i])%>"
										<%=hosts[i].equals(host) ? " selected=\"selected\"" : ""%>
									><%=Routines.htmlspecialchars(hosts[i])%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<th class="indented">Context ID:</th>
						<td>
							<select name="context-id" class="name-context-id"
								size="<%=Routines.bounded(contexts.length, 2, 10)%>"
							>
								<% for (int i = 0; i < contexts.length; i++) { %>
									<% ContextEntry context = contexts[i]; %>
									<option
										value="<%=Routines.htmlspecialchars(context.getContextId())%>"
										<%=context.getContextId().equals(contextId) ? " selected=\"selected\"" : ""%>
									><%=Routines.htmlspecialchars(context.getContextId())%></option>
								<% } %>
							</select>
						</td>
					</tr>
					<tr>
						<th class="indented">Properties:</th>
						<td>
							<textarea name="properties" class="name-properties"
								rows="5" cols="50"
							><%=Routines.htmlspecialchars(properties)%></textarea><br />
							<div class="note">
								Enter one property on each line in the format <em>name = value</em>.<br />
								Whitespace around the name and value will be trimmed.
							</div>
						</td>
					</tr>
					<tr>
						<td class="buttons" colspan="2">
							<input type="submit" class="type-submit" name="run" value="Run" />
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div id="xml-sheet"<%=taskRunType.equals("xml") ? " class=\"tabsheet-sheet-visible\"" : " class=\"tabsheet-sheet-invisible\"" %>>
			<form id="tasks-run-task-xml-form" action="<%=page_.currentActionURL()%>" method="post">
				<input type="hidden" name="task-run-type" value="xml" />
				<table class="form center-block">
					<tr>
						<th class="indented">Task descriptor:</th>
						<td>
							<textarea name="xml" class="name-xml"
								rows="5" cols="50"
							><%=Routines.htmlspecialchars(xml)%></textarea>
						</td>
					</tr>
					<tr>
						<td class="buttons" colspan="2">
							<input type="submit" class="type-submit" name="run" value="Run" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</div>