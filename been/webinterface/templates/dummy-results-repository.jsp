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
	import="cz.cuni.mff.been.benchmarkmanagerng.Analysis"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

%>
<table border="0">
<tr><td>Analysis:</td>
	<td><form id="analysisform" action="" method="post">
		<select name="analysis_name" id="analysis_name">
			<option value="">----------</option>
<%
	Object analyses = application.getAttribute("analyses");
	if( analyses != null && analyses instanceof Iterable ){
		for( Analysis an : (Iterable<Analysis>)analyses ) {
%>
			<option value="<%= an.getName() %>"><%= an.getName() %></option>
<%
		}
	}
%>
		</select>
		<input type="submit" name="submit" value="OK" />
	</form></td>
</tr>
</table>
<br><br>

<%
	Object success;
	success = application.getAttribute( "success" );

	if ( success != null ) {
		if ( (Boolean) success ) {
			Object oDescriptors = application.getAttribute( "descriptors" );
			if( oDescriptors != null && oDescriptors instanceof Map ){
				Map<String, DatasetDescriptor> descriptors = (Map<String, DatasetDescriptor>)oDescriptors;
				for( String descriptorName : descriptors.keySet() ){
					%><h2><%= descriptorName %></h2><ul><%
					DatasetDescriptor desc = descriptors.get(descriptorName);
					for (String tagName : desc.tags()) {
%>
	<li><%=				tagName + " - " + desc.get(tagName).toString() %></li>
<%
					}
%>

</ul><br>

<%
				}
			}
		} else {
			%><h2>Exception message</h2><%
			%><pre><%=application.getAttribute( "message" )%></pre><%
			%><h2>Exception backtrace</h2><%
			%><pre><%
				for (
					StackTraceElement element
					: (StackTraceElement[]) application.getAttribute( "backtrace" )
				) {
					%><%=element.toString() + '\n'%><%
				}
			%></pre><%
		}
	}
%>
