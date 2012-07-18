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
%><%@
	page import="cz.cuni.mff.been.logging.*"
%><%
	OutputHandle outputHandle = (OutputHandle)application.getAttribute("outputHandle");
%>	
<% if (outputHandle != null) { %>
	<%
		String[] line = outputHandle.getNextLines(1);
		int n = 0;
	%>
	<% if (line.length > 0) { %>
		<div class="output-view">
			<%=Routines.nl2br(Routines.htmlspecialchars(line[0]) + '\n')%>
			<% n += line[0].length(); %>
			<% while ((line = outputHandle.getNextLines(1)).length > 0 && n < 10 * 1024 * 1024) { %>
				<%=Routines.nl2br(Routines.htmlspecialchars(line[0]) + '\n')%>
				<% n +=line[0].length(); %>
			<% } %>
			<% if (n >= 10 * 1024 * 1024) { %>
				.<br />
				.<br />
				.<br />
				Too much output. See the Host Runtime data on the machine where the task is running.
			<% } %>
		</div>
	<% } else { %>
		<p class="center">No output generated yet.</p>
	<% } %>
<% } else { %>
	<p class="center">No output generated yet.</p>
<% } %>