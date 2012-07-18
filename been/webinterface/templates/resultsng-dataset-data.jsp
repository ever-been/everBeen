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
	import="cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple"
	import="cz.cuni.mff.been.resultsrepositoryng.data.DataHandle"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	String[] headers = (String[])application.getAttribute( "headers" );
	Collection<DataHandleTuple> data = (Collection<DataHandleTuple>)application.getAttribute( "data" );

%>
<table class="real">
	<tr>
		<th>Serial</th><%
	for(String tagName : headers) {
		%>
		<th><%= Routines.htmlspecialchars(tagName) %></th>
		<%
	}
	%>
	</tr><%
	for(DataHandleTuple row : data) {
		%><tr>
		<td><%= Routines.htmlspecialchars(row.getSerial().toString()) %></td><%
		for(String tagName : headers) {
			DataHandle dh = row.get(tagName);
			String valueString = "?";
			try {
				if( dh.getValue(dh.getType().getJavaType()) == null ){
					valueString = "<i>null</i>";
				} else {
				 	valueString = dh.getValue(dh.getType().getJavaType()).toString();
				}
			} catch (Exception e) {
				// nothing
			}
			%>
			<td><%= valueString %></td><%
		}
		%>
	</tr><%
	}
	%>				
</table>
