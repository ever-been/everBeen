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
	Messages infoMessages = page_.getInfoMessages();
%>
<% if (!infoMessages.isEmpty()) { %>
	<div class="info-messages"><div class="top-left"><div class="top-right"><div class="bottom-left"><div class="bottom-right"><div class="inner">
		<% if (infoMessages.size() == 1) { %>
			<% Message message = (Message)infoMessages.iterator().next(); %>
			<%=message.getFormat().equals(Message.Format.TEXT)
				? Routines.htmlspecialchars(message.getText())
				: message.getText()%>
		<% } else { %>
			<ul>
				<% for (Iterator i = infoMessages.iterator(); i.hasNext(); ) { %>
					<% Message message = (Message)i.next(); %>
					<li><%=message.getFormat().equals(Message.Format.TEXT)
						? Routines.htmlspecialchars(message.getText())
						: message.getText()%></li>
				<% } %>
			</ul>
		<% } %>
	</div></div></div></div></div></div>
<% } %>