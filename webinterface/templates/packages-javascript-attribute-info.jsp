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
	import="java.util.*"
	import="cz.cuni.mff.been.softwarerepository.*"
	import="cz.cuni.mff.been.webinterface.packages.*"
%>
var attributeInfo = {
	<% for (int i = 0; i < PackageMetadata.ATTRIBUTE_INFO.length; i ++) { %>
		<% AttributeInfo attributeInfo = PackageMetadata.ATTRIBUTE_INFO[i]; %>
		<%=attributeInfo.getName()%>: {
			humanName: "<%=Routines.javaScriptEscape(attributeInfo.getHumanName())%>",
			klass: "<%=Routines.javaScriptEscape(attributeInfo.getKlass().getName())%>"
		}<%=i < PackageMetadata.ATTRIBUTE_INFO.length - 1 ? "," : ""%>
	<% } %>
};

var klassToOperators = {};

<%
	for (Iterator i = Operator.getKlasses().iterator(); i.hasNext();) {
		Class klass = (Class)i.next();
		Operator[] operators = Operator.forKlass(klass);
		%>
			klassToOperators["<%=klass.getName()%>"] = [
		<%
		for (int j = 0; j < operators.length; j++) {
			%>
				{
					name: "<%=Routines.javaScriptEscape(operators[j].getName())%>",
					title: "<%=Routines.javaScriptEscape(operators[j].getTitle())%>"
				}<%=j < operators.length - 1 ? "," : ""%>
			<%
		}
		%>
			];
		<%
	}
%>