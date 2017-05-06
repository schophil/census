import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import census from '../census';

/**
 * Concrete implementation of the service using AXIOS.js to make the HTTP calls.
 */
export function DashboardService() {

	/**
	 * Lists the statistics for the last X days for a specific subject.
	 */
	this.list = function (subject, days, filter) {
    var params = {
        period: "-" + days
    };

    if (filter) {
      params.category = filter;
    }

		return axios.get('/rest/subjects/' + subject.id + '/stats/days', {
      params: params,
			transformResponse: [
				function (data) {
					console.log('DashboardService.list transform > ', data);
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
		var url = '/rest/subjects/' + subject.id + '/stats/days/' + dateAsString;

		return axios.get(url);
	};

  this.getCategories = function () {
    var url = '/rest/categories';
    return axios.get(url);
  };
}

/**
 * A mocked version of the service for testing and developing.
 */
export function MockDashboardService() {

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
					totalRequests: _.random(100, 5000),
					hour: i
				};
				g.activityPerHour.push(hour);
			});
		}

		target.popularResources = [];
		for (var j = 0; j < 10; j++) {
			target.popularResources.push({
				path: '/fictive/resource/' + _.random(100, 200),
				hits: _.random(5, 100)
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
			day.totalRequests = _.random(30000, 50000);
			day.totalRequestsInError = _.random(500, 1000);
			day.averageResponseTime = _.random(0, 10, true);
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

	this.getCategories = function () {
		var data = ['a', 'b'];
		var p = new Promise(function (resolve, reject) {
			window.setTimeout(function () {
				resolve({ data: data });
			}, census.mockDelay);
		});
		return p;
  };
}

export default DashboardService;
