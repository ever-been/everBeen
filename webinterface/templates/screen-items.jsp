<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: David Majda
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
	import="cz.cuni.mff.been.webinterface.screen.*"
	import="cz.cuni.mff.been.common.id.*"
%><%
	Item[] items = (Item[])application.getAttribute("items");
	boolean indented = ((Boolean)application.getAttribute("indented")).booleanValue();
%>
<% for (int i = 0; i < items.length; i++) { %>
	<% Item item = items[i]; %>
			
	<%-- StaticText --%>
	<% if (item instanceof StaticText) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<% StaticText staticText = (StaticText)item; %>
				<%=Routines.htmlspecialchars(staticText.getText())%>
			</td>
		</tr>
	<% } %>

	<%-- Checkbox --%>
	<% if (item instanceof Checkbox) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<% Checkbox checkbox = (Checkbox)item; %>
				<input type="checkbox" id="<%=checkbox.formElementName()%>"
					name="<%=checkbox.formElementName()%>"
				 	<%= checkbox.isChecked() ? " checked=\"checked\"" : "" %>/>
					<label for="<%=checkbox.formElementName()%>"><%=Routines.htmlspecialchars(checkbox.getLabel())%></label>
			</td>
		</tr>
	<% } %>

	<%-- Input --%>
	<% if (item instanceof Input) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<% Input input = (Input)item; %>
				<% if( input.getSize() == Input.Size.AREA ) { %>
				<textarea name="<%=item.formElementName()%>"
					class="name-<%=item.formElementName()%>"
					rows="5" cols="50"><%=Routines.htmlspecialchars(input.getValue())%></textarea>
				<% } else { %>
				<input type="text" name="<%=item.formElementName()%>"
			 		class="type-text-<%=input.getSize().toString().toLowerCase()%>"
					value="<%=Routines.htmlspecialchars(input.getValue())%>" />
				<% } %>
			</td>
		</tr>
	<% } %>

	<%-- RSLInput --%>
	<% if (item instanceof RSLInput) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<% RSLInput input = (RSLInput)item; %>
				<%
					HashMap data = new HashMap();
					
					data.put("name", item.formElementName());
					data.put("value", input.getValue());
					data.put("type", "host");
					out.flush(); // unforunately can't be inside writeTemplate method
					page_.writeTemplate("rsl-widget", data);
				%>
			</td>
		</tr>
	<% } %>

	<%-- Select --%>
	<% if (item instanceof Select) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<%
					Select select = (Select)item;
					int selectedIndex = select.getSelectedIndex();
					int size = select.getOptions().length < 10
						? select.getOptions().length
						: 10;
				%>
				<select name="<%=item.formElementName()%>"
					 size="<%=size%>">
					 <% for (int j = 0; j < select.getOptions().length; j++) { %>
					 	<% Option option = select.getOptions()[j]; %>
					 	<option value="<%=Routines.htmlspecialchars(option.getId())%>"
					 		<%= j == selectedIndex ? " selected=\"selected\"" : "" %>
					 	><%=Routines.htmlspecialchars(option.getLabel())%></option>
					 <% } %>
				</select>
			</td>
		</tr>
	<% } %>

	<%-- MultiSelect --%>
	<% if (item instanceof MultiSelect) { %>
		<tr>
			<th<%=indented ? " class=\"indented\"" : ""%>>
				<%=Routines.htmlspecialchars(item.getLabel())%>:
			</th>
			<td>
				<%
					MultiSelect multiSelect = (MultiSelect)item;
					int[] selectedIndexes = multiSelect.getSelectedIndexes();
					Arrays.sort(selectedIndexes);
					int size = multiSelect.getOptions().length < 10
						? ( multiSelect.getOptions().length > 5
							? multiSelect.getOptions().length
							: 5 )
						: 10;
				%>
				<input type="hidden" name="<%=item.formElementName()%>"
					 value="<%=Routines.join(",", selectedIndexes)%>" />
				<input type="hidden" name="<%=item.formElementName()%>-allow-multiple"
					 value="<%= multiSelect.getAllowMultiple() ? "true" : "false" %>" />
				<script type="text/javascript">
					multiSelectOptions["<%=item.formElementName()%>"] = [
						<% for (int j = 0; j < multiSelect.getOptions().length; j++) { %>
					 		<% Option option = multiSelect.getOptions()[j]; %>
						 	{
						 		id: "<%=Routines.javaScriptEscape(option.getId())%>",
						 		label: "<%=Routines.javaScriptEscape(option.getLabel())%>"
						 	},
						<% } %>
					];
				</script>
				<table class="multiselect">
					<tr>
						<td>Selected:</td>
						<td>&nbsp;</td>
						<td>Not selected:</td>
					</tr>
					<tr>
						<td>
							<select name="<%=item.formElementName()%>-selected"
								size="<%=size%>"
								onclick="multiSelectSelectedClick(this.form, '<%=item.formElementName()%>');"
							>
								<% for (int j = 0; j < multiSelect.getOptions().length; j++) { %>
								 	<% if (Arrays.binarySearch(selectedIndexes, j) >= 0) { %>
									 	<% Option option = multiSelect.getOptions()[j]; %>
						 				<option value="<%=Routines.htmlspecialchars(option.getId())%>">
						 					<%=Routines.htmlspecialchars(option.getLabel())%>
									 	</option>
									<% } %>
								<% } %>
							</select>
						</td>
						<td>
							<input type="button" class="type-button" value="<<" disabled="disabled"
								name="<%=item.formElementName()%>-add-button"
								onclick="multiSelectAddOption(this.form, '<%=item.formElementName()%>');"
							/>
							<br />
							<input type="button" class="type-button" value=">>" disabled="disabled"
								name="<%=item.formElementName()%>-delete-button"
								onclick="multiSelectDeleteOption(this.form, '<%=item.formElementName()%>');"
							/>
						</td>
						<td>
							<select name="<%=item.formElementName()%>-not-selected"
								size="<%=size%>"
								onclick="multiSelectNotSelectedClick(this.form, '<%=item.formElementName()%>');"
							>
								<% for (int j = 0; j < multiSelect.getOptions().length; j++) { %>
								 	<% if (Arrays.binarySearch(selectedIndexes, j) < 0 || multiSelect.getAllowMultiple() ) { %>
									 	<% Option option = multiSelect.getOptions()[j]; %>
						 				<option value="<%=Routines.htmlspecialchars(option.getId())%>">
						 					<%=Routines.htmlspecialchars(option.getLabel())%>
									 	</option>
									<% } %>
								<% } %>
							</select>
							<img src="../../img/clear.gif" onload="multiSelectRebuildSelects(
								document.getElementById('benchmarks-run-configure-plugin-form'),
								'<%=item.formElementName()%>');">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<% } %>
			
	<%-- RadiosWithSections --%>
	<% if (item instanceof RadiosWithSections) { %>
		<% RadiosWithSections radios = (RadiosWithSections)item; %>
		<% int selectedIndex = radios.getSelectedIndex(); %>
		<% for (int j = 0; j < radios.getItems().length; j++) { %>
			<% RadioWithSectionItem radioItem = radios.getItems()[j]; %>
			<tr>
				<th colspan="2"<%=indented ? " class=\"indented\"" : ""%>>
				 	<label><input type="radio" name="<%=item.formElementName()%>"
				 		value="<%=Routines.htmlspecialchars(radioItem.getOption().getId())%>"
				 		<%= j == selectedIndex ? " checked=\"checked\"" : "" %>
				 	/>
				 	<%=Routines.htmlspecialchars(radioItem.getOption().getLabel())%></label>
				</th>
			</tr>
			<%
				String description = radioItem.getSection() != null
					? radioItem.getSection().getDescription()
					: null;
				if (description != null && !description.equals("")) {
			%>
				<tr>
					<td colspan="2" class="indented">
						<%=Routines.htmlspecialchars(description)%>
					</td>
				</tr>
			<% } %>
			<%
				if (radioItem.getSection() != null) {
					HashMap data = new HashMap();
					data.put("items", radioItem.getSection().getItems());
					data.put("indented", Boolean.TRUE);
					out.flush(); // unforunately can't be inside writeTemplate method
					page_.writeTemplate("screen-items", data);
				}
			%>
		<% } %>
	<% } %>
<% } %>
