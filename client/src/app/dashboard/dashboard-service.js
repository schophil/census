// vim: set sw=2 ts=2:

var census = census || {};

census.DashboardService = function (census, moment) {

	/**
	 * Concrete implementation of the service using AXIOS.js to make the HTTP calls.
	 */
	function DashboardService() {
		this.list = function (subject, days) {
			return axios.get('/api/' + subject.id + '/stats/list/' + days, {
				transformResponse: [
					function (data) {
						console.log('DashboardService.list transform > ', data);
						data = JSON.parse(data);
						data.forEach(g => {
							g.date = moment(g.date, 'YYYY-MM-DD');
						});
						return data;
					}
				]
			});
		};

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
			var dateAsString = date.format('YYYY-MM-DD');
			var url = '/api/' + subject.id + '/stats/details/' + dateAsString;

			return axios.get(url);
		}
	}

	/**
	 * A mocked version of the service for testing and developing.
	 */
	function MockDashboardService() {

		this.dayDetails = function (subject, date) {
			var yesterday = {
				activityPerHour: []
			};
			
			var target = {
				activityPerHour: []
			};

			var tomorrow = {
				activityPerHour: []
			};

			var group = [yesterday, target, tomorrow];
			// create fictional data
			for (var i = 0; i < 24; i++) {
				group.forEach(g => {
					var hour = {
						totalRequests: this._invent(100, 5000),
						hour: i
					};
					g.activityPerHour.push(hour);
				});
			}

			var p = new Promise(function (resolve, reject) {
				window.setTimeout(function () {
					resolve({ data: group} );
				}, census.mockDelay);
			});
			return p;
		};

		this.list = function (subject, days) {
			var data = [];
			var total = 0;

			// create fictional data
			for (var i = days; i > 0; i--) {
				var day = {
					date: moment().subtract(i, 'days')
				};
				// invent data
				day.totalRequests = this._invent(30000, 50000);
				day.totalRequestsInError = this._invent(500, 1000);
				day.averageResponseTime = this._invent(0, 10);
				day.maxResponseTime = 5;
				day.minResponseTime = 0.01;
				day.totalUserIds = 45;
				data.push(day);

				total += day.totalRequests;
			}

			// update the shares - this number describes how much percent each day represents 
			// over the total period (in total number of requests)
			data.forEach(function (d) {
				d.share = d.totalRequests * 100 / total;
			});

			var p = new Promise(function (resolve, reject) {
				window.setTimeout(function () {
					resolve({ data: data });
				}, census.mockDelay);
			});
			return p;
		};

		this._invent = function (min, max) {
			return Math.floor(Math.random() * (max - min)) + min;
		};
	}

	return census.mock ? new MockDashboardService() : new DashboardService();

}(census, moment);
