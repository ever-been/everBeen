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
	import="java.net.URLEncoder"
%><%
	PackageMetadata[] sourcePackages = (PackageMetadata[])application.getAttribute("sourcePackages");
	PackageMetadata[] binaryPackages = (PackageMetadata[])application.getAttribute("binaryPackages");
	PackageMetadata[] taskPackages = (PackageMetadata[])application.getAttribute("taskPackages");
	PackageMetadata[] dataPackages = (PackageMetadata[])application.getAttribute("dataPackages");
        PackageMetadata[] modulePackages = (PackageMetadata[])application.getAttribute("modulePackages");
	ArrayList conditions = (ArrayList)application.getAttribute("conditions");
	Condition[] atoms = (Condition[])application.getAttribute("atoms");
	PackagesModule.NoPackagesReason noPackagesReason = (PackagesModule.NoPackagesReason)application.getAttribute("noPackagesReason");
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<form id="packages-list-form" action="<%=page_.currentActionURL()%>">
		<%
			String conditionIds = "";
			boolean first = true;
			for (int i = 0; i < conditions.size(); i++) {
				if (!first) {
					conditionIds += ",";
				}
				conditionIds += Integer.toString(i);
				first = false;
			}
		%>
		<input type="hidden" name="condition-ids" value="<%=conditionIds%>" />
		<table class="form center-block">
			<% if (conditions.size() > 0) { %>
				<% for (int i = 0; i < conditions.size(); i++) { %>
					<% 
						HashMap data = new HashMap();
						atoms[i].prepare(); // fill-in attributeInfo member
					%>
					<tr id="condition-row<%=i%>">
						<td>
							<%
								data.clear();
								data.put("conditionId", new Integer(i));
								data.put("attribute", ((HashMap)conditions.get(i)).get("attribute"));
								out.flush(); // unforunately can't be inside writeTemplate method
								page_.writeTemplate("packages-list-attribute-widget", data);
							%>
						</td>
						<td>
							<%
								data.clear();
								data.put("conditionId", new Integer(i));
								data.put("klass", atoms[i].getAttributeInfo().getKlass());
								data.put("operator", ((HashMap)conditions.get(i)).get("operator"));
								out.flush(); // unforunately can't be inside writeTemplate method
								page_.writeTemplate("packages-list-operator-widget", data);
							%>
						</td>
						<td>
							<%
								data.clear();
								data.put("conditionId", new Integer(i));
								data.put("klass", atoms[i].getAttributeInfo().getKlass());
								data.put("value", ((HashMap)conditions.get(i)).get("value"));
								out.flush(); // unforunately can't be inside writeTemplate method
								page_.writeTemplate("packages-list-value-widget", data);
							%>
						</td>
						<td><a href="javascript:packagesListDeleteConditionLinkClick(<%=i%>);">Delete condition</a></td>
					</tr>
				<% } %>
			<% } else { %>
				<tr id="no-conditions-row">
					<td colspan="4" class="center">(no conditions specified)</td>
				</tr>
			<% } %>
			<tr id="add-condition-row">
				<td colspan="3" class="left"><a href="javascript:packagesListAddConditionLinkClick();">Add condition</a></td>
				<td class="right"><input type="submit" class="type-submit" name="list" value="Filter" /></td>
			</tr>
		</table>
	</form>
</div></div></div></div></div></div>
<%
	out.flush();
	page_.writeInfoMessages();
	page_.writeErrorMessages();
%>
<% if (sourcePackages.length > 0 || binaryPackages.length > 0
		|| taskPackages.length > 0 || dataPackages.length > 0 || modulePackages.length > 0) { %>
	
<table class="spacing center-block">
	<%
		HashMap data = new HashMap();

		data.put("title", "Source packages");
		data.put("packages", sourcePackages);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("packages-list-inner-table", data);
		
		data.clear();
		data.put("title", "Binary packages");
		data.put("packages", binaryPackages);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("packages-list-inner-table", data);

		data.clear();
		data.put("title", "Task packages");
		data.put("packages", taskPackages);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("packages-list-inner-table", data);

		data.clear();
		data.put("title", "Data packages");
		data.put("packages", dataPackages);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("packages-list-inner-table", data);
                
                data.clear();
		data.put("title", "Pluggable module packages");
		data.put("packages", modulePackages);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeTemplate("packages-list-inner-table", data);
	%>
</table>
<% } else { %>
	<% if (noPackagesReason.equals(PackagesModule.NoPackagesReason.INITIAL)) { %>
		<p class="center">Specify filtering conditions.</p>
	<% } else if (noPackagesReason.equals(PackagesModule.NoPackagesReason.NO_MATCH)) { %>
		<p class="center">No package matched filtering conditions.</p>
	<% } else if (noPackagesReason.equals(PackagesModule.NoPackagesReason.ERROR)) { %>
		<p class="center">Error in filtering conditions specification.</p>
	<% } %>
<% } %>
<script type="text/javascript" src="<%=page_.actionURL("javascript-attribute-info")%>"></script>
<script type="text/javascript">nextConditionId = <%=conditions.size()%>;</script>