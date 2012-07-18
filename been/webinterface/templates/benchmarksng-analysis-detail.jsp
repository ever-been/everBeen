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

	Analysis analysis = (Analysis)application.getAttribute("analysis");

	if( analysis != null ){
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("list-analyses")%>">Analyses</a>
	&raquo;
	<a
		href="<%=page_.currentActionURL()%>?name=<%=Routines.htmlspecialchars(analysis.getName())%>"
	><%=Routines.htmlspecialchars(analysis.getName())%></a>
</div></div></div></div></div></div>
<%

		HashMap widgetData = new HashMap();
		widgetData.put("analysis", analysis);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("benchmarksng-analysis-widget", widgetData);

%>
<table class="list center-block">
	<tbody>
		<tr>
			<td>
				<form method="post" action="<%=page_.currentActionURL()%>?name=<%= Routines.htmlspecialchars(analysis.getName()) %>">
					<input type="submit" name="run"  value="Run">
				</form>
			</td><td>
				<form method="post" action="<%= page_.actionURL("edit-analysis")
					%>?name=<%= Routines.htmlspecialchars(analysis.getName()) %>">
					<input type="submit" name="edit" value="Edit">
				</form>
			</td>
		</tr>
	</tbody>
</table>
<% } %>
