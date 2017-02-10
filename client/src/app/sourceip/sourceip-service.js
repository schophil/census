// vim: set sw=2 ts=2:
'use strict';

var census = census || {};

census.SourceipService = function (census, moment) {

	/**
	 * Concrete implementation of the service using AXIOS.js to make the HTTP calls.
	 */
	function SourceipService() {

		this.search = function (filter, days) {
			return axios.get('/api/sources/', {
				params: {
					query: filter,
					days: days
				},
				transformResponse: [
					function (data) {
						console.log('DashboardService.list transform > ', data);
						data = JSON.parse(data);
						data.forEach(g => {
							g.ips.forEach(ip => { 
								ip.lastUsed = moment(ip.lastUsed, census.dateApiFormat);
							});
						});
						return data;
					}
				]
			});

		}
	}

	/**
	 * A mocked version of the service for testing and developing.
	 */
	function MockSourceipService() {

		this.search = function (filter, days) {
			var data = [];
			for (let i = 0; i < 4; i++) {
				console.log("Create user ", i);
				var el = {
					userid: "PU" + _.random(111111, 888888),
					ips: []
				};
				var max = _.random(1, 3);
				for (let j = 0; j < max; j++) {
					console.log("Create ip ", i, j);
					el.ips.push({
						ip: "IP" + _.random(111111, 888888),
						lastUsed: moment().subtract(_.random(1, 15), 'days')
					});
				}
				data.push(el);
			}

			var p = new Promise(function (resolve, reject) {
				window.setTimeout(function () {
					resolve({ data: data });
				}, census.mockDelay);
			});

			return p;
		};
	}

	return census.mock ? new MockSourceipService() : new SourceipService();

}(census, moment);

