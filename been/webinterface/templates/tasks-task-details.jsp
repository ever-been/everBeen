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
	import="cz.cuni.mff.been.logging.*"
	import="cz.cuni.mff.been.jaxb.td.*"
	import="java.util.EnumSet"
%><%
	TaskEntry task = (TaskEntry)application.getAttribute("task");
	CheckPoint[] checkpoints = (CheckPoint[])application.getAttribute("checkpoints");
	LogRecord[] logRecords = (LogRecord[])application.getAttribute("logRecords");
	EnumSet logFields = (EnumSet)application.getAttribute("logFields");
	OutputHandle standardOutputHandle = (OutputHandle)application.getAttribute("standardOutputHandle");
	OutputHandle errorOutputHandle = (OutputHandle)application.getAttribute("errorOutputHandle");
%>	
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("context-list")%>">Contexts</a>
	&raquo;
	<a
		href="<%=page_.actionURL("context-details")%>?cid=<%=Routines.htmlspecialchars(task.getContextId())%>"
	><%=Routines.htmlspecialchars(task.getContextId())%></a>
	&raquo;
	<a
		href="<%=page_.currentActionURL()%>?cid=<%=Routines.htmlspecialchars(task.getContextId())%>&amp;tid=<%=Routines.htmlspecialchars(task.getTaskId())%>"
	><%=Routines.htmlspecialchars(task.getTaskId())%></a>
</div></div></div></div></div></div>

<div class="tabsheet">
	<ul class="tabsheet-tabs">
		<li class="active"><a href="#" onclick="tabsheetActivate(this, 'information-sheet'); return false;">Information</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'logs-sheet'); return false;">Logs</a></li>
	</ul>
	<div class="tabsheet-sheets">
		<div id="information-sheet" class="tabsheet-sheet-visible">
<table class="form">
	<tr>
		<td colspan="2"><h2>General</h2></td>
	</tr>
	<tr>
		<th>ID:</th>
		<td><%=Routines.htmlspecialchars(task.getTaskId())%></td>
	</tr>
	<tr>
		<th>Tree address:</th>
		<td><%=Routines.htmlspecialchars(task.getTreePath())%></td>
	</tr>
	<tr>
		<th>Name:</th>
		<td><%=Routines.htmlspecialchars(task.getTaskName())%></td>
	</tr>
	<tr>
		<th>Description:</th>
		<td><%=Routines.htmlspecialchars(task.getTaskDescription())%></td>
	</tr>
	<tr>
		<th>Package name:</th>
		<td>
			<%
				/* Needs to be sorted alphabetically. */
				String[] bootPackages = new String[] {
					"benchmarkmanagerng-1.0.bpk",
					"detectortask-1.0.bpk",
					"hostmanager-1.0.bpk",
					"resultsrepositoryng-1.0.bpk",
					"softwarerepository-1.0.bpk",
				};
				
				if (Arrays.binarySearch(bootPackages, task.getPackageName()) < 0) {
			%>
				<a href="<%=page_.moduleActionURL("packages", "details")%>?package=<%=Routines.htmlspecialchars(task.getPackageName())%>"
				><%=Routines.htmlspecialchars(task.getPackageName())%></a>
			<% } else { %>
				<%=Routines.htmlspecialchars(task.getPackageName())%>
			<% } %>
		</td>
	</tr>
	<tr>
		<th>Host:</th>
		<td>
			<% if (task.getHostName() != null) { %>
				<a href="<%=page_.moduleActionURL("hosts", "host-details")%>?hostname=<%=Routines.htmlspecialchars(task.getHostName())%>"
				><%=Routines.htmlspecialchars(task.getHostName())%></a>
			<% } else { %>
				(unknown)
			<% } %>
		</td>
	</tr>
	<tr>
		<th>Exclusivity:</th>
		<td><%=Routines.htmlspecialchars(task.getExclusivity().toString())%></td>
	</tr>
	<tr>
		<td colspan="2"><h2>Status</h2></td>
	</tr>
	<tr>
		<th>State:</th>
		<td><%=Routines.htmlspecialchars(task.getState().toString())%></td>
	</tr>
	<tr>
		<th>Submitted:</th>
		<td>
			<% long timeSubmitted = task.getTimeSubmitted(); %>
			<%=timeSubmitted != 0
				? new Date(timeSubmitted).toString()
					+ " (" 
					+ Routines.formatMillisAsHMS(task.getCurrentTime() - timeSubmitted)
					+ " ago)"
				: "N/A"
			%>
		</td>
	</tr>
	<tr>
		<th>Started:</th>
		<td>
			<% long timeStarted = task.getTimeStarted(); %>
			<%=timeStarted != 0
				? new Date(timeStarted).toString()
					+ " (" 
					+ Routines.formatMillisAsHMS(task.getCurrentTime() - timeStarted)
					+ " ago)"
				: "N/A"
			%>
		</td>
	</tr>
	<tr>
		<th>Finished:</th>
		<td>
			<% long timeFinished = task.getTimeFinished(); %>
			<%=timeFinished != 0
				? new Date(timeFinished).toString()
					+ " (" 
					+ Routines.formatMillisAsHMS(task.getCurrentTime() - timeFinished)
					+ " ago)"
				: "N/A"
			%>
		</td>
	</tr>
	<tr>
		<td colspan="2"><h2>Restarts &amp; timeouts</h2></td>
	</tr>
	<tr>
		<th>Restart count:</th>
		<td><%=task.getRestartCount()%></td>
	</tr>
	<tr>
		<th>Maximum restarts allowed:</th>
		<td><%=task.getRestartMax() != 0 ? task.getRestartMax() : "not restricted" %></td>
	</tr>
	<tr>
		<th>Maximum running time:</th>
		<td>
			<%=task.getTimeoutRun() != 0
				? Routines.formatMillisAsHMS(task.getTimeoutRun())
				: "not restricted" %>
		</td>
	</tr>
	<tr>
		<td colspan="2"><h2>Directories</h2></td>
	</tr>
	<tr>
		<th>Task directory:</th>
		<td><%=task.getDirectoryPathTask()%></td>
	</tr>
	<tr>
		<th>Working directory:</th>
		<td><%=task.getDirectoryPathWorking()%></td>
	</tr>
	<tr>
		<th>Temporary directory:</th>
		<td><%=task.getDirectoryPathTemporary()%></td>
	</tr>
	<tr>
		<td colspan="2"><h2>Reached checkpoints</h2></td>
	</tr>
	<tr>
		<td colspan="2">
			<% if (checkpoints.length > 0) { %>
				<table class="real">
					<tr>
						<th>Name</th>
						<th>Value</th>
					</tr>
					<%
						for (int i = 0; i < checkpoints.length; i++) {
							CheckPoint checkpoint = checkpoints[i];
					%>
						<tr>
							<td><%=Routines.htmlspecialchars(checkpoint.getName())%></td>
							<td><%=checkpoint.getValue() != null ? Routines.htmlspecialchars(checkpoint.getValue().toString()) : "N/A" %></td>
						</tr>
					<% } %>
				</table>
			<% } else { %>
				No checkpoints reached yet.
			<% } %>
		</td>
	</tr>
	<tr>
		<td colspan="2"><a name="properties" /><h2>Task properties</h2></td>
	</tr>
	<tr>
		<td colspan="2">
			<% Properties properties = task.getTaskProperties(); %>
			<% if (properties.size() > 0) { %>
				<table class="real">
					<tr>
						<th>Name</th>
						<th>Value</th>
					</tr>
					<%
						Set keySet = properties.keySet();
						for (Iterator it = keySet.iterator(); it.hasNext(); ) {
							Object key = it.next();
							Object value = properties.get(key);
					%>
						<tr>
							<td><%=Routines.htmlspecialchars(key.toString())%></td>
							<td><%=Routines.nl2br(Routines.htmlspecialchars(value.toString()))%></td>
						</tr>
					<% } %>
				</table>
			<% } else { %>
				No task properties set.
			<% } %>
		</td>
	</tr>
	<tr>
		<td colspan="2"><h2>Task dependencies</h2></td>
	</tr>
	<tr>
		<td colspan="2">
			<% Dependencies dependencies = task.getOriginalTaskDescriptor().getDependencies(); %>
			<% if (dependencies != null && dependencies.getDependencyCheckPoint().size() > 0) { %>
				<table class="real">
					<tr>
						<th>Task ID</th>
						<th>Checkpoint name</th>
						<th>Checkpoint value</th>
					</tr>
					<%
						for (DependencyCheckPoint dependency : dependencies.getDependencyCheckPoint() ) {
					%>
						<tr>
							<td><a href="<%=page_.currentActionURL() %>?cid=<%=
								Routines.htmlspecialchars(task.getContextId())%>&amp;tid=<%=
								Routines.htmlspecialchars(dependency.getTaskId())%>#properties"><%=
								Routines.htmlspecialchars(dependency.getTaskId())
								%></a></td>
							<td><%=Routines.htmlspecialchars(dependency.getType().toString())%></td>
							<td><%=dependency.getValue() == null ? "" :
								Routines.htmlspecialchars(dependency.getValue().toString())%></td>
						</tr>
					<% } %>
				</table>
			<% } else { %>
				No dependencies set
			<% } %>
		</td>
	</tr>
	<tr>
		<td class="buttons" colspan="2">
			<form action="<%=page_.actionURL("task-kill")%>"
				onsubmit="return confirm('Do you really want to kill task &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(task.getTaskName()))%>&quot;?');">
				<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(task.getContextId())%>" />
				<input type="hidden" name="tid" value="<%=Routines.htmlspecialchars(task.getTaskId())%>" />
				<input type="submit" class="type-submit" value="Kill"
					<%=task.getState() != TaskState.RUNNING && task.getState() != TaskState.SLEEPING && task.getState() != TaskState.SUBMITTED ? "disabled='disabled'" : ""%>
				/>
			</form>
		</td>
	</tr>
</table>
		</div>
		<div id="logs-sheet" class="tabsheet-sheet-invisible">
			<%
				HashMap data = new HashMap();
				data.put("logRecords", logRecords);
				data.put("logFields", logFields);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("log-and-output-view", data);
			%>
		</div>
	</div>
</div>
