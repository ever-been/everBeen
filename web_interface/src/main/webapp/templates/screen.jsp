<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: David Majda
  File author: Jiri Tauber

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
	import="cz.cuni.mff.been.webinterface.screen.*"
	import="cz.cuni.mff.been.common.id.*"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	Screen screen = (Screen)application.getAttribute("screen");
%>
<form id="benchmarks-run-configure-plugin-form" action="<%=page_.currentActionURL()%>" method="post">
	<input type="hidden" name="sid" value="<%=Routines.htmlspecialchars(screen.getSid().toString())%>" />
	<table class="form center-block">
		<% for (int i = 0; i < screen.getSections().length; i++) { %>
			<% Section section = screen.getSections()[i]; %>
			
			<%-- ===== Section title & description ===== --%>
			<%
				String title = section.getTitle();
				if (title != null && !section.getTitle().equals("")) {
			%>
				<tr>
					<td colspan="2">
						<h2><%=Routines.htmlspecialchars(title)%></h2>
					</td>
				</tr>
			<% } %>
			<%
				String description = section.getDescription();
				if (description != null && !description.equals("")) {
			%>
				<tr>
					<td colspan="2">
						<%=Routines.htmlspecialchars(description)%>
					</td>
				</tr>
			<% } %>
			
			<%-- ===== Section items ===== --%>
			<%
				HashMap data = new HashMap();
				data.put("items", section.getItems());
				data.put("indented", Boolean.FALSE);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("screen-items", data);
			%>
			
		<% } %>
		<tr>
			<td class="buttons" colspan="2">
				<% if( screen.showButtonPrevious() ){ %>
				<input type="submit" class="type-submit" name="previous" value="< Previous" />
				<% } %>
				<% if( screen.showButtonNext() ){ %>
				<input type="submit" class="type-submit" name="next" value="Next >" />
				<% } %>
				&nbsp;
				<% if( screen.showButtonCancel() ){ %>
				<input type="submit" class="type-submit" name="cancel" value="Cancel" />
				<% } %>
				<% if( screen.showButtonFinish() ){ %>
				<input type="submit" class="type-submit" name="finish" value="Finish" />
				<% } %>
			</td>
		</tr>
	</table>
</form>
