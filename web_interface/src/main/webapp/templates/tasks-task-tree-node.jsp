<%--

  BEEN: Benchmarking Environment
  ==============================
   
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
	import="cz.cuni.mff.been.taskmanager.data.*"
	import="cz.cuni.mff.been.webinterface.tasks.*"
	import="cz.cuni.mff.been.common.id.*"
%><%String name = (String)application.getAttribute("name");
	String path = (String)application.getAttribute("path");
	if( path == null ){ path = ""; }

	TaskEntryImplementation task = (TaskEntryImplementation)application.getAttribute("task");
	String[] children = (String[])application.getAttribute("children");

	if( task != null ){%>Task: <a href="<%= page_.actionURL("task-details") %>?cid=<%=
			Routines.htmlspecialchars(task.getContextId()) %>&amp;tid=<%=
			Routines.htmlspecialchars(task.getTaskId())%>"><%=
			Routines.htmlspecialchars(task.getTaskName()) %></a><%
	}

	if( children != null ){
		for( String child : children ){
		    String childPath = path+"/"+child;

			String htmlPath = Routines.htmlspecialchars(childPath);
			String jsPath = Routines.javaScriptEscape(childPath);
			String jsUrl = Routines.javaScriptEscape(page_.actionURL("AJAXtaskTreeNode")+"?node="+htmlPath);

			%><div id="<%= htmlPath %>">
				<div>
					<a href="#" id="<%= htmlPath %>-plus"
						onclick="treeExpand('<%=jsPath%>', '<%=jsUrl%>')"><img src="../../img/plus.gif"></a>
					<a href="#" id="<%= htmlPath %>-minus"  style="display: none;"
						onclick="treeCollapse('<%=jsPath%>')"><img src="../../img/minus.gif"></a>
					<a href="#" title="(re)load this node information"
						onclick="treeExpand('<%=jsPath%>', '<%=jsUrl%>')">
						<%=Routines.htmlspecialchars(child) %>
					</a>
	 			</div>
				<div class="tree-node-children" id="<%= htmlPath %>-children"></div>
			</div>
			<%
		}
	}
	%>
</div>

