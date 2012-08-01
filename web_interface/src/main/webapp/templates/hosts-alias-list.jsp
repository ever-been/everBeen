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
	List aliases = (List)application.getAttribute("aliases");
%>
<% if (aliases.size() > 0) { %>
	<table class="list">
		<% for (int i = 0; i < aliases.size(); i++) { %>
			<% SoftwareAliasDefinition alias = (SoftwareAliasDefinition)aliases.get(i); %>
			<tr>
				<th>
					<a href="<%=page_.actionURL("alias-edit")%>?alias=<%=Routines.htmlspecialchars(alias.getAliasName())%>"
						><%=Routines.htmlspecialchars(alias.getAliasName())%>
					</a>
				</th>
				<td class="button-spacer">&nbsp;</td>
				<td>
					<form action="<%=page_.actionURL("alias-delete")%>"
						onsubmit="return confirm('Do you really want to delete software alias definition &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(alias.getAliasName()))%>&quot;?');">
						<input type="hidden" name="alias" value="<%=Routines.htmlspecialchars(alias.getAliasName())%>" />
						<input type="submit" class="type-submit" value="Delete" />
					</form>
				</td>
			</tr>
		<% } %>
	</table>
<% } else { %>
	<p class="center">No software alias definitions entered.  You can add
	software alias definitions using
	<a href="<%=page_.actionURL("alias-add")%>">Add alias</a> page.</p>
<% } %>