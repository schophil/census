import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import census from '../census';

/**
* Concrete implementation of the service using AXIOS.js to make the HTTP calls.
*/
export function SourceipService() {

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
          data.forEach(function (g) {
            g.ips.forEach(function (ip) {
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
export function MockSourceipService() {

  this.search = function (filter, days) {
    var data = [];
    for (var i = 0; i < 4; i++) {
      console.log("Create user ", i);
      var el = {
        userid: "PU" + _.random(111111, 888888),
        ips: []
      };
      var max = _.random(1, 3);
      for (var j = 0; j < max; j++) {
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

export default SourceipService;
