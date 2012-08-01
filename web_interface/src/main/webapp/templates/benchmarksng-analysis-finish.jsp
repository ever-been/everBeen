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
	import="cz.cuni.mff.been.jaxb.config.Config"
	import="cz.cuni.mff.been.jaxb.config.Type"
	import="cz.cuni.mff.been.jaxb.config.Value"
	import="cz.cuni.mff.been.jaxb.config.Group"
	import="cz.cuni.mff.been.jaxb.config.Item"

	import="cz.cuni.mff.been.benchmarkmanagerng.Analysis"
	import="cz.cuni.mff.been.benchmarkmanagerng.Configuration"
	import="cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator"
%><%
	page_.writeInfoMessages();
	page_.writeWarningMessages();
	page_.writeErrorMessages();

	Analysis analysis = (Analysis)application.getAttribute( "analysis" );

%>

<form id="benchmarks-run-configure-plugin-form" action="<%=page_.currentActionURL()%>" method="post">
	<input type="hidden" name="sid" value="finish" />
	<table class="form center-block">
		<tr><td>
		<%
			HashMap widgetData = new HashMap();
			widgetData.put("analysis", analysis);
			widgetData.put("contexts", null);
			page_.writeTemplate("benchmarksng-analysis-widget", widgetData);
		%>
		</td></tr>
		<tr>
			<td class="buttons">
				<input type="submit" class="type-submit" name="previous" value="< Previous" />
				<input type="submit" class="type-submit" name="cancel" value="Cancel" />
				<input type="submit" class="type-submit" name="finish" value="Finish" />
			</td>
		</tr>
	</table>
</form>
