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
	String group = "";
	if (editing) {
		group = (String)application.getAttribute("group");
	}
	String name = (String)application.getAttribute("name");
	String description = (String)application.getAttribute("description");
	boolean isDefault = ((Boolean)application.getAttribute("isDefault")).booleanValue();
	List hosts = (List)application.getAttribute("hosts");
	List groupHosts = (List)application.getAttribute("groupHosts");
	String rsl = null;
	if (!isDefault) {
		rsl = (String)application.getAttribute("rsl");
	}
%>
<form id="hosts-group-add-edit-form" action="<%=page_.currentActionURL()%>" method="post">
	<% if (editing) { %>
		<input type="hidden" name="group" value="<%=Routines.htmlspecialchars(group)%>" />
	<% } %>
	<table class="center-block">
		<tr>
			<td>
				<table class="form">
					<tr>
						<td colspan="2"><h2>Group data</h2></td>
					</tr>
					<tr>
						<th>Name:</th>
						<td>
							<% if (!isDefault) { %>
								<input type="text" name="name" class="name-name"
									value="<%=Routines.htmlspecialchars(name)%>" />
							<% } else { %>
								<%=Routines.htmlspecialchars(name)%>
							<% } %>
						</td>
					</tr>
					<tr>
						<th>Description:</th>
						<td>
							<textarea name="description" class="name-description"
								rows="5" cols="50"
							><%=Routines.htmlspecialchars(description)%></textarea>
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
			</td>
			<td>
				<table class="form">
					<tr>
						<td><h2>Hosts</h2></td>
					</tr>
					<tr>
						<td>
						<% for (int i = 0; i < hosts.size(); i++) { %>
							<% HostInfoInterface host = (HostInfoInterface)hosts.get(i); %>
							<input type="checkbox" name="group-hosts[<%=Routines.htmlspecialchars(host.getHostName())%>]"
								<%=groupHosts.contains(host.getHostName()) ? " checked=\"checked\"" : ""%>
								<%=isDefault ? " disabled=\"disabled\"" : ""%>
							/>
							<a
								href="<%=page_.actionURL("host-details")%>?hostname=<%=Routines.htmlspecialchars(host.getHostName())%>"
							><%=Routines.htmlspecialchars(host.getHostName())%></a><br />
						<% } %>
						</td>
					</tr>
					<% if (!isDefault) { %>
						<tr id="rsl-expander-row"<%=rsl != "" ? " style=\"display: none\"" : ""%>>
							<td>
								<a href="javascript:groupAddEditRSLExpanderClick()"
								>Select hosts using <abbr title="Restriction Specificaton Language">RSL</abbr>
								expression &gt;&gt;</a>
							</td>
						</tr>
						<tr id="rsl-textarea-row"<%=rsl == "" ? " style=\"display: none\"" : ""%>>
							<td>
								Enter <abbr title="Restriction Specificaton Language">RSL</abbr> expression:<br />
								<textarea name="rsl" class="name-rsl" rows="5" cols="25"
								><%=Routines.htmlspecialchars(rsl)%></textarea>
							</td>
						</tr>
						<tr id="rsl-button-row"<%=rsl == "" ? " style=\"display: none\"" : ""%>>
							<td class="buttons">
								<input type="button" class="type-button" name="select-matching-hosts"
									value="Select matching hosts"
									onclick="groupAddEditSelectMatchingHostsClick();" />
							</td>
						</tr>
						<tr id="rsl-info-message-row">
							<td>
								<div class="info-messages" id="rsl-info-message"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner"></div></div></div></div></div></div>
							</td>
						</tr>
						<tr id="rsl-error-message-row">
							<td>
								<div class="error-messages" id="rsl-error-message"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner"></div></div></div></div></div></div>
							</td>
						</tr>
					<% } %>
				</table>
			</td>
		</tr>
	</table>
</form>