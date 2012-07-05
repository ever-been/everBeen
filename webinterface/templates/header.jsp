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
	String onload = (String)application.getAttribute("onload");
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="David Majda" />
		<meta name="copyright" content="Copyright &copy; 2004-05 BEEN Team" />

		<title>BEEN<%=page_.getTitle() != "" ? ": " + Routines.htmlspecialchars(page_.getTitle()) : ""%></title>
		<link href="<%=page_.getRootPath() + "styles/styles.css"%>" rel="stylesheet" type="text/css" />
		<!--[if lt IE 7]>
		<link href="<%=page_.getRootPath() + "styles/styles-ie.css"%>" rel="stylesheet" type="text/css" />
		<![endif]-->
		<script src="<%=page_.getRootPath() + "scripts/scripts.js"%>" type="text/javascript"></script>
		<link href="<%=page_.getRootPath() + "favicon.ico"%>" rel="shortcut icon" type="image/x-icon" />
	</head>
	<body id="been-body"<%=onload != "" ? " onload=\"" + Routines.htmlspecialchars(onload) + "\"" : "" %>>
		<% if (page_.getLayoutType() == Page.LayoutType.NORMAL) { %>
			<div id="header">
				<a id="logo" href="<%=page_.getRootPath()%>"></a>
				<ul>
					<% int moduleCount = page_.getModuleCount(); %>
					<% for (int i = 0; i < moduleCount; i++) { %>
						<%
							Module module = page_.getModule(i);
							String klass = module.getId().equals(page_.getActiveModule()) ? "active" : "";
							String href = page_.getRootPath() + module.getId() +"/";
						%>
						<li<%=klass != "" ? " class=\"" + klass + "\"" : ""%>><a href="<%=href%>"><%=Routines.htmlspecialchars(module.getName())%></a></li>
					<% } %>
				</ul>
				<div id="menu">
					<% if (page_.getActiveModule() != "") { %>
						<%
							Module module = page_.getModuleById(page_.getActiveModule());
							MenuItem[] menu = module.getMenu();
						%>
						<% for (int i = 0; i < menu.length; i++) { %>
							<% if (i != 0) { %> | <% } %>
							<a href="<%=page_.getRootPath() + module.getId() + "/" + menu[i].getId()%>/"<%=menu[i].getId().equals(page_.getActiveAction()) ? "class=\"active\"" : ""%>><%=Routines.htmlspecialchars(menu[i].getName())%></a>
						<% } %>
					<% } else { %>
						&nbsp;
					<% } %>
				</div>
			</div>
		<% } %>
		<div id="content">
			<% if (page_.getShowTitle() && page_.getTitle() != "") { %><h1><%=Routines.htmlspecialchars(page_.getTitle())%></h1><% } %>		