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
	import="java.util.*"
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();

	boolean editing = ((Boolean)application.getAttribute("editing")).booleanValue();
	String alias = null;
	if (editing) {
		alias = (String)application.getAttribute("alias");
	}
	String aliasName = (String)application.getAttribute("aliasName");
	String resultName = (String)application.getAttribute("resultName");
	String resultVendor = (String)application.getAttribute("resultVendor");
	String resultVersion = (String)application.getAttribute("resultVersion");
	String osRestriction = (String)application.getAttribute("osRestriction");
	String appRestriction = (String)application.getAttribute("appRestriction");
%>
<form id="hosts-alias-add-edit-form" action="<%=page_.currentActionURL()%>" method="post">
	<% if (editing) { %>
		<input type="hidden" name="alias" value="<%=Routines.htmlspecialchars(alias)%>" />
	<% } %>
	<table class="form center-block">
		<tr>
			<td colspan="2"><h2>Alias data</h2></td>
		</tr>
		<tr>
			<td colspan="2" class="note">
				You can use <code>\${name}</code>, <code>\${vendor}</code> and
				<code>\${version}</code> variables in the following four form
				fields.<br />
				
				When matching, they will be replaced by real values from matched
				application.
			</td>
		</tr>
		<tr>
			<th>Alias name:</th>
			<td>
				<input type="text" name="alias-name" class="name-alias-name"
					value="<%=Routines.htmlspecialchars(aliasName)%>" />
			</td>
		</tr>
		<tr>
			<th>Result name:</th>
			<td>
				<input type="text" name="result-name" class="name-result-name"
					value="<%=Routines.htmlspecialchars(resultName)%>" />
			</td>
		</tr>
		<tr>
			<th>Result vendor:</th>
			<td>
				<input type="text" name="result-vendor" class="name-result-vendor"
					value="<%=Routines.htmlspecialchars(resultVendor)%>" />
			</td>
		</tr>
		<tr>
			<th>Result version:</th>
			<td>
				<input type="text" name="result-version" class="name-result-version"
					value="<%=Routines.htmlspecialchars(resultVersion)%>" />
			</td>
		</tr>
		<tr>
			<td colspan="2"><h2>Restrictions</h2></td>
		</tr>
		<tr>
			<th>Restriction for the operating system:</th>
			<td>
				<%
					HashMap data = new HashMap();
					
					data.put("name", "os-restriction");
					data.put("value", osRestriction);
					data.put("type", "os");
					out.flush(); // unforunately can't be inside writeTemplate method
					page_.writeTemplate("rsl-widget", data);
				%>
			</td>
		</tr>
		<tr>
			<th>Restriction for the application:</th>
			<td>
				<%
					data.clear();
					data.put("name", "app-restriction");
					data.put("value", appRestriction);
					data.put("type", "app");
					out.flush(); // unforunately can't be inside writeTemplate method
					page_.writeTemplate("rsl-widget", data);
				%>
			</td>
		</tr>
		<tr>
			<td class="buttons" colspan="2">
				<% if (editing) { %>
					<input type="submit" class="type-submit" name="edit" value="Edit" />
				<% } else { %>
					<input type="submit" class="type-submit" name="add" value="Add" />
				<% } %>
				<input type="submit" class="type-submit" name="cancel" value="Cancel" />
			</td>
		</tr>
	</table>
</form>
