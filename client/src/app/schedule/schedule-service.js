import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import census from '../census';

export function ScheduleService() {

}

export function MockScheduleService() {

  this.getScheduled = function () {
    var data = [];
    data.push({
      status: 'started',
      scheduledOn: moment(),
      startedOn: moment(),
      target: moment()
    });
    data.push({
      status: 'scheduled',
      scheduledOn: moment(),
      startedOn: null,
      target: moment()
    })

    var p = new Promise(function (resolve, reject) {
      _.delay(function () {
        resolve({ data: data} );
      }, census.mockDelay);
    });
    return p;
  };

}
