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

var logs = [];
function addLog(log) {
	logs.push(log);
	$("#logsLoading").html("");
	$("#logsTable").show();
	$("#logsTable > tbody:last").append(
		$("<tr>")
			.append($("<td>").text(log.senderId.substring(0, 8)))
			.append($("<td>").text(log.name))
			.append($("<td>").text(log.level))
			.append($("<td>").text(log.message))
	);
	var p = $("#logsTable").parent();
	p[0].scrollTop = p[0].scrollHeight;
}
