// vim: set ts=2 sw=2:
'use strict';

var census = census || {};

census.AppService = function (census) {

	function AppService() {

		this.getApps = function () {
			return axios.get('/api/subjects');
		};
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
						}, census.mockDelay);
				}
			);
			return p;
		};
	}

	return census.mock ? new MockAppService() : new AppService();

}(census);
