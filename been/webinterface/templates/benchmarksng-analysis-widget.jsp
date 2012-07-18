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
	import="cz.cuni.mff.been.benchmarkmanagerng.Analysis"
	import="cz.cuni.mff.been.benchmarkmanagerng.AnalysisState"
	import="cz.cuni.mff.been.benchmarkmanagerng.Configuration"
	import="cz.cuni.mff.been.benchmarkmanagerng.module.BMModule"
%><%
	HashMap widgetData = new HashMap();
	Analysis analysis = (Analysis)application.getAttribute( "analysis" );
	Map<String, AnalysisState> contexts = (Map<String, AnalysisState>)application.getAttribute( "contexts" );

	List<BMModule> modules = new ArrayList<BMModule>();
	modules.add(analysis.getGenerator());
	if( analysis.getEvaluators() != null ){
		modules.addAll(analysis.getEvaluators());
	}

	String lastTime = analysis.getLastTime() != null ? analysis.getLastTime().toString() : "never";

	BMModule module = null;
%>
<div class="tabsheet">
	<ul class="tabsheet-tabs">
		<li class="active">
			<a onclick="tabsheetActivate(this, 'general-sheet'); return false;" href="#">General</a>
		</li><%
		for(int i=0; i < modules.size(); i++ ){
			module = modules.get(i);
			%><li class="">
			<a onclick="tabsheetActivate(this, 'module-<%= i %>-sheet'); return false;" href="#"><%= module.getPackageName() %></a>
			</li><%
		} %>
	</ul>

	<div class="tabsheet-sheets">
		<div id="general-sheet" class="tabsheet-sheet-visible">
			<table class="form"><tbody>
				<tr>
					<td colspan="3"><%= Routines.htmlspecialchars(analysis.getDescription()) %></td>
				</tr><tr>
					<th>Results:</th>
					<td colspan="2">
						<%
							String resultsLink;
							String linkTitle;
							if( analysis.getResultsLink() != null && !analysis.getResultsLink().equals("") ){
								resultsLink = analysis.getResultsLink();
								linkTitle = analysis.getResultsLink();
							} else {
								resultsLink = page_.moduleActionURL("resultsrepositoryng","list-datasets")
												+"?analysis="+analysis.getName();
								linkTitle = "Results Repository";
							}
						%><a href="<%= Routines.htmlspecialchars(resultsLink) %>"><%= Routines.htmlspecialchars(linkTitle) %></a>
					</td>
				</tr><tr>
					<th>Status:</th>
					<td class="analysis-status-<%= analysis.getState().toString().toLowerCase()
						%>" colspan="2"><%= analysis.getState().toString() %></td>
				</tr><tr title="last time when generator succeeded">
					<th>Last run:</th>
					<td class="<%= analysis.shouldBeScheduled() ? "analysis-time-passed" : ""
						%>" colspan="2"><%= lastTime %></td>
				</tr><tr>
					<th>Scheduling period:</th>
					<td colspan="2"><%= analysis.getRunPeriod() != null ?
						analysis.getRunPeriod().toString()+" minutes" : "-" %></td>
				</tr><tr>
					<th>Generator and monitor host RSL:</th>
					<td colspan="2"><%= Routines.htmlspecialchars(analysis.getGeneratorHostRSL().toString()) %></td>
				</tr><%
				if( contexts != null ){

					%><tr>
						<td colspan="3"><h2>Active contexts:</h2></td>
					</tr><%

					if( contexts.isEmpty() ){

					    %><tr><td colspan="3">No active contexts</td></tr><%

					} else for( String ctxName : contexts.keySet() ) {

					    AnalysisState ctxState = contexts.get(ctxName);
						%><tr>
						<td><a href="<%= page_.moduleActionURL("tasks","context-details")
							%>?cid=<%= Routines.htmlspecialchars( ctxName )%>"
							><%= Routines.htmlspecialchars( ctxName ) %></a></td>
						<td class="analysis-status-<%=ctxState.toString().toLowerCase()
							%>"><%= ctxState.toString() %></td>
						<td><form method="post" action="<%=page_.currentActionURL()
							%>?name=<%= Routines.htmlspecialchars(analysis.getName()) %>">
	                        <input type="submit" name="finish_context" value="Mark as finished">
							<input type="hidden" name="context" value="<%= Routines.htmlspecialchars( ctxName ) %>">
						</form></td>
					</tr>
					<%

					}
				}
				%>
			</tbody></table>
		</div>
<%
		Configuration configuration = null;
		String[] value = null;

		for(int i=0; i < modules.size(); i++ ){
			module = modules.get(i);
			%><div id="module-<%= i %>-sheet" class="tabsheet-sheet-invisible">
				<table class="form"><tbody>
					<tr>
						<th>Package name:</th>
						<td><%= module.getName() %></td>
					</tr><tr>
						<th>Package version:</th>
						<td><%= module.getVersion() %></td>
					</tr><tr>
						<th>Configuration:</td>
						<td>
							<table class="real"><tbody>
								<tr>
									<th>Property</th>
									<th>Value</th>
								</tr><%
								configuration = module.getConfiguration();
								for(String key : configuration.keySet() ){
									value = configuration.get(key);
									%><tr>
									<td><%= Routines.htmlspecialchars(key) %></td>
									<td><%= Routines.nl2br(Routines.htmlspecialchars(Routines.join("<br>",value))) %></td>
								</tr><%
								} %>
							</tbody></table>
						</td>
					</tr>
				</tbody></table>
			</div><%
		} %>
	</div>
</div>
