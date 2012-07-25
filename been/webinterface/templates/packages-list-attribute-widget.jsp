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
	import="cz.cuni.mff.been.softwarerepository.*"
	import="cz.cuni.mff.been.webinterface.packages.*"
%><%
	int conditionId = ((Integer)application.getAttribute("conditionId")).intValue();
	String attribute = (String)application.getAttribute("attribute");
%>
<select
		name="attribute[<%=conditionId%>]"
		onchange="packagesListAttributeWidgetChange(<%=conditionId%>);">
	<% for (int i = 0; i < PackageMetadata.ATTRIBUTE_INFO.length; i++) {%>
		<% AttributeInfo attributeInfo = PackageMetadata.ATTRIBUTE_INFO[i]; %>
		<option
			 value="<%=Routines.htmlspecialchars(attributeInfo.getName())%>"
			 <%=attributeInfo.getName().equals(attribute) ? " selected=\"selected\"" : ""%>
		><%=Routines.htmlspecialchars(attributeInfo.getHumanName())%></option>
	<% } %>
</select>