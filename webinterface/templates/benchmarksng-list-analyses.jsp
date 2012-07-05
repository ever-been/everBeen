<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: Jiri Tauber

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
	import="cz.cuni.mff.been.benchmarkmanagerng.Analysis"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	Collection<Analysis> analyses = (Collection<Analysis>)application.getAttribute( "analyses" );
	if( analyses != null && !analyses.isEmpty() ){
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.currentActionURL()%>">Analyses</a>
</div></div></div></div></div></div>
<table class="list center-block">
	<tbody>
		<tr>
			<th>Name</th>
			<th>Status</th>
			<th title="last time when generator succeeded">Last run</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		<%
		for (Analysis analysis : analyses) {
			%><tr>
			<th><a href="<%=page_.actionURL("analysis-detail")%>?name=<%= Routines.htmlspecialchars(analysis.getName()) %>">
				<%= Routines.htmlspecialchars(analysis.getName()) %></a></th>
			<td class="analysis-status-<%= analysis.getState().toString().toLowerCase()
				%>"><%= analysis.getState().toString() %></td>
			<td class="<%= analysis.shouldBeScheduled() ? "analysis-time-passed" : "" %>"><%=
						analysis.getLastTime() != null ? analysis.getLastTime().toString() : "never" %></td>
			<td>
				<form method="post" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="name" value="<%= Routines.htmlspecialchars(analysis.getName()) %>">
					<input type="submit" name="run"  value="Run">
				</form>
			</td><td>
				<form method="post" action="<%=page_.actionURL("edit-analysis")%>?name=<%= Routines.htmlspecialchars(analysis.getName()) %>">
					<input type="submit" name="edit" value="Edit">
				</form>
			</td><td>
				<form method="post" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="name" value="<%= Routines.htmlspecialchars(analysis.getName()) %>">
					<input type="submit" name="delete" value="Delete"
						onclick="return confirm('Do you really want to delete analysis &quot;<%= Routines.htmlspecialchars(analysis.getName()) %>&quot;?');">
				</form>
			</td>
		</tr>
		<% } %>
	</tbody>
</table>
<br><br>
<table class="center-block"><tr><td>
<%
	}

	if( (Boolean)application.getAttribute( "schedulerRunning" ) ){
		%>
		<form action="<%=page_.currentActionURL()%>" method="post">
			<input type="submit" name="stop_scheduler" value="Stop Scheduler">
		</form>
<% } else { %>
		Restart BenchmarkManagerNg service to start scheduler again
<% }%>
</td></tr></table>
