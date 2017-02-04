// vim: set ts=2 sw=2:
'use strict';

var census = census || {};

census.AppService = function (census) {

	function AppService() {

	}

	function MockAppService() {

		this.getApps = function () {
			var data = [
				{ id: 'egames', name: 'eGames' },
				{ id: 'epis', name: 'EPIS' },
				{ id: 'protocol', name: 'ProtocolAPI' }
			];
			var p = new Promise(
				function (resolve, reject) {
					window.setTimeout(
						function () {
							resolve(data);
						}, 5000
					);
				}
			);
			return p;
		};
	}

	return census.mock ? new MockAppService() : new AppService();

}(census);
