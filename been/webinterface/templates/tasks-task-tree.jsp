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
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	String htmlPath = Routines.htmlspecialchars("/");
	String jsPath = Routines.javaScriptEscape("/");
	String jsUrl = Routines.javaScriptEscape(page_.actionURL("AJAXtaskTreeNode")+"?node="+htmlPath);

%>
<table class="center-block"><tr><td>
	<div id="/" class="left" style="min-width: 400px">
		<div>
			<a href="#" title="(re)load this node information"
				onclick="treeExpand('<%=jsPath%>', '<%=jsUrl%>')">
				<%=Routines.htmlspecialchars("<ROOT>") %>
			</a>
		</div>
		<div class="tree-node-children" id="<%= htmlPath %>-children">
			<%
			HashMap widgetData = new HashMap();
			widgetData.put("name", application.getAttribute("name"));
			widgetData.put("path", application.getAttribute("path"));
			widgetData.put("task", application.getAttribute("task"));
			widgetData.put("children", application.getAttribute("children"));

			out.flush(); // unforunately can't be inside writeTemplate method
			page_.writeTemplate("tasks-task-tree-node", widgetData);
			%>
		</div>
	</div>
</td></tr></table>
