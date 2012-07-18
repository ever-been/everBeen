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
%><%
	page_.writeInfoMessages();
	page_.writeErrorMessages();
	
	String taskManagerHostname = (String)application.getAttribute("taskManagerHostname");
	boolean showDebugOptions = ((Boolean)application.getAttribute("showDebugOptions")).booleanValue();

	boolean showHostRuntimeConfiguration = ((Boolean)application.getAttribute("showHostRuntimeConfiguration")).booleanValue();
	String maxPackageCacheSize = null;
	String keptClosedContextCount = null;
	if (showHostRuntimeConfiguration) {
		maxPackageCacheSize = (String)application.getAttribute("maxPackageCacheSize");
		keptClosedContextCount = (String)application.getAttribute("keptClosedContextCount");
	}

	boolean showHostManagerConfiguration = ((Boolean)application.getAttribute("showHostManagerConfiguration")).booleanValue();
	String hostDetectionTimeout = null;
	String pendingRefreshInterval = null;
	String activityMonitorInterval = null;
	String deadHostTimeout = null;
	String briefModeInterval = null;
	String defaultDetailedModeInterval = null;
	if (showHostManagerConfiguration) {
		hostDetectionTimeout = (String)application.getAttribute("hostDetectionTimeout");
		pendingRefreshInterval = (String)application.getAttribute("pendingRefreshInterval");
		activityMonitorInterval = (String)application.getAttribute("activityMonitorInterval");
		deadHostTimeout = (String)application.getAttribute("deadHostTimeout");
		briefModeInterval = (String)application.getAttribute("briefModeInterval");
		defaultDetailedModeInterval = (String)application.getAttribute("defaultDetailedModeInterval");
	}
%>
<form id="configuration-configuration-form" action="<%=page_.currentActionURL()%>" method="post">
	<input type="hidden" name="show-host-runtime-configuration" value="<%=showHostRuntimeConfiguration%>" />
	<input type="hidden" name="show-host-manager-configuration" value="<%=showHostManagerConfiguration%>" />
	<table class="form center-block">
		<tr>
			<td colspan="2"><h2>General</h2></td>
		</tr>
		<tr>
			<th>Task Manager host name:</th>
			<td>
				<input type="text" name="task-manager-hostname"
					class="name-task-manager-hostname"
					value="<%=Routines.htmlspecialchars(taskManagerHostname)%>" />
			</td>
		</tr>
		<tr>
			<th>Number of closed contexts kept:</th>
			<td>
				<input type="text" name="kept-closed-context-count"
					class="name-kept-closed-context-count"
					value="<%=Routines.htmlspecialchars(keptClosedContextCount)%>" />
			</td>
		</tr>
		<tr>
			<th>Debug:</th>
			<td>
				<label><input type="checkbox" name="show-debug-options"
					<%=showDebugOptions ? " checked=\"checked\"" : ""%>
					/> Show debug options</label><br />
			</td>
		</tr>
		<tr>
			<td colspan="2"><h2>Host Runtime</h2></td>
		</tr>
		<% if (showHostRuntimeConfiguration) { %>
			<tr>
				<th>Package cache size limit:</th>
				<td>
					<input type="text" name="max-package-cache-size"
						class="name-max-package-cache-size"
						value="<%=Routines.htmlspecialchars(maxPackageCacheSize)%>" />
					MB
				</td>
			</tr>
		<% } else { %>
			<tr>
				<td colspan="2" class="center">Host Runtime configuration is not
				available, because RMI reference to the Task Manager could not
				be obtained.</td>
			</tr>
		<% } %>
		<tr>
			<td colspan="2"><h2>Host Manager</h2></td>
		</tr>
		<% if (showHostManagerConfiguration) { %>
			<tr>
				<th>Host detection timeout:</th>
				<td>
					<input type="text" name="host-detection-timeout"
						class="name-host-detection-timeout"
						value="<%=Routines.htmlspecialchars(hostDetectionTimeout)%>" />
					s
				</td>
			</tr>
			<tr>
				<th>Host detection timeout check interval:</th>
				<td>
					<input type="text" name="pending-refresh-interval"
						class="name-pending-refresh-interval"
						value="<%=Routines.htmlspecialchars(pendingRefreshInterval)%>" />
					s
				</td>
			</tr>
			<tr>
				<th>Activity Monitor refresh interval:</th>
				<td>
					<input type="text" name="activity-monitor-interval"
						class="name-activity-monitor-interval"
						value="<%=Routines.htmlspecialchars(activityMonitorInterval)%>" />
					s
				</td>
			</tr>
			<tr>
				<th>Host crash timeout:</th>
				<td>
					<input type="text" name="dead-host-timeout"
						class="name-dead-host-timeout"
						value="<%=Routines.htmlspecialchars(deadHostTimeout)%>" />
					s
				</td>
			</tr>
			<tr>
				<th>Brief mode sampling interval:</th>
				<td>
					<input type="text" name="brief-mode-interval"
						class="name-brief-mode-interval"
						value="<%=Routines.htmlspecialchars(briefModeInterval)%>" />
					ms
				</td>
			</tr>
			<tr>
				<th>Default detailed mode sampling interval:</th>
				<td>
					<input type="text" name="default-detailed-mode-interval"
						class="name-default-detailed-mode-interval"
						value="<%=Routines.htmlspecialchars(defaultDetailedModeInterval)%>" />
					ms
				</td>
			</tr>
		<% } else { %>
			<tr>
				<td colspan="2" class="center">Host Manager configuration is not
				available, because RMI reference to the Host Manager could not
				be obtained.</td>
			</tr>
		<% } %>
		<tr>
			<td class="buttons" colspan="2">
				<input type="submit" class="type-submit" name="save" value="Save" />
			</td>
		</tr>
	</table>
</form>
