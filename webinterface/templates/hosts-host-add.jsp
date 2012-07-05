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
	import="cz.cuni.mff.been.hostmanager.database.*"
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();
	
	String hostname = (String)application.getAttribute("hostname");
%>
<form id="hosts-host-add-form" action="<%=page_.currentActionURL()%>" method="post">
	<table class="form center-block">
		<tr>
			<td colspan="2"><h2>Host data</h2></td>
		</tr>
		<tr>
			<th>Hostname:</th>
			<td>
				<input type="text" name="hostname" class="name-hostname"
					value="<%=Routines.htmlspecialchars(hostname)%>" />
			</td>
		</tr>
		<tr>
			<td class="buttons" colspan="2">
				<input type="submit" class="type-submit" name="add" value="Add" />
				<input type="submit" class="type-submit" name="cancel" value="Cancel" />
			</td>
		</tr>
	</table>
</form>