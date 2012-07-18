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
	import="java.lang.reflect.*"
	import="cz.cuni.mff.been.common.*"
	import="cz.cuni.mff.been.softwarerepository.*"
	import="cz.cuni.mff.been.webinterface.packages.*" 
%><%
	PackageMetadata metadata = (PackageMetadata)application.getAttribute("metadata");
%>
<table class="form center-block">
	<tr>
		<td colspan="2"><h2>Package information</h2></td>
	</tr>
	<% for (int i = 0; i < PackageMetadata.ATTRIBUTE_INFO.length; i++) { %>
		<tr>
			<% AttributeInfo info = PackageMetadata.ATTRIBUTE_INFO[i]; %>
			<th><%=Routines.htmlspecialchars(Routines.ucfirst(info.getHumanName()))%>:</th>
			<td><%
				Object value = null;
				try {
					value = info.getGetter().invoke(metadata, (Object[])null);
				} catch (IllegalArgumentException e) {
					assert false : "This should not happen.";
				} catch (IllegalAccessException e) {
					assert false : "This should not happen.";
				} catch (InvocationTargetException e) {
					assert false : "This should not happen.";
				}

				if (value == null) { 
					out.println("N/A");
				} else if (info.getKlass().getName().equals("java.lang.String")) {
					out.println(Routines.htmlspecialchars((String)value));
				} else if (info.getKlass().getName().equals("java.util.Date")) {
					out.println(Routines.htmlspecialchars(((Date)value).toString()));
				} else if (info.getKlass().getName().equals("java.util.ArrayList")) {
					boolean first = true;
					for (Iterator iterator = ((ArrayList)value).iterator(); iterator.hasNext(); ) {
						if (!first) {
							out.print(", ");
						}
						out.print(Routines.htmlspecialchars((String)iterator.next()));
						first = false;
           			}
				} else if (info.getKlass().getName().equals("cz.cuni.mff.been.common.Version")) {
					out.println(Routines.htmlspecialchars(((Version)value).toString()));
				} else if (info.getKlass().getName().equals("cz.cuni.mff.been.softwarerepository.PackageType")) {
					out.println(Routines.htmlspecialchars(((PackageType)value).toString()));
				} else {
					assert false: "Should not happen.";
				}
            %></td>
		</tr>
	<% } %>
</table>