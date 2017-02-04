// vim: set sw=2 ts=2:

var census = census || {};

census.DashboardService = function (mock, moment) {

	function DashboardService() {
	}

	function MockDashboardService() {
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
				day.averageResponseTime = 0.2;
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
					resolve(data);
				}, 5000);
			});
			return p;
		};

		this._invent = function (min, max) {
			return Math.floor(Math.random() * (max - min)) + min;
		};
	}

	return mock ? new MockDashboardService() : new DashboardService();

}(census.mock, moment);
