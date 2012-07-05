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
	import="cz.cuni.mff.been.hostmanager.*"
	import="cz.cuni.mff.been.hostmanager.value.*"
%><%
	String name = (String)application.getAttribute("name");
	Class klass = (Class)application.getAttribute("klass");
	String value = (String)application.getAttribute("value");
%>
<%-- ValueBoolean --%>
<% if (klass.equals(ValueBoolean.class)) { %>
	<label><input type="radio"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		<%=value.equals("true") ? "checked=\"checked\"" : "" %>
		value="true"
	/> true</label>
	<label><input type="radio"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		<%=value.equals("false") ? "checked=\"checked\"" : "" %>
		value="false"
	/> false</label>
<% } %>

<%-- ValueInteger --%>
<% if (klass.equals(ValueInteger.class)) { %>
	<input type="text" class="type-text-narrow"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		value="<%=Routines.htmlspecialchars(value)%>"
	/>
<% } %>

<%-- ValueDouble --%>
<% if (klass.equals(ValueDouble.class)) { %>
	<input type="text" class="type-text-narrow"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		value="<%=Routines.htmlspecialchars(value)%>"
	/>
<% } %>

<%-- ValueString --%>
<% if (klass.equals(ValueString.class)) { %>
	<input type="text" class="type-text-wide"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		value="<%=Routines.htmlspecialchars(value)%>"
	/>
<% } %>

<%-- ValueRegexp --%>
<% if (klass.equals(ValueRegexp.class)) { %>
	<input type="text" class="type-text-wide"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		value="<%=Routines.htmlspecialchars(value)%>"
	/>
<% } %>

<%-- ValueVersion --%>
<% if (klass.equals(ValueVersion.class)) { %>
	<input type="text" class="type-text-narrow"
		name="value-<%=Routines.htmlspecialchars(name.toString())%>"
		value="<%=Routines.htmlspecialchars(value)%>"
	/>
<% } %>

<%-- ValueList --%>
<% if (klass.equals(ValueList.class)) { %>
	<%=Routines.htmlspecialchars(value.toString())%>
<% } %>

<%-- ValueRange --%>
<% if (klass.equals(ValueRange.class)) { %>
	<%=Routines.htmlspecialchars(value.toString())%>
<% } %>