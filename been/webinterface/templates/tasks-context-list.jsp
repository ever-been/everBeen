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
	import="cz.cuni.mff.been.taskmanager.*"
	import="cz.cuni.mff.been.taskmanager.data.*"
	import="cz.cuni.mff.been.task.Task"
	import="java.text.SimpleDateFormat"
%><%
	ContextEntry[] contexts = (ContextEntry[])application.getAttribute("contexts");
	
	page_.writeInfoMessages();
	page_.writeErrorMessages();
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("context-list")%>">Contexts</a>
</div></div></div></div></div></div>

<% if (contexts.length > 0) { %>
	<table class="list center-block">
		<tr>
			<th>Context ID</th>
			<th>Context name</th>
			<th title="Context creation">Time</th>
			<th>&nbsp;</th>
			<th>&nbsp;</th>
		</tr>
		<% for (int i = 0; i < contexts.length; i++) { %>
		  <% ContextEntry context = contexts[i]; %>
		  <% if( context.isOpen() ){ %>
			<tr>
		  <% } else { %>
			<tr class="context-state-closed">
		  <% } %>
				<th><a
					href="<%=page_.moduleActionURL("tasks", "context-details")%>?cid=<%=Routines.htmlspecialchars(context.getContextId())%>"
					><%=Routines.htmlspecialchars(context.getContextId())%></a>
				</th>
				<td><%=Routines.htmlspecialchars(context.getContextName())%></td>
				<td><%=Routines.htmlspecialchars(SimpleDateFormat.getInstance().format(new Date(context.getCurentTime())))%></td>
				<td>
					<form action="<%=page_.moduleActionURL("tasks", "context-kill")%>"
						onsubmit="return confirm('Do you really want to kill all tasks in context &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(context.getContextName()))%>&quot;?');">
						<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(context.getContextId())%>" />
						<input type="submit" class="type-submit" value="Kill all tasks in context" />
					</form>
				</td>
				<td>
					<% if (!context.getContextId().equals(TaskManagerInterface.SYSTEM_CONTEXT_ID)) { %>
						<form action="<%=page_.actionURL("context-delete")%>"
							onsubmit="return confirm('Do you really want to delete context &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(context.getContextName()))%>&quot;?');">
							<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(context.getContextId())%>" />
							<input type="submit" class="type-submit" value="Delete context" />
						</form>
					<% } else { %>
						&nbsp;
					<% } %>
				</td>
			</tr>
		<% } %>
	</table>
<% } else { %>
	<p class="center">No contexts active.</p>
<% } %>
