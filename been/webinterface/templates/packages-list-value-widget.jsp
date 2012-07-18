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
	Class klass = (Class)application.getAttribute("klass");
	String value = (String)application.getAttribute("value");
	
	String klassName = klass.getName();
%>
<% if (klassName.equals("java.lang.String")) { %>
	<input type="text" class="type-text" name="value[<%=conditionId%>]" value="<%=Routines.htmlspecialchars(value)%>" />
<%	} else if (klassName.equals("java.util.Date")) { %>
	<input type="text" class="type-text" name="value[<%=conditionId%>]" value="<%=Routines.htmlspecialchars(value)%>" />
<%	} else if (klassName.equals("java.util.ArrayList")) { %>
	<input type="text" class="type-text" name="value[<%=conditionId%>]" value="<%=Routines.htmlspecialchars(value)%>" />
<%	} else if (klassName.equals("cz.cuni.mff.been.common.Version")) { %>
	<input type="text" class="type-text" name="value[<%=conditionId%>]" value="<%=Routines.htmlspecialchars(value)%>" />
<%	} else if (klassName.equals("cz.cuni.mff.been.softwarerepository.PackageType")) { %>
	<% String[] packageTypes = { "source", "binary", "task", "data" }; %>
	<select name="value[<%=conditionId%>]">
		<% for (int i = 0; i < packageTypes.length; i++) {%>
			<option
				value="<%=Routines.htmlspecialchars(packageTypes[i])%>"
			 	<%=packageTypes[i].equals(value) ? " selected=\"selected\"" : ""%>
			><%=Routines.htmlspecialchars(packageTypes[i])%></option>
		<% } %>
	</select>
<%	} else { assert false: "Invalid attribute class"; } %>