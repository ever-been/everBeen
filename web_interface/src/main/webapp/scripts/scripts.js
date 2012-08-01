/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */

/* ===== Bits and pieces of functional programming... ===== */

/* String.split works in a bit uncomfortable way - when called on empty string,
 * it returns an array with one element (empty string). We want it to retrun
 * empty array in this case.
 */
String.prototype.split2 = function(separator) {
	return this != "" ? this.split(separator) : [];
}

/* Now some definitions for retarded browsers... */

if (Array.prototype.filter == undefined) {
	Array.prototype.filter = function(callback) {
		var result = [];
		for (var i = 0; i < this.length; i++) {
			if (callback(this[i], i, this)) {
				result.push(this[i]);
			}
		}
		return result;
	}
}

if (Array.prototype.map == undefined) {
	Array.prototype.map = function(callback) {
		var result = [];
		for (var i = 0; i < this.length; i++) {
			result.push(callback(this[i], i, this));
		}
		return result;
	}
}

if (Array.prototype.indexOf == undefined) {
	Array.prototype.indexOf = function(searchElement, fromIndex) {
		if (fromIndex == undefined) {
			fromIndex = 0;
		} else if (fromIndex < 0) {
			fromIndex = this.length - fromIndex;
		}
		
		for (var i = fromIndex; i < this.length; i++) {
			if (this[i] == searchElement) {
				return i;
			};
		}
		return -1;
	}
}

/* ===== Autofocus helper functions ===== */

var windowClicked    = false;
var windowKeyPressed = false;

function windowClick() {
	windowClicked = true;
}

function windowKeyPress(event) {
	windowKeyPressed = true;

	/* Ctrl+Alt+Shift+B */
	if (event.ctrlKey && event.altKey && event.shiftKey && event.charCode == 66) {
		document.getElementById("logo").style.backgroundImage = "url('/been/img/logo-animated.gif')";
	}
}

window.onclick = windowClick;
window.onkeypress = windowKeyPress;

function shouldAutoFocus() {
	return !windowClicked
		&& !windowKeyPressed
		&& document.documentElement.scrollTop == 0;
}

function focusElementById(id) {
	document.getElementById(id).focus();
}

function focusElementByFormAndElement(form, element) {
	document.forms[form].elements[element].focus();
}

/* ===== Routines ===== */

var NUMBER_SET_SEPARATOR = ",";

function numberSetInputAdd(input, number) {
	var numbers = input.value.split2(NUMBER_SET_SEPARATOR);
	numbers.push(number);
	input.value = numbers.join(NUMBER_SET_SEPARATOR);
}

function numberSetInputDelete(input, number) {
	var numbers = input.value.split2(NUMBER_SET_SEPARATOR);
	numbers.splice(numbers.indexOf(number.toString()), 1);
	input.value = numbers.join(NUMBER_SET_SEPARATOR);
}

function browserIsIE() {
	return navigator.userAgent.indexOf("MSIE") != -1
		&& navigator.userAgent.indexOf("Opera") == -1;
}

/* This function is necessary, because IE has problems with form element names
 * containing "[" and "]". Such elements aren't included in the elements
 * associative array.
 *
 * We work around this issue by traversing the elements by numerical index and
 * manually comparing their names.
 */
function getFormElement(form, element) {
	if (!browserIsIE) {
		return document.forms[form].elements[element];
	} else {
		var elements = document.forms[form].elements;
		for (var i = 0; i < elements.length; i++) {
			if (i == elements[i] || elements[i].name == element) {
				return elements[i];
			}
		}
		return null;
	}
}

/* This is a little hack: for some reason (a bug?), IE reports bigger length
 * of arrays, so we must lower it.
 *
 * Bury this browser!
 */
function arrayLength(a) {
	return browserIsIE() ? a.length - 1 : a.length;
}

function swapNodes(a, b) {
	/* If b is next to a, the isertBefore method will fail at least in IE and
	 * Opera (trying to insert b before itself, when b is detached from the
	 * document).
	 *
	 * In this case we simply call ourselves with reversed parameters.
	 */
	if (a.nextSibling != b) {
		var nextSibling = a.nextSibling;
		var parentNode = a.parentNode;
		b.parentNode.replaceChild(a, b);
		parentNode.insertBefore(b, nextSibling);
	} else {
		swapNodes(b, a);
	}
}

function findFirstLink(element) {
	for (var child = element.firstChild; child; child = child.nextSibling) {
		if (child.nodeType == 1 && child.nodeName == "A") { // it is an "a" element
			return child;
		}
	}
	return null;
}

function writeCookie(name, value, days) {
  var expires = new Date();
  expires.setTime(expires.getTime() + (86400000 * days));
  document.cookie = name + "=" + escape(value) + "; expires="
    + expires.toGMTString();
}

function readCookie(name) {
  var cookies = document.cookie.split(/;[ ]+/);
  for (var i = 0; i < cookies.length; i++) {
    var parts = cookies[i].split("=");
    if (parts[0] == name) {
      return parts[1];
     }
  }
  return null;
}

/* ===== Tabsheets ===== */

function tabsheetActivate(sender, sheetId) {
	var senderTab = sender.parentNode;
	var tabsheetTabs = senderTab.parentNode;
	for (var tab = tabsheetTabs.firstChild; tab; tab = tab.nextSibling) {
		if (tab.nodeType == 1) { // it is element
			if (tab != senderTab) {
				tab.className = "";
			} else {
				tab.className = "active";
			}
		}
	}
	
	var tabsheetSheets = tabsheetTabs;
	do {
		tabsheetSheets = tabsheetSheets.nextSibling;
	} while (tabsheetSheets && tabsheetSheets.nodeType != 1);
	
	for (var sheet = tabsheetSheets.firstChild; sheet; sheet = sheet.nextSibling) {
		if (sheet.nodeType == 1) { // it is element
			if (sheet.getAttribute("id") != sheetId) {
				sheet.className = "tabsheet-sheet-invisible";
			} else {
				sheet.className = "tabsheet-sheet-visible";
			}
		}
	}
}

/* ===== MultiSelects ==== */

var multiSelectOptions = {};

function multiSelectBuildSelect(name, select, callback) {
	while (select.hasChildNodes()) {
		select.removeChild(select.firstChild);
	}
	
	for (var i = 0; i < arrayLength(multiSelectOptions[name]); i++) {
		for (var j = callback(i); j > 0; j--) {
			var option = document.createElement("option");
			option.value = multiSelectOptions[name][i].id;
			option.appendChild(document.createTextNode(multiSelectOptions[name][i].label));
			select.appendChild(option);
		}
	}
}

function multiSelectRebuildSelects(form, name) {
	var selectedSelect = form.elements[name + "-selected"];
	var notSelectedSelect = form.elements[name + "-not-selected"];
	var allowMultiple = form.elements[name + "-allow-multiple"].value == "true";
	var selectedIndexes = form.elements[name].value.split2(NUMBER_SET_SEPARATOR);
	
	multiSelectBuildSelect(name, selectedSelect, function(index) {
		var count = 0;
		for (var i = 0; i < selectedIndexes.length; i++) {
			if (selectedIndexes[i] == index) {
				count++;
			}
		}
		return count;
	});

	if( !allowMultiple ){
		multiSelectBuildSelect(name, notSelectedSelect, function(index) {
			for (var i = 0; i < selectedIndexes.length; i++) {
				if (selectedIndexes[i] == index) {
					return 0;
				}
			}
			return 1;
		});
	}

}

function multiSelectIdToIndex(name, id) {
	for (var i = 0; i < multiSelectOptions[name].length; i++) {
		if (multiSelectOptions[name][i].id == id) {
			return i;
		}
	}
	return -1;
}

function multiSelectUpdateButtons(form, name) {
	form.elements[name + "-add-button"].disabled
		= form.elements[name + "-not-selected"].selectedIndex == -1;
	form.elements[name + "-delete-button"].disabled
		= form.elements[name + "-selected"].selectedIndex == -1;
}

function multiSelectSelectedClick(form, name) {
	form.elements[name + "-not-selected"].selectedIndex = -1;
	multiSelectUpdateButtons(form, name);
}

function multiSelectNotSelectedClick(form, name) {
	form.elements[name + "-selected"].selectedIndex = -1;
	multiSelectUpdateButtons(form, name);
}


function multiSelectOperation(form, name, selectSuffix, operationFunction) {
	var index = multiSelectIdToIndex(name, form.elements[name + selectSuffix].value);
	operationFunction(form.elements[name], index);
	multiSelectRebuildSelects(form, name);
	multiSelectUpdateButtons(form, name);
}

function multiSelectAddOption(form, name) {
	multiSelectOperation(form, name, "-not-selected", numberSetInputAdd);
}

function multiSelectDeleteOption(form, name, id) {
	multiSelectOperation(form, name, "-selected", numberSetInputDelete);
}

/* ===== LogViews ==== */

/* List of all log views on the page, indexed by the DOM element representing
 * the wrapping <div>. 
 */
var logViews = {};

function logViewCellValue(cell) {
	var link = findFirstLink(cell);
	if (link) {
		return link.firstChild.nodeValue;
	} else {
		return cell.firstChild.nodeValue;
	}
}

function logViewSortTable(logViewElement, columnIndex, desc) {
	function updateHeaderCellsClass(logViewElement, columnIndex, desc) {
		var table = logViewElement.getElementsByTagName("TABLE")[0];
		var headerRow = table.tHead.rows[0];
		for (var i = 0; i < headerRow.cells.length; i++) {
			cell = headerRow.cells[i];
			if (i == columnIndex) {
				if (desc) {
					removeClass(cell, "sorted-asc");
					addClass(cell, "sorted-desc");
				} else {
					removeClass(cell, "sorted-desc");
					addClass(cell, "sorted-asc");
				}
			} else {
				removeClass(cell, "sorted-asc");
				removeClass(cell, "sorted-desc");
			}
		}
	}
	
	function quickSort(table, comparator, columnIndex, left, right) {
		var l = left;
		var r = right;
		var pivot = logViewCellValue(table.rows[Math.floor((left + right) / 2)].cells[columnIndex]);
		
		do {
			while (comparator(logViewCellValue(table.rows[l].cells[columnIndex]), pivot) < 0) { l++; }
			while (comparator(logViewCellValue(table.rows[r].cells[columnIndex]), pivot) > 0) { r--; }
			if (l <= r) {
				swapNodes(table.rows[l], table.rows[r]);
				l++;
				r--;
			}
		} while (l <= r);
		
		if (left < r)  { quickSort(table, comparator, columnIndex, left, r); }
		if (l < right) { quickSort(table, comparator, columnIndex, l, right); }
	}

	function sortCells(logViewElement, columnIndex, desc) {
		var table = logViewElement.getElementsByTagName("TABLE")[0];
		var comparator = desc
			? logViewReverseComparator(logViews[logViewElement].columns[columnIndex].comparator)
			: logViews[logViewElement].columns[columnIndex].comparator;
			
		quickSort(table, comparator, columnIndex, 1, table.rows.length - 1);
	}
	
	updateHeaderCellsClass(logViewElement, columnIndex, desc);
	sortCells(logViewElement, columnIndex, desc);
}

function logViewCreate(logViewElement, columns) {
	var table = logViewElement.getElementsByTagName("TABLE")[0];
	var headerRow = table.tHead.rows[0];
	for (var i = 0; i < headerRow.cells.length; i++) {
		headerRow.cells[i].onclick = function() {
			if (hasClass(this, "sorted-asc")) {
				logViewSortTable(logViewElement, this.cellIndex, true);
			} else {
				logViewSortTable(logViewElement, this.cellIndex, false);
			}
		}
	}

	logViews[logViewElement] = {
		columns: columns
	};
}

function logViewStringComparator(a, b) {
	if (a < b) {
		return -1;
	} else if (a > b) {
		return 1
	} else {
		return 0;
	}
}

function logViewStringCaseInsensitiveComparator(a, b) {
	return logViewStringComparator(a.toLowerCase(), b.toLowerCase());
}

/* Dates in the table are stored in a format which allows sorting them as
 * strings. 
 */
logViewDateComparator = logViewStringComparator;

function logViewLogLevelComparator(a, b) {
	/* This list needs to be in sync with cz.cuni.mff.been.logging.LogLevel. */
	var levels = ["OFF", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "ALL"];
	
	return levels.indexOf(a) - levels.indexOf(b);
}

function logViewReverseComparator(comparator) {
	return function(a, b) {
		return -comparator(a, b);
	}
}

/* ===== Calendar ===== */

function calendarDayOfWeekChange(input) {
	input.checked = true;
}

function calendarCellClick(sender, input, number) {
	if (hasClass(sender, "selected")) {
		removeClass(sender, "selected");
		numberSetInputDelete(input, number);
	} else {
		addClass(sender, "selected");
		numberSetInputAdd(input, number);
	}

	document.getElementById(input.name + "-checkbox").checked = true;
}

function calendarSelectLinkClick(input, selectFlag) {
	for (var i = 1; i <= 31; i++) {
		var calendarCell = document.getElementById(input.name + "-calendar-cell-" + i);
		if (selectFlag) {
			var classFunction = addClass;
			var numberSetFunction = numberSetInputAdd;
		} else {
			var classFunction = removeClass;
			var numberSetFunction = numberSetInputDelete;
		}
		if (hasClass(calendarCell, "selected") != selectFlag) {
			classFunction(calendarCell, "selected");
			numberSetFunction(input, i);
		}
	}

	document.getElementById(input.name + "-checkbox").checked = true;
}

function calendarSelectAllLinkClick(input) {
	calendarSelectLinkClick(input, true);
}

function calendarSelectNoneLinkClick(input) {
	calendarSelectLinkClick(input, false);
}

/* ===== XMLHttpRequest ===== */

function getUrl(url) {
	/* Create the request object. */
	var request = null;
	if (window.XMLHttpRequest) { // Mozilla, Safari,...
    	request = new XMLHttpRequest();
	} else if (window.ActiveXObject) { // IE
		try {
			request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}
	if (!request) return null;
	
	/* Initiates and sends the request, waits for response and returns response
	 * text.
	 */
	request.open("GET", url, false);
	request.send(null);
    if (request.readyState != 4 && request.status != 200) return null;
    return request.responseText;
}

/* ===== className functions ===== */

/* Following three functions were written by Dean Edwards.
 *
 * See <http://dean.edwards.name/IE7/caveats/>.
 */
 
function addClass(element, className) {
	if (!(hasClass(element, className))) {
		if (element.className) {
			element.className += " " + className;
		} else {
			element.className = className;
		}
	}
}

function removeClass(element, className) {
	var regexp = new RegExp("(^|\\s)" + className + "(\\s|$)");
	element.className = element.className.replace(regexp, "$2");
}

function hasClass(element, className) {
	var regexp = new RegExp("(^|\\s)" + className + "(\\s|$)");
	return regexp.test(element.className);
}

function getElementsByClassName(tagName, className){
    var elements = document.getElementsByTagName(tagName);
    result = [];
    for (var i = 0; i < elements.length; i++) {
    	if (hasClass(elements[i], className)) {
    		result.push(elements[i]);
    	}
    }
    return result;
}

/* ===== Packages ===== */

function attributeWidgetName(conditionId) { return "attribute[" + conditionId + "]"; }
function operatorWidgetName(conditionId)  { return "operator["  + conditionId + "]"; }
function valueWidgetName(conditionId)     { return "value["     + conditionId + "]"; }

function conditionRowId(conditionId)      { return "condition-row" + conditionId; }

function buildSelectElement(name, items) {
	var result = document.createElement("select");
	result.name = name;
	for (var i = 0; i < items.length; i++) {
		var option = document.createElement("option");
		option.value = items[i].value;
		option.appendChild(document.createTextNode(items[i].name));
		result.appendChild(option);
	}
	return result;
}

function buildAttributeWidget(conditionId) {
	var items = [];
	for (var i in attributeInfo) {
		items.push({
			name: attributeInfo[i].humanName,
			value: i
		});
	}
	return buildSelectElement(attributeWidgetName(conditionId), items);
}

function selectedAttributeKlass(conditionId) {
	var attributeWidget = getFormElement(0, attributeWidgetName(conditionId));
	return attributeWidget.value
		? attributeInfo[attributeWidget.value].klass
		: null;
	return null;
}

function buildOperatorWidget(conditionId) {
	var klass = selectedAttributeKlass(conditionId);
	return buildSelectElement(
		operatorWidgetName(conditionId),
		klass
			? klassToOperators[klass].map(function(operator) {
				return {
					name: operator.title,
					value: operator.name
				};
			})
			: []
	);
}

var valueWidgetBuilders = {};

valueWidgetBuilders["java.lang.String"] = function(conditionId) {
	var result = document.createElement("input");
	result.type = "text";
	result.className = "type-text";
	result.name = valueWidgetName(conditionId);
	return result;
};

valueWidgetBuilders["java.util.Date"] = function(conditionId) {
	var result = document.createElement("input");
	result.type = "text";
	result.className = "type-text";
	result.name = valueWidgetName(conditionId);
	return result;
};

valueWidgetBuilders["java.util.ArrayList"] = function(conditionId) {
	var result = document.createElement("input");
	result.type = "text";
	result.className = "type-text";
	result.name = valueWidgetName(conditionId);
	return result;
};

valueWidgetBuilders["cz.cuni.mff.been.common.Version"] = function(conditionId) {
	var result = document.createElement("input");
	result.type = "text";
	result.className = "type-text";
	result.name = valueWidgetName(conditionId);
	return result;
};

valueWidgetBuilders["cz.cuni.mff.been.softwarerepository.PackageType"] = function(conditionId) {
	return buildSelectElement(
		valueWidgetName(conditionId),
		[
			{ name: "source", value: "source" },
			{ name: "binary", value: "binary" },
			{ name: "task",   value: "task"   },
			{ name: "data",   value: "data"   }
		]
	);
};

function buildValueWidget(conditionId) {
	var klass = selectedAttributeKlass(conditionId);
	return klass
		? valueWidgetBuilders[klass](conditionId)
		: null;
}

var nextConditionId;

var CONDITION_TABLE_PERMANENT_ROW_COUNT = 1;

var CONDITION_TABLE_ATTRIBUTE_CELL_INDEX = 0;
var CONDITION_TABLE_OPERATOR_CELL_INDEX  = 1;
var CONDITION_TABLE_VALUE_CELL_INDEX     = 2;
var CONDITION_TABLE_LINK_CELL_INDEX      = 3;

function packagesListAddConditionLinkClick() {
	var noConditionsRow = document.getElementById("no-conditions-row");
	var addConditionRow = document.getElementById("add-condition-row");
	var conditionTable = addConditionRow.parentNode;
	var conditionIds = document.forms[0].elements["condition-ids"];
	
	if (noConditionsRow) {
		conditionTable.deleteRow(noConditionsRow.rowIndex);
	}
	
	var conditionId = nextConditionId;

	var newRow = conditionTable.insertRow(
		conditionTable.rows.length - CONDITION_TABLE_PERMANENT_ROW_COUNT
	);
	newRow.id = conditionRowId(conditionId);
	
	var cell1 = newRow.insertCell(CONDITION_TABLE_ATTRIBUTE_CELL_INDEX);
	var attributeWidget = buildAttributeWidget(conditionId);
	attributeWidget.onchange = function() {
		packagesListAttributeWidgetChange(conditionId)
	};
	cell1.appendChild(attributeWidget);
	
	var cell2 = newRow.insertCell(CONDITION_TABLE_OPERATOR_CELL_INDEX);
	cell2.appendChild(buildOperatorWidget(conditionId));
	
	var cell3 = newRow.insertCell(CONDITION_TABLE_VALUE_CELL_INDEX);
	cell3.appendChild(buildValueWidget(conditionId, null));

	var cell4 = newRow.insertCell(CONDITION_TABLE_LINK_CELL_INDEX);
	var deleteConditionLink = document.createElement("a");
	deleteConditionLink.href = "javascript:packagesListDeleteConditionLinkClick("
		+ conditionId
		+ ");";
	deleteConditionLink.appendChild(document.createTextNode("Delete condition"));
	cell4.appendChild(deleteConditionLink);

	numberSetInputAdd(conditionIds, conditionId);
	nextConditionId++;
}

function packagesListDeleteConditionLinkClick(conditionId) {
	var rowToDelete = document.getElementById(conditionRowId(conditionId));
	var conditionTable = rowToDelete.parentNode;
	var conditionIds = document.forms[0].elements["condition-ids"];

	conditionTable.deleteRow(rowToDelete.rowIndex);
	
	if (conditionTable.rows.length == CONDITION_TABLE_PERMANENT_ROW_COUNT) {
		var noConditionsRow = conditionTable.insertRow(0);
		noConditionsRow.id = "no-conditions-row";

		var cell = noConditionsRow.insertCell(0);
		cell.colSpan = 4;
		cell.className = "center";
		cell.appendChild(document.createTextNode("(no conditions specified)"));
	}

	numberSetInputDelete(conditionIds, conditionId);
}

function packagesListAttributeWidgetChange(conditionId) {
	var operatorWidget = getFormElement(0, operatorWidgetName(conditionId));
	var valueWidget = getFormElement(0, valueWidgetName(conditionId));
	
	operatorWidget.parentNode.removeChild(operatorWidget);
	document.getElementById(conditionRowId(conditionId))
		.cells[CONDITION_TABLE_OPERATOR_CELL_INDEX]
		.appendChild(buildOperatorWidget(conditionId));

	valueWidget.parentNode.removeChild(valueWidget);
	document.getElementById(conditionRowId(conditionId))
		.cells[CONDITION_TABLE_VALUE_CELL_INDEX]
		.appendChild(buildValueWidget(conditionId));
}

/* ===== Benchmarks ===== */

function benchmarksExperimentDetailsEntityTreeItemClick(id) {
	var expanderImage = document.getElementById("entity-tree-item-expander-" + id);
	var bodyDiv = document.getElementById("entity-tree-item-body-" + id);
	if (bodyDiv.style.display == "block") {
		bodyDiv.style.display = "none";
		expanderImage.src = "../../img/plus.gif";
		writeCookie("entity-tree-item-" + id, "closed", 365);
	} else {
		bodyDiv.style.display = "block";
		expanderImage.src = "../../img/minus.gif";
		writeCookie("entity-tree-item-" + id, "open", 365);
	}
	return false;
}

function benchmarksRunSelectPluginPluginListChange() {
	var pluginList = document.getElementById("plugin-list");
	var pluginDescription = document.getElementById("plugin-description");

	pluginDescription.innerHTML = pluginDescriptions[pluginList.value];
}

/* ===== Hosts ===== */

function userPropertiesFormNewTypeChange() {
	var newTypeSelect = document.forms[2].elements["new-type"];
	var newTextValueContainer = document.getElementById("new-text-value-container");
	var newBooleanValueContainer = document.getElementById("new-boolean-value-container");
	if (newTypeSelect.value == "boolean") {
		newTextValueContainer.style.display = "none";
		newBooleanValueContainer.style.display = "inline";
	} else {
		newTextValueContainer.style.display = "inline";
		newBooleanValueContainer.style.display = "none";
	}
}

function hostsUpdateOperationStatus(handle, successURL) {
	var statusMessage = document.getElementById("status-message");
	var statusImage = document.getElementById("status-image");
	var statusJS = getUrl("../operation-status/?handle=" + handle);
	if (statusJS) {
		statusObject = eval("(" + statusJS + ")");

		statusMessage.innerHTML = statusObject.message;
		if (statusObject.value == "success") {
			location.href = successURL;
		}
		if (statusObject.value == "failed") {
			statusMessage.className = "error-message";
			statusImage.src = "../../img/loading-failed.gif";
		}
	} else {
		statusMessage.innerHTML = "Error requesting status information.";
		statusMessage.className = "error-message";
		statusImage.src = "../../img/loading-failed.gif";
	}
}

function hostsHostAddingLoad(handle) {
	function updateStatus() {
		hostsUpdateOperationStatus(handle, "../host-list/?action=added&handle=" + handle);
	}
	
	updateStatus();
	window.setInterval(updateStatus, 1000);
}

function hostsHostRefreshingLoad(hostname, handle) {
	function updateStatus() {
		hostsUpdateOperationStatus(handle, "../host-details/?hostname="
			+ hostname + "&action=refreshed&handle=" + handle);
	}
	
	updateStatus();
	window.setInterval(updateStatus, 1000);
}

function groupAddEditRSLExpanderClick() {
	document.getElementById("rsl-expander-row").style.display = "none";
	document.getElementById("rsl-textarea-row").style.display
		= (browserIsIE() ? "block" : "table-row");
	document.getElementById("rsl-button-row").style.display
		= (browserIsIE() ? "block" : "table-row");
	document.forms[0].elements["rsl"].focus();
}

function groupAddEditSelectMatchingHostsClick() {
	function showMessage(id, messageHTML) {
		document.getElementById(id + "-row").style.display
			= (browserIsIE() ? "block" : "table-row");
		/* This is really ugly, but there probably isn't a better way.  Oh how I
		 * wish XBL working everywhere now...
		 */
		document.getElementById(id)
			.firstChild
			.firstChild
			.firstChild
			.firstChild
			.firstChild
			.innerHTML = messageHTML;
	}

	function hideMessage(id) {
		document.getElementById(id + "-row").style.display = "none";
	}

	function showInfoMessage(messageHTML)  { showMessage("rsl-info-message", messageHTML); }
	function hideInfoMessage()             { hideMessage("rsl-info-message"); }
	function showErrorMessage(messageHTML) { showMessage("rsl-error-message", messageHTML); }
	function hideErrorMessage()            { hideMessage("rsl-error-message"); }
	
	var responseJS = getUrl("../host-javascript-list-rsl/?rsl="
		+ encodeURIComponent(document.forms[0].elements["rsl"].value));
	if (responseJS) {
		var response = eval("(" + responseJS + ")");
		if (response.validationResult == null) {
			with (document.forms[0]) {
				for (var i = 0; i < elements.length; i++) {
					if (elements[i].type == "checkbox") {
						elements[i].checked = false;
					}
				}
				for (i = 0; i < arrayLength(response.hosts); i++) {
					getFormElement(0, "group-hosts[" + response.hosts[i] + "]").checked = true;
				}
			}
			hideErrorMessage();
			showInfoMessage("Matching hosts selected.");
		} else {
			hideInfoMessage();
			showErrorMessage(response.validationResult);
		}
	} else {
		hideInfoMessage();
		showErrorMessage("Error getting the list of matching hosts.");
	}
}

/* ===== Results ===== */

function resultsListCheckboxClick(checkbox, formId, idsInputName, id) {
	/* Change the contents of the hidden field containing the ids of selected
	 * entities.
	 */
	var idsInput = document.getElementById(formId).elements[idsInputName];
	if (checkbox.checked) {
		numberSetInputAdd(idsInput, id);
	} else {
		numberSetInputDelete(idsInput, id);
	}
	
	/* Enable/disable "Export" button and show/hide explanation. */
	document.getElementById(formId).elements["export"].disabled
		= idsInput.value == "";
	document.getElementById("export-note-row").style.display
		= idsInput.value == ""
			? (browserIsIE() ? "block" : "table-row")
			: "none";
}

function resultsListFilterLinkClick(link, classToSearch, classToSet, linkHTML, linkClickFunction) {
	var elements = getElementsByClassName("tr", classToSearch);
	for (var i = 0; i < elements.length; i++) {
		removeClass(elements[i], classToSearch);
		addClass(elements[i], classToSet);
	}
	
	link.innerHTML = linkHTML;
	link.onclick = function() {
		eval(linkClickFunction).call(null, link);
		return false;
	}
}

function resultsAnalysisListAnalysisCheckboxClick(sender, aid) {
	resultsListCheckboxClick(sender, "analysis-list-analysis-export-form", "aids", aid);
}

function resultsAnalysisDetailsExperimentCheckboxClick(sender, eid) {
	resultsListCheckboxClick(sender, "analysis-details-experiment-export-form", "eids", eid);
}

function resultsAnalysisDetailsShowIncompleteExperimentsLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"incomplete-invisible",
		"incomplete-visible",
		"&laquo; Hide incomplete experiments",
		"resultsAnalysisDetailsHideIncompleteExperimentsLinkClick"
	);
}

function resultsAnalysisDetailsHideIncompleteExperimentsLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"incomplete-visible",
		"incomplete-invisible",
		"Show incomplete experiments &raquo;",
		"resultsAnalysisDetailsShowIncompleteExperimentsLinkClick"
	);
}

function resultsExperimentDetailsBinaryCheckboxClick(sender, bid) {
	resultsListCheckboxClick(sender, "experiment-details-binary-export-form", "bids", bid);
}

function resultsExperimentDetailsShowIncompleteBinariesLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"incomplete-invisible",
		"incomplete-visible",
		"&laquo; Hide incomplete binaries",
		"resultsExperimentDetailsHideIncompleteBinariesLinkClick"
	);
}

function resultsExperimentDetailsHideIncompleteBinariesLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"incomplete-visible",
		"incomplete-invisible",
		"Show incomplete binaries &raquo;",
		"resultsExperimentDetailsShowIncompleteBinariesLinkClick"
	);
}

function resultsBinaryDetailsRunCheckboxClick(sender, rid) {
	resultsListCheckboxClick(sender, "binary-details-run-export-form", "rids", rid);
}

function resultsBinaryDetailsShowInvalidRunsLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"invalid-invisible",
		"invalid-visible",
		"&laquo; Hide invalid runs",
		"resultsBinaryDetailsHideInvalidRunsLinkClick"
	);
}

function resultsBinaryDetailsHideInvalidRunsLinkClick(sender) {
	resultsListFilterLinkClick(
		sender,
		"invalid-visible",
		"invalid-invisible",
		"Show invalid runs &raquo;",
		"resultsBinaryDetailsShowInvalidRunsLinkClick"
	);
}

/* ===== Services ===== */

function servicesListHostChange(sender, formId) {
	document.forms[formId].elements["host"].value = sender.value;
}

/* ===== TaskTree ===== */

function treeExpand(path, url){
	AJAXUpdate(path+"-children", url);
	var element = document.getElementById(path+"-children");
	if( element != null ){
		element.style.display = "block";
	}
	element = document.getElementById(path+"-plus");
	if( element != null ){
		element.style.display = "none";
	}
	element = document.getElementById(path+"-minus");
	if( element != null ){
		element.style.display = "inline";
	}
}

function treeCollapse(path){
	var element = document.getElementById(path+"-children");
	if( element != null ){
		element.style.display = "none";
	}
	element = document.getElementById(path+"-plus");
	if( element != null ){
		element.style.display = "inline";
	}
	element = document.getElementById(path+"-minus");
	if( element != null ){
		element.style.display = "none";
	}
}

/* ===== AJAX support ===== */

/* inserts result from url as object inner html */
function AJAXUpdate(elementId, url){
	var xmlhttp;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
		alert("Your browser does not support XMLHTTP!");
		return;
	}
	// show the "loading" image
	document.getElementById(elementId).innerHTML =
		"<img src=\"../../img/loading.gif\">";

	xmlhttp.onreadystatechange = function(){
		if( xmlhttp.readyState == 4 ){
			document.getElementById(elementId).innerHTML = xmlhttp.responseText;
		}
	}
	try {
		xmlhttp.open("GET",url,true);
		xmlhttp.send(null);
	} catch( e ){
		alert( e.name+": "+e.message );
		document.getElementById(elementId).innerHTML =
			"<img src=\"img/loading-failed.gif\">";
	}
}
