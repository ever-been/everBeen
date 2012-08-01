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
	import="cz.cuni.mff.been.logging.*"
	import="java.text.SimpleDateFormat"
	import="java.util.EnumSet"
%><%
	LogRecord[] logRecords = (LogRecord[])application.getAttribute("logRecords");
	EnumSet logFields = (EnumSet)application.getAttribute("logFields");
	LogEntityColumn[] logEntityColumns = (LogEntityColumn[])application.getAttribute("logEntityColumns");
%>	
<% if (logRecords != null && logRecords.length > 0) { %>
	<div class="log-view" id="log-view">
		<table>
			<thead>
				<tr>
					<% for (int i = 0; i < logEntityColumns.length; i++) { %><th><%=Routines.htmlspecialchars(logEntityColumns[i].getTitle())%></th><% } %>
					<% if (logFields.contains(LogRecord.Fields.CONTEXT)) { %><th>Context</th><% } %>
					<% if (logFields.contains(LogRecord.Fields.TASK_ID)) { %><th>Task ID</th><% } %>
					<% if (logFields.contains(LogRecord.Fields.HOSTNAME)) { %><th>Host</th><% } %>
					<% if (logFields.contains(LogRecord.Fields.TIMESTAMP)) { %><th class="sorted-asc">Time</th><% } %>
					<% if (logFields.contains(LogRecord.Fields.LEVEL)) { %><th>Level</th><% } %>
					<% if (logFields.contains(LogRecord.Fields.MESSAGE)) { %><th>Message</th><% } %>
				</tr>
			</thead>
			<tbody>
				<% for (int i = 0; i < logRecords.length; i++) { %>
					<% LogRecord logRecord = logRecords[i]; %>
					<tr class="log-level-<%=logRecord.getLevel().toString().toLowerCase()%>">
						<% for (int j = 0; j < logEntityColumns.length; j++) { %>
							<td>
								<%
									LogEntityColumn.Value value = logEntityColumns[j].getValue(
										logRecord.getContext(),
										logRecord.getTaskID()
									);
								%>
								<% if (value != null) { %>
									<a
										href="<%=Routines.htmlspecialchars(String.format(logEntityColumns[j].getUrlTemplate(), value.getId().toString()))%>"
									><%=Routines.htmlspecialchars(value.getName().toString())%></a>
								<% } else { %>
									N/A
								<% } %>
							</td>
						<% } %>
						<% if (logFields.contains(LogRecord.Fields.CONTEXT)) { %><td><%=Routines.htmlspecialchars(logRecord.getContext())%></td><% } %>
						<% if (logFields.contains(LogRecord.Fields.TASK_ID)) { %><td><%=Routines.htmlspecialchars(logRecord.getTaskID())%></td><% } %>
						<% if (logFields.contains(LogRecord.Fields.HOSTNAME)) { %>
							<td>
								<a
									href="<%=page_.moduleActionURL("hosts", "host-details")%>?hostname=<%=Routines.htmlspecialchars(logRecord.getHostname())%>"
								><%=Routines.htmlspecialchars(logRecord.getHostname())%></a>
							</td>
						<% } %>
						<% if (logFields.contains(LogRecord.Fields.TIMESTAMP)) { %>
							<td>
								<%
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'&nbsp;'HH:mm:ss");
									String formattedTimestamp = sdf.format(logRecord.getTimestamp());
								%>
								<%=formattedTimestamp.toString()%>
							</td>
						<% } %>
						<% if (logFields.contains(LogRecord.Fields.LEVEL)) { %><td><%=Routines.htmlspecialchars(logRecord.getLevel().toString())%></td><% } %>
						<% if (logFields.contains(LogRecord.Fields.MESSAGE)) { %><td><%=Routines.nl2br(Routines.htmlspecialchars(logRecord.getMessage()))%></td><% } %>
					</tr>
				<% } %>
			</tbody>
		</table>
	</div>
	<script type="text/javascript">
		logViewCreate(document.getElementById("log-view"), [
			<% for (int j = 0; j < logEntityColumns.length; j++) { %>{ comparator: logViewStringCaseInsensitiveComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.CONTEXT)) { %>{ comparator: logViewStringCaseInsensitiveComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.TASK_ID)) { %>{ comparator: logViewStringCaseInsensitiveComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.HOSTNAME)) { %>{ comparator: logViewStringCaseInsensitiveComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.TIMESTAMP)) { %>{ comparator: logViewDateComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.LEVEL)) { %>{ comparator: logViewLogLevelComparator },<% } %>
			<% if (logFields.contains(LogRecord.Fields.MESSAGE)) { %>{ comparator: logViewStringCaseInsensitiveComparator},<% } %>
		]);
	</script>
<% } else { %>
	<p class="center">No logs generated yet.</p>
<% } %>