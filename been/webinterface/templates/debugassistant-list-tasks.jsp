<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: Jan Tattermusch

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
	import="cz.cuni.mff.been.debugassistant.SuspendedTask"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	Collection<SuspendedTask> tasks = (Collection<SuspendedTask>)application.getAttribute( "tasks" );

	if( tasks != null && !tasks.isEmpty() ){
%>
<div class="bar"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
	<a href="<%=page_.actionURL("list-tasks")%>">Suspended tasks</a>
</div></div></div></div></div></div>
<table class="list center-block">
	<tbody>
		<tr>
			<th>Context</th>
			<th>Task ID</th>
			<th>Name</th>
			<th>Host</th>
			<th>Debug Port</th>
			<th></th>
			<th></th>
		</tr>
		<%
		for (SuspendedTask task : tasks) {
			%><tr>
			<td><%= Routines.htmlspecialchars(task.getContext()) %></td>
			<td><%= Routines.htmlspecialchars(task.getTaskId()) %></td>
			<td><%= Routines.htmlspecialchars(task.getName()) %></td>
			<td><%= Routines.htmlspecialchars(task.getHost()) %></td>
			<td><%= Routines.htmlspecialchars(new Integer(task.getPort()).toString()) %></td>
			<td>
				<form id="run-task-form" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="taskid" value="<%=Routines.htmlspecialchars(task.getId().toString())%>" />
					<input type="submit" class="type-submit" name="run" value="Run without debugging"/>
				</form>
			</td>
			<td>
				<form id="delete-task-form" action="<%=page_.currentActionURL()%>">
					<input type="hidden" name="taskid" value="<%=Routines.htmlspecialchars(task.getId().toString())%>" />
					<input type="submit" class="type-submit" name="delete" value="Delete from list"/>
				</form>
			</td>
		</tr>
		<% } %>
	</tbody>
</table>
<% } %>
