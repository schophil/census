import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import census from '../census';

export function ScheduleService() {

  this.getScheduled = function () {
    return axios.get('/rest/retrieval/scheduled', {
			transformResponse: [
				function (data) {
					data = JSON.parse(data);
					data.forEach(function (g) {
						g.scheduledOn = moment(g.scheduledOn, census.dateApiFormat);
            g.startedOn = moment(g.startedOn, census.dateApiFormat);
					});
					return data;
				}
			]
		});
  };
}

export function MockScheduleService() {

  this.getScheduled = function () {
    var data = [];
    data.push({
      status: 'started',
      scheduledOn: moment(),
      startedOn: moment(),
      target: moment(),
      subject: 'subject'
    });
    data.push({
      status: 'scheduled',
      scheduledOn: moment(),
      startedOn: null,
      target: moment(),
      subject: 'subject'
    })

    var p = new Promise(function (resolve, reject) {
      _.delay(function () {
        resolve({ data: data} );
      }, census.mockDelay);
    });
    return p;
  };

  this.schedule = function (date, subject) {
    var p = new Promise(function(resolve, reject) {
      _.delay(function () {
        resolve();
      })
    });
    return p;
  };
}
