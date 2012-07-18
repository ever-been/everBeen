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
	import="cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	Collection<String> analyses = (Collection<String>)application.getAttribute( "analyses" );

	if( analyses != null && !analyses.isEmpty() ){
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("list-analyses")%>">Analyses</a>
</div></div></div></div></div></div>
<table class="list center-block">
	<tbody>
		<tr>
			<th>Analysis name</th>
		</tr>
		<%
		for (String analysis : analyses) {
			%><tr>
			<th><a href="<%= page_.actionURL("list-datasets") %>?analysis=<%=
				Routines.htmlspecialchars(analysis) %>"><%= Routines.htmlspecialchars(analysis) %></a></th>
		</tr>
		<% } %>
	</tbody>
</table>
<% } %>
