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
	import="cz.cuni.mff.been.logging.*"
	import="java.text.SimpleDateFormat"
	import="java.util.EnumSet"
%><%
	LogRecord[] logRecords = (LogRecord[])application.getAttribute("logRecords");
	EnumSet logFields = (EnumSet)application.getAttribute("logFields");
	OutputHandle standardOutputHandle = (OutputHandle)application.getAttribute("standardOutputHandle");
	OutputHandle errorOutputHandle = (OutputHandle)application.getAttribute("errorOutputHandle");
%>	
<div class="tabsheet">
	<ul class="tabsheet-tabs">
		<li class="active"><a href="#" onclick="tabsheetActivate(this, 'logs-sheet'); return false;">Logs</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'standard-output-sheet'); return false;">Standard output</a></li>
		<li><a href="#" onclick="tabsheetActivate(this, 'error-output-sheet'); return false;">Error output</a></li>
	</ul>
	<div class="tabsheet-sheets">
		<div id="logs-sheet" class="tabsheet-sheet-visible">
			<%
				HashMap data = new HashMap();
				data.put("logRecords", logRecords);
				data.put("logFields", logFields);
				data.put("logEntityColumns", new LogEntityColumn[] {});
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("log-view", data);
			%>
		</div>
		<div id="standard-output-sheet" class="tabsheet-sheet-invisible">
			<%
				data.clear();
				data.put("outputHandle", standardOutputHandle);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("output-view", data);
			%>
		</div>
		<div id="error-output-sheet" class="tabsheet-sheet-invisible">
			<%
				data.clear();
				data.put("outputHandle", errorOutputHandle);
				out.flush(); // unforunately can't be inside writeTemplate method
				page_.writeTemplate("output-view", data);
			%>
		</div>
	</div>
</div>			