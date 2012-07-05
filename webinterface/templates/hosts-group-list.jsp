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
	
	List groups = (List)application.getAttribute("groups");
%>
<% if (groups.size() > 0) { %>
	<table class="list">
		<% for (int i = 0; i < groups.size(); i++) { %>
		  <% HostGroup group = (HostGroup)groups.get(i); %>
			<tr>
				<th>
					<a
						href="<%=page_.actionURL("group-edit")%>?group=<%=Routines.htmlspecialchars(group.getName())%>"
					><%=Routines.htmlspecialchars(group.getName())%>
				</th>
				<td class="button-spacer">&nbsp;</td>
				<td>
					<% if (!group.isDefaultGroup()) { %>
						<form action="<%=page_.actionURL("group-delete")%>"
							onsubmit="return confirm('Do you really want to delete group &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(group.getName()))%>&quot;?');">
							<input type="hidden" name="group" value="<%=Routines.htmlspecialchars(group.getName())%>" />
							<input type="submit" class="type-submit" class="type-submit" value="Delete" />
						</form>
					<% } %>
				</td>
			</tr>
		<% } %>
	</table>
<% } else { %>
	<p class="center">No groups entered. You can add groups using
	<a href="<%=page_.actionURL("group-add")%>">Add group</a> page.</p>
<% } %>