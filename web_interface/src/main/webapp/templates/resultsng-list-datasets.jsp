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

	String analysis = (String)application.getAttribute( "analysis" );
	Map<String, DatasetDescriptor> descriptors = (Map<String, DatasetDescriptor>)
										application.getAttribute( "descriptors" );

if( !descriptors.isEmpty() ){

%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("list-analyses")%>">Analyses</a>
	&raquo;
	<a
		href="<%=page_.actionURL("list-datasets")%>?analysis=<%=Routines.htmlspecialchars(analysis)%>"
	><%=Routines.htmlspecialchars(analysis)%></a>
</div></div></div></div></div></div>
<div class="tabsheet">
	<ul class="tabsheet-tabs">
	<%
	String active = "active";
	for(String name : descriptors.keySet() ){
		%><li class="<%= active %>">
		<a onclick="tabsheetActivate(this, '<%= Routines.htmlspecialchars(name)
		%>'); return false;" href="#"><%= Routines.htmlspecialchars(name) %></a>
		</li><%
		active = "";
	} %>
	</ul>

	<div class="tabsheet-sheets">
	<%
	active = "visible";
	for(String name : descriptors.keySet() ){
		DatasetDescriptor descriptor = descriptors.get(name);
		%><div id="<%= Routines.htmlspecialchars(name) %>" class="tabsheet-sheet-<%= active %>"><% active = "invisible"; %>
			<table class="form"><tbody>
				<tr>
					<td colspan="2"><h2>Dataset type:</td>
				</tr>
				<tr>
					<td colspan="2"><%= descriptor.getDatasetType() %></td>
				</tr>
				<tr>
					<td colspan="2"><h2>Key tags:</td>
				</tr><%
				for(String tagName : descriptor.idTags()) {
					%><tr>
					<th><%= Routines.htmlspecialchars(tagName) %></th>
					<td><%= Routines.htmlspecialchars(descriptor.get(tagName).toString()) %></td>
				</tr>
				<% } %>
				<tr>
					<td colspan="2"><h2>Data tags:</td>
				</tr><%
				for(String tagName : descriptor.dataTags()) {
					%><tr>
					<th><%= Routines.htmlspecialchars(tagName) %></th>
					<td><%= Routines.htmlspecialchars(descriptor.get(tagName).toString()) %></td>
				</tr>
				<% } %>
				<tr>
					<td class="buttons" colspan="2">
					<form action="<%=page_.actionURL("delete-dataset")%>"
						onsubmit="return confirm('Do you really want to delete dataset &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(name))%>&quot;?');">
						<input type="hidden" name="analysis" value="<%=Routines.htmlspecialchars(analysis)%>" />
						<input type="hidden" name="dataset" value="<%=Routines.htmlspecialchars(name)%>" />
						<input type="submit" class="type-submit" value="Delete dataset" />
					</form>
					</td>
				</tr>
			</tbody></table>
			<h2>Data:</h2>
			<%
			String linkParams = "?analysis="+Routines.htmlspecialchars(analysis)
								+"&amp;dataset="+Routines.htmlspecialchars(name);
			%>
			<button type="button" onclick="AJAXUpdate('<%= Routines.htmlspecialchars(name)%>-data', '<%= page_.actionURL("AJAXdatasetData")+linkParams %>')">Reload</button>
			(<a href="<%= page_.actionURL("datasetData")+linkParams %>" target="_blank">new window</a>)<br><br>
			<div id="<%= Routines.htmlspecialchars(name) %>-data">... Click reload to load the data</div>
		</div><%
		} %>
	</div>
</div>

<% } %>
