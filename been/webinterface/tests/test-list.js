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

var tests = {
	main: {
		name: "Main",
		tests: [
			"home"
		]
	},
	
	tasks: {
		name: "Tasks",
		tests:  [
			"software-repository-not-running",
			"software-repository-start",

			"context-list-basic",
			"context-details-basic",
			"context-details-action-invalid",
			"context-details-cid-missing",
			"context-details-cid-invalid",
			"context-kill-cid-missing",
			"context-kill-cid-invalid",
			"context-kill-ok",
			
			"benchmark-manager-not-running",
			"benchmark-manager-start",
			
			"task-details-basic",
			"task-details-cid-missing",
			"task-details-cid-invalid",
			"task-details-tid-missing",
			"task-details-tid-invalid",
			"task-kill-cid-missing",
			"task-kill-cid-invalid",
			"task-kill-tid-missing",
			"task-kill-tid-invalid",
			"task-kill-ok",
			"task-logs-basic",
			"task-manager-logs-basic",

			"software-repository-not-running",
			"software-repository-start",
			"host-manager-not-running",
			"host-manager-start",
			"task-run-basic",
			"task-run-gui-no-task-name",
			"task-run-gui-no-host",
			"task-run-gui-no-context-id",
			"task-run-gui-invalid-properties",
			"task-run-gui-ok",
			"task-run-xml-descriptor-not-xml",
			"task-run-xml-descriptor-nonsense-xml",
			"task-run-xml-ok",
			"software-repository-stop",
			"host-manager-stop"
		]
	},

	packages: {
		name: "Packages",
		tests: [
			"software-repository-not-running",
			"software-repository-start",

			"upload-basic",
			"upload-action-invalid",
			"upload-no-file",
			"details-no-package",
			"details-invalid-package",
			"details-source",
			"details-binary",
			"details-task",
			"details-data",
			"list-basic",
			"list-invalid-action",
			"list-missing-attribute",
			"list-invalid-attribute",
			"list-missing-operator",
			"list-invalid-operator",
			"list-missing-value",
			"list-invalid-value",
			"list-filter-basic",
			"list-filter-package-name",
			"list-filter-version",
			"list-filter-hardware-platforms",
			"list-filter-software-platforms",
			"list-filter-package-type",
			"list-filter-human-name",
			"list-filter-download-url",
			"list-filter-download-date",
			"list-filter-download-date-invalid",
			"list-filter-source-package-filename",
			"list-filter-binary-identifier",
			"list-filter-build-configuration",
			"list-filter-multiple-conditions",
			"list-filter-delete-condition",

			"software-repository-stop"
		]
	},

	benchmarks: {
		name: "Benchmarks",
		tests: [
			"benchmark-manager-not-running",
			"benchmark-manager-start",

			"plugin-list-basic",
			"plugin-install-basic",
			"plugin-install-action-invalid",
			"plugin-install-no-file",
			"rsl-help-type-missing",
			"rsl-help-type-invalid",
			"rsl-help-host-basic",

			"benchmark-manager-stop"
		]
	},

	hosts: {
		name: "Hosts",
		tests: [
			"host-manager-not-running",
			"host-manager-start",

			"operation-status-handle-missing",
			"operation-status-handle-invalid",
			"host-list-basic",
			"host-list-action-invalid",
			/*
			"host-add-cancel",
			"host-add-hostname-empty",
			"host-add-hostname-invalid",
			"host-add-localhost",
			"host-add-hostname-duplicate",
			"host-adding-hostname-missing",
			"host-adding-handle-missing",
			"host-adding-handle-invalid",
			*/
			"host-details-hostname-missing",
			"host-details-hostname-invalid",
			"host-details-basic-windows",
			"host-details-basic-linux",
			"host-details-history",
			"host-refresh-hostname-missing",
			"host-refresh-hostname-invalid",
			"host-refreshing-hostname-missing",
			"host-refreshing-handle-missing",
			"host-refreshing-handle-invalid",
			"host-delete-hostname-missing",
			"host-delete-hostname-invalid",
			"host-delete-ok",
			"group-list-basic",
			"group-list-action-invalid",
			"group-add-cancel",
			"group-add-name-empty",
			"group-add-name-duplicate",
			"group-add-rsl",
			"group-add-ok",
			"group-edit-cancel",
			"group-edit-name-empty",
			"group-edit-name-duplicate",
			"group-edit-rsl",
			"group-edit-ok",
			"group-edit-all-hosts-cancel",
			"group-edit-all-hosts-basic",
			"group-edit-all-hosts-ok",
			"group-edit-group-missing",
			"group-edit-group-invalid",
			"group-delete-ok",
			"group-delete-group-missing",
			"group-delete-group-invalid",
			"alias-list-action-invalid",
			"alias-list-basic",
			"alias-add-cancel",
			"alias-add-empty-alias-name",
			"alias-add-empty-result-name",
			"alias-add-empty-app-restriction",
			"alias-add-invalid-result-name",
			"alias-add-invalid-result-vendor",
			"alias-add-invalid-result-version",
			"alias-add-invalid-os-restriction",
			"alias-add-invalid-app-restriction",
			"alias-add-ok",
			"alias-add-alias-name-duplicate",
			"alias-edit-cancel",
			"alias-edit-empty-alias-name",
			"alias-edit-empty-result-name",
			"alias-edit-empty-app-restriction",
			"alias-edit-invalid-result-name",
			"alias-edit-invalid-result-vendor",
			"alias-edit-invalid-result-version",
			"alias-edit-invalid-os-restriction",
			"alias-edit-invalid-app-restriction",
			"alias-edit-alias-name-duplicate",
			"alias-edit-ok",
			"alias-edit-alias-missing",
			"alias-edit-alias-invalid",
			"alias-delete-alias-missing",
			"alias-delete-alias-invalid",
			"alias-delete-ok",
			"rsl-help-type-missing",
			"rsl-help-type-invalid",
			"rsl-help-os-basic",
			"rsl-help-app-basic",
						
			"host-manager-stop"
		]
	},

	results: {
		name: "Results",
		tests:  [
			"results-repository-not-running",
			"results-repository-start",
			"benchmark-manager-start",

			"analysis-list-basic",
			"analysis-list-action-invalid",
			"analysis-details-basic",
			"analysis-details-change-comment",
			"analysis-details-action-invalid",
			"analysis-details-aid-missing",
			"analysis-details-aid-invalid",
			"analysis-results-graph-aid-missing",
			"analysis-results-graph-aid-invalid",
			"analysis-results-graph-index-missing",
			"analysis-results-graph-index-invalid",
			"analysis-export-aids-missing",
			"analysis-export-aids-invalid",
			"analysis-export-format-missing",
			"analysis-export-format-invalid",
			"analysis-delete-basic",
			"analysis-delete-aid-missing",
			"analysis-delete-aid-invalid",
			"experiment-details-basic",
			"experiment-details-change-comment",
			"experiment-details-action-invalid",
			"experiment-details-aid-missing",
			"experiment-details-aid-invalid",
			"experiment-details-eid-missing",
			"experiment-details-eid-invalid",
			"experiment-results-graph-aid-missing",
			"experiment-results-graph-aid-invalid",
			"experiment-results-graph-eid-missing",
			"experiment-results-graph-eid-invalid",
			"experiment-results-graph-index-missing",
			"experiment-results-graph-index-invalid",
			"experiment-export-aid-missing",
			"experiment-export-aid-invalid",
			"experiment-export-eids-missing",
			"experiment-export-eids-invalid",
			"experiment-export-format-missing",
			"experiment-export-format-invalid",
			"experiment-delete-basic",
			"experiment-delete-aid-missing",
			"experiment-delete-aid-invalid",
			"experiment-delete-eid-missing",
			"experiment-delete-eid-invalid",
			"binary-details-basic",
			"binary-details-change-comment",
			"binary-details-action-invalid",
			"binary-details-aid-missing",
			"binary-details-aid-invalid",
			"binary-details-eid-missing",
			"binary-details-eid-invalid",
			"binary-details-bid-missing",
			"binary-details-bid-invalid",
			"binary-export-aid-missing",
			"binary-export-aid-invalid",
			"binary-export-eid-missing",
			"binary-export-eid-invalid",
			"binary-export-bids-missing",
			"binary-export-bids-invalid",
			"binary-export-format-missing",
			"binary-export-format-invalid",
			"binary-delete-basic",
			"binary-delete-aid-missing",
			"binary-delete-aid-invalid",
			"binary-delete-eid-missing",
			"binary-delete-eid-invalid",
			"binary-delete-bid-missing",
			"binary-delete-bid-invalid",
			"run-details-basic",
			"run-details-action-invalid",
			"run-details-aid-missing",
			"run-details-aid-invalid",
			"run-details-eid-missing",
			"run-details-eid-invalid",
			"run-details-bid-missing",
			"run-details-bid-invalid",
			"run-details-rid-missing",
			"run-details-rid-invalid",
			"run-load-graph-aid-missing",
			"run-load-graph-aid-invalid",
			"run-load-graph-eid-missing",
			"run-load-graph-eid-invalid",
			"run-load-graph-bid-missing",
			"run-load-graph-bid-invalid",
			"run-load-graph-rid-missing",
			"run-load-graph-rid-invalid",
			"run-load-graph-index-missing",
			"run-load-graph-index-invalid",
			"run-raw-data-aid-missing",
			"run-raw-data-aid-invalid",
			"run-raw-data-eid-missing",
			"run-raw-data-eid-invalid",
			"run-raw-data-bid-missing",
			"run-raw-data-bid-invalid",
			"run-raw-data-rid-missing",
			"run-raw-data-rid-invalid",
			"run-export-aid-missing",
			"run-export-aid-invalid",
			"run-export-eid-missing",
			"run-export-eid-invalid",
			"run-export-bid-missing",
			"run-export-bid-invalid",
			"run-export-rids-missing",
			"run-export-rids-invalid",
			"run-export-format-missing",
			"run-export-format-invalid",
			"run-delete-basic",
			"run-delete-aid-missing",
			"run-delete-aid-invalid",
			"run-delete-eid-missing",
			"run-delete-eid-invalid",
			"run-delete-bid-missing",
			"run-delete-bid-invalid",
			"run-delete-rid-missing",
			"run-delete-rid-invalid",
			"package-list-basic",
			"package-list-action-invalid",
			"package-upload-basic",
			"package-upload-action-invalid",
			"package-upload-no-file",
			"package-delete-ok",
			"r-functions-help-basic",

			"results-repository-stop",
			"benchmark-manager-stop"
		]
	},

	configuration: {
		name: "Configuration",
		tests:  [
			"configuration-basic",
			"configuration-action-invalid",
			"configuration-save-task-manager-hostname-invalid",
			"configuration-save-max-package-cache-size-invalid",
			"configuration-save-kept-closed-context-count-invalid",
			"configuration-save-ok"
		]
	},

	services: {
		name: "Services",
		tests:  [
			"list-basic",
			"list-invalid-action",
			"list-start-host-empty",
			"list-start-host-invalid",
			"list-start-restart-stop-ok",
			"logs-basic",
			"logs-service-missing",
			"logs-service-invalid"
		]
	}
};
