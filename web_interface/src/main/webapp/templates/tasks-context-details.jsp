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
	import="cz.cuni.mff.been.webinterface.tasks.*"
	import="cz.cuni.mff.been.common.id.*"
	import="java.io.Serializable"
	import="java.text.SimpleDateFormat"

%><%page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	ContextEntry context = (ContextEntry)application.getAttribute("context");
	TaskEntryImplementation[] tasks = (TaskEntryImplementation[])application.getAttribute("tasks");
	Map checkpoints = (Map)application.getAttribute("checkpoints");%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("context-list")%>">Contexts</a>
	&raquo;
	<a
		href="<%=page_.actionURL("context-details")%>?cid=<%=Routines.htmlspecialchars(context.getContextId())%>"
	><%=Routines.htmlspecialchars(context.getContextId())%></a>
</div></div></div></div></div></div>

<div class="center">
<%
	if( !context.isOpen() ){
		Messages msg = new Messages();
		msg.addTextMessage("This context is closed.");
		page_.setWarningMessages(msg);
		out.flush(); // unforunately can't be inside writeTemplate method
		page_.writeWarningMessages();
	}
%>
</div>

<table class="form center-block">
	<tr>
		<td colspan="2"><h2>Context data</h2></td>
	</tr>
	<tr>
		<th>ID:</th>
		<td><%=Routines.htmlspecialchars(context.getContextId())%></td>
	</tr>
	<tr>
		<th>Name:</th>
		<td><%=Routines.htmlspecialchars(context.getContextName())%></td>
	</tr>
	<tr>
		<th>Description:</th>
		<td><%=Routines.htmlspecialchars(context.getContextDescription())%></td>
	</tr>
	<tr>
		<th>Time:</th>
		<td><%=Routines.htmlspecialchars(SimpleDateFormat.getInstance().format(new Date(context.getCurentTime())))%></td>
	</tr>
	<tr>
		<th>Associated analysis:</th>
		<td>
			<%
				AID aid = null;
				boolean valuesOK = false;
				Serializable magicObject = context.getMagicObject();
				if (magicObject != null) {
					try {
						aid = (AID) magicObject;
						valuesOK = true;
					} catch (ClassCastException e) {
						/* Do nothing, valuesOK will remain false if there are
						 * some strange things in the magicObject and we will
						 * check for it later.
						 */
					}
				}
			%>
			<% if (valuesOK) { %>
				<a href="<%=page_.moduleActionURL("benchmarksng", "analysis-detail")
					%>?name=<%=Routines.htmlspecialchars(aid.getName())%>"><%=Routines.htmlspecialchars(aid.getName())%></a>
			<% } else { %>
				none
			<% } %>
		</td>
	</tr>
	<% if (!context.getContextId().equals(TaskManagerInterface.SYSTEM_CONTEXT_ID)) { %>
		<tr>
			<td class="buttons" colspan="2">
				<form action="<%=page_.actionURL("context-delete")%>"
					onsubmit="return confirm('Do you really want to delete context &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(context.getContextName()))%>&quot;?');">
					<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(context.getContextId())%>" />
					<input type="submit" class="type-submit" value="Delete context" />
				</form>
			</td>
		</tr>
	<% } %>
	<tr>
		<td colspan="2"><h2>Tasks</h2></td>
	</tr>
	<tr>
		<td colspan="2">
			<%
				HashMap data = new HashMap();
				data.put("tasks", tasks);
				data.put("checkpoints", checkpoints);
				data.put("showHost", true);
				data.put("showContext", false);
				data.put("mode", TaskListMode.NORMAL);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("tasks-task-list", data);
			%>
		</td>
	</tr>
	<tr>
		<td class="buttons" colspan="2">
			<form action="<%=page_.actionURL("context-kill")%>"
				onsubmit="return confirm('Do you really want to kill all tasks in context &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(context.getContextName()))%>&quot;?');">
				<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(context.getContextId())%>" />
				<input type="submit" class="type-submit" value="Kill all tasks in context" />
			</form>
		</td>
	</tr>
</table>
