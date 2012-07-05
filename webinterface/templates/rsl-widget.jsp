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
%><%
	String name = (String)application.getAttribute("name");
	String value = (String)application.getAttribute("value");
	String type = (String)application.getAttribute("type");
%>
<textarea name="<%=Routines.htmlspecialchars(name)%>"
	class="name-<%=Routines.htmlspecialchars(name)%>" rows="5" cols="50"
><%=Routines.htmlspecialchars(value)%></textarea><br />
<small>Use <abbr title="Restriction Specificaton Language">RSL</abbr> to specify the
conditions. <a href="<%=page_.actionURL("rsl-help")%>?type=<%=type%>" onclick="return !window.open('<%=page_.actionURL("rsl-help")%>?type=<%=type%>')">Need help with syntax?</a>