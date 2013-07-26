var plotData = {};
var plotVar = "cpuUsage";
var plotVarOptions = {
	cpuUsage: {
		min: 0,
		max: 100,
		tickFormatter: function (n) {
			return n + " %";
		}
	},
	freeMemory: {
		min: 0,
		tickFormatter: function(n) {
			return (n / 1024. / 1024. / 1024.).format(2) + " GiB";
		}
	},
	loadAverage:{
		min: 0
	},
	netBytes: {
		min: 0,
		tickFormatter: function(n) {
			return (n / 1024. / 1024.).format(2) + " MiB/s";
		}
	},
	fsBytes: {
		min: 0,
		tickFormatter: function(n) {
			return (n / 1024. / 1024.).format(2) + " MiB/s";
		}
	},
};
function draw() {
	var values = [];
    for (var id in plotData) {
        dataForOneRuntime = plotData[id][plotVar];
		objectForOneRuntime = { label: id.substring(0, 8), data: dataForOneRuntime };
		values.push(objectForOneRuntime);
	}

	var xmax = new Date().getTime();
	var xmin = new Date().getTime() - 1 * 60 * 1000 /* 5 minutes */;
	var xformatter = function(n) {
		return Date.create(n).format("{hh}:{mm}:{ss}");
	};

	$.plot($("#plot-placeholder"), values, {
		yaxis: plotVarOptions[plotVar],
		xaxis: { min: xmin, max: xmax, tickFormatter: xformatter },
		legend: { position: "nw" }
	});
}
function addPlotPoint(id, timestamp, cpuUsage, freeMemory, loadAverage, netBytes, fsBytes) {
	if (! plotData[id]) plotData[id] = {cpuUsage: [], freeMemory: [], loadAverage: [], netBytes: [], fsBytes: []};
	plotData[id]["cpuUsage"].push([timestamp, cpuUsage]);
	plotData[id]["freeMemory"].push([timestamp, freeMemory]);
	plotData[id]["loadAverage"].push([timestamp, loadAverage]);
	plotData[id]["netBytes"].push([timestamp, netBytes]);
	plotData[id]["fsBytes"].push([timestamp, fsBytes]);
	draw();
}
function select(e, item) {
	$("#selectButtons a").removeClass("active");
	$(e).addClass("active");
	plotVar = item;
    draw();
	return false;
}

$(document).ready(function() {
	draw();
});

var LOG_LEVEL_TRACE = 1;
var LOG_LEVEL_DEBUG = 2;
var LOG_LEVEL_INFO = 3;
var LOG_LEVEL_WARN = 4;
var LOG_LEVEL_ERROR = 5;

var logs = [];
function addLog(log) {
	logs.push(log);

	var d = Date.create(log.message.time).format("{H}:{mm}:{ss}.{fff}");
	var classname = log.message.name;
	classname = classname.substr(classname.lastIndexOf(".") + 1);
	var t = "<a href='/task/detail/" + log.taskId + "'>" + log.taskId.substring(0, 8) + "</a>";


	var lev = log.message.level;
	if (log.message.level == LOG_LEVEL_TRACE) lev = "<i class='icon-caret-down'></i>";
	else if (log.message.level == LOG_LEVEL_DEBUG) lev = "<i class='icon-caret-down'></i>";
	else if (log.message.level == LOG_LEVEL_INFO) lev = "<i class='icon-info'></i>";
	else if (log.message.level == LOG_LEVEL_WARN) lev = "<i class='icon-exclamation'></i>";
	else if (log.message.level == LOG_LEVEL_ERROR) lev = "<i class='icon-bolt'></i>";
	else lev = "<i class='icon-question'></i> " + log.message.level;

	var color = "black";
	if (log.message.level == LOG_LEVEL_WARN) color = "#c66";
	else if (log.message.level == LOG_LEVEL_ERROR) color = "red";

	var line = "<span style='color: " + color + "'>[" + d + "] " + t + " " + lev + " " + classname + " - " + log.message.message + "</span>";

	$("#logsLoading").html("");
	$("#logsTable").show();
	$("#logsTable > tbody:last").append(
		$("<tr>")
			.append($("<td>").html(line))
	);

	if ($("#logautoscroll").is(":checked")) {
		var p = $("#logsTable").parent();
		p[0].scrollTop = p[0].scrollHeight;
	}
}
