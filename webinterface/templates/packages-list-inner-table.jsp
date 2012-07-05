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
	String title = (String)application.getAttribute("title");
	PackageMetadata[] packages = (PackageMetadata[])application.getAttribute("packages");
%>
<% if (packages.length > 0) { %>
	<tr><td><h2><%=Routines.htmlspecialchars(title)%></h2></td></tr>
	<tr><td><table class="list full-width">
		<tr>
			<th>Package name</th>
			<th>Human-readable name</th>
			<th>Version</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		<% for (int i = 0; i < packages.length; i++) { %>
			<% PackageMetadata package_ = packages[i]; %>
			<tr>
				<th>
					<a
						href="<%=page_.actionURL("details")%>?package=<%=Routines.htmlspecialchars(URLEncoder.encode(package_.getFilename(), "UTF-8"))%>"
					><%=Routines.htmlspecialchars(package_.getName())%></a>
				</th>
				<td><%=Routines.htmlspecialchars(package_.getHumanName())%></a></td>
				<td><%=Routines.htmlspecialchars(package_.getVersion().toString())%></td>
				<td>
					<form action="<%=page_.actionURL("download")%>">
						<input type="hidden" name="package" value="<%=Routines.htmlspecialchars(package_.getFilename())%>" />
						<input type="submit" class="type-submit" value="Download" />
					</form>
				</td>
				<td>
					<form action="<%=page_.actionURL("delete")%>"
						onsubmit="return confirm('Do you really want to delete package &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(package_.getFilename()))%>&quot;?');">
						<input type="hidden" name="package" value="<%=Routines.htmlspecialchars(package_.getFilename())%>" />
						<input type="submit" class="type-submit" value="Delete" />
					</form>
				</td>
			</tr>
		<% } %>
	</table></td></tr>
<% } %>