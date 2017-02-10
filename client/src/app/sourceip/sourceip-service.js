// vim: set sw=2 ts=2:
'use strict';

var census = census || {};

census.SourceipService = function (census, moment) {

	/**
	 * Concrete implementation of the service using AXIOS.js to make the HTTP calls.
	 */
	function SourceipService() {

		/**
		 * Lists the statistics for the last X days for a specific subject.
		 */
		this.list = function (subject, days) {
			return axios.get('/api/stats/' + subject.id + '/list/' + days, {
				transformResponse: [
					function (data) {
						console.log('SourceipService.list transform > ', data);
						data = JSON.parse(data);
						data.forEach(g => {
							g.date = moment(g.date, census.dateApiFormat);
						});
						return data;
					}
				]
			});
		};

		/**
		 * Retrieves the detailed statistics for a specific subject on a specific date. This will 
		 * actually retrieve 3 days: the target, the date before and the date after.
		 */
		this.dayDetails = function (subject, date) {
			var yesterday = moment(date).subtract(1, 'days');
			var tomorrow = moment(date).add(1, 'days');

			var self = this;
			var p = new Promise(function (resolve, reject) {
				axios.all([ 
					self._getOneDay(subject, yesterday), 
					self._getOneDay(subject, date),
					self._getOneDay(subject, tomorrow), 
				])
					.then(axios.spread(function (y, d, t) {
						resolve({
							data: [y.data, d.data, t.data]
						});
					}));
			});

			return p;
		};

		this._getOneDay = function (subject, date) {
			var dateAsString = date.format(census.dateApiFormat);
			var url = '/api/stats/' + subject.id + '/details/' + dateAsString;

			return axios.get(url);
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

