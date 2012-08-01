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
	import="cz.cuni.mff.been.task.Task"
%><%
	TaskEntry[] tasks = (TaskEntry[])application.getAttribute("tasks");
	Map checkpoints = (Map)application.getAttribute("checkpoints");
	boolean showHost = ((Boolean)application.getAttribute("showHost")).booleanValue();
	boolean showContext = ((Boolean)application.getAttribute("showContext")).booleanValue();
	TaskListMode mode = (TaskListMode)application.getAttribute("mode");
%>
<% if (tasks.length > 0) { %>
	<table class="list full-width">
		<tr>
			<th>Task ID</th>
			<th>Task name</th>
			<th>Tree address</th>
			<th>Running time</th>
			<th>State</th>
			<% if (showHost) { %>
				<th>Host</th>
			<% } %>
			<% if (showContext) { %>
				<th>Context</th>
			<% } %>
			<th>&nbsp;</th>
		</tr>
		<% for(int i = 0; i < tasks.length; i++) { %>
			<% TaskEntry task = tasks[i]; %>
			<%
				long runningTime = 0;
				switch(task.getState()) {
					case SUBMITTED:
					case SCHEDULED:
						runningTime = 0;
						break;
					case RUNNING:
					case SLEEPING:
						runningTime = task.getCurrentTime() - task.getTimeStarted();
						break;
					case FINISHED:
						runningTime = task.getTimeFinished() - task.getTimeStarted();
						break;
					case ABORTED:
						/* If the task was aborted after it was started,
						 * it should have set the timeStarted and timeFinished
						 * to a sane value; otherwise both timeStarted and timeFinished
						 * times should be zero.
						 */
						runningTime = task.getTimeStarted() != 0 && task.getTimeFinished() != 0
							? task.getTimeFinished() - task.getTimeStarted()
							: 0;
						break;
					default:
						assert false: "Invalid tasks state.";
						runningTime = 0;
				}
				
				String klass;
				if (mode.equals(TaskListMode.NORMAL)) {
					klass = "task-state-" + Routines.htmlspecialchars(task.getState().toString());
					if (task.getState() == TaskState.FINISHED) {
						CheckPoint[] checkpointsInTask = (CheckPoint[])checkpoints.get(task);
						for (int j = 0; j < checkpointsInTask.length; j++) {
							if (checkpointsInTask[j].getName().equals(Task.CHECKPOINT_NAME_FINISHED)) {
								if (checkpointsInTask[j].getValue().equals(new Integer(Task.EXIT_CODE_SUCCESS))) {
									klass += "-success";
								} else {
									klass += "-error";
								}
								break;
							}
						}
					}
				} else {
					klass = "";
				}
				
				String stateString;
				if (mode.equals(TaskListMode.NORMAL)) {
					stateString = task.getState().toString();
				} else if (mode.equals(TaskListMode.BEFORE_EXECUTION)) {
					stateString = "N/A";
				} else if (mode.equals(TaskListMode.AFTER_EXECUTION)) {
					stateString = "finished";
				} else {
					throw new AssertionError("Invalid mode.");
				}
				
			%>
			<tr>
				<th><% if (mode.equals(TaskListMode.NORMAL)) { %><a
					href="<%=page_.moduleActionURL("tasks", "task-details")%>?cid=<%=Routines.htmlspecialchars(task.getContextId())%>&amp;tid=<%=Routines.htmlspecialchars(task.getTaskId())%>"
					><% } %><%=Routines.htmlspecialchars(task.getTaskId())%><% if (mode.equals(TaskListMode.NORMAL)) { %></a><% } %>
				</th>
				<th><%=Routines.htmlspecialchars(task.getTaskName())%></th>
				<td><%=Routines.htmlspecialchars(task.getTreePath())%></td>
				<td>
					<%=runningTime != 0
						? Routines.formatMillisAsHMS(runningTime)
						: "N/A"
					%>
				</td>
				<td class="<%=klass%>"><%=Routines.htmlspecialchars(stateString)%></td>
				<% if (showHost) { %>
					<td>
						<% if (task.getHostName() != null) { %>
							<a href="<%=page_.moduleActionURL("hosts", "host-details")%>?hostname=<%=Routines.htmlspecialchars(task.getHostName())%>"
							><%=Routines.htmlspecialchars(task.getHostName())%></a>
						<% } else { %>
							(unknown)
						<% } %>
					</td>
				<% } %>
				<% if (showContext) { %>
					<td><% if (mode.equals(TaskListMode.NORMAL)) { %><a
						href="<%=page_.moduleActionURL("tasks", "context-details")%>?cid=<%=Routines.htmlspecialchars(task.getContextId())%>"
						><% } %><%=Routines.htmlspecialchars(task.getContextId())%><% if (mode.equals(TaskListMode.NORMAL)) { %></a><% } %>
					</td>
				<% } %>
				<td>
					<form action="<%=page_.moduleActionURL("tasks", "task-kill")%>"
						onsubmit="return confirm('Do you really want to kill task &quot;<%=Routines.htmlspecialchars(Routines.javaScriptEscape(task.getTaskName()))%>&quot;?');">
						<input type="hidden" name="cid" value="<%=Routines.htmlspecialchars(task.getContextId())%>" />
						<input type="hidden" name="tid" value="<%=Routines.htmlspecialchars(task.getTaskId())%>" />
						<input type="submit" class="type-submit" value="Kill"
							<%=task.getState() != TaskState.RUNNING && task.getState() != TaskState.SLEEPING && task.getState() != TaskState.SUBMITTED ? "disabled='disabled'" : ""%>
						/>
					</form>
				</td>
			</tr>
		<% } %>
	</table>
<% } else { %>
	<div class="center">No tasks running <%=showHost ? "in this context" : "on this host"%>.</div>
<% } %>