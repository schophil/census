import census from './census';
import SpinService from './spinner/spinner-service';
import formatNumber from 'format-number';
import { AppService, MockAppService } from './app-service';
import { SourceipService, MockSourceipService } from './sourceip/sourceip-service';
import { DashboardService, MockDashboardService } from './dashboard/dashboard-service';
import { ScheduleService, MockScheduleService } from './schedule/schedule-service';

// settings
census.debug = !PRODUCTION; 
census.mock = !PRODUCTION;
census.mockDelay = 1000;
census.dateFormat = 'dd D.M.YY';
census.dateTimeFormat = 'dd D.M.YY';
census.dateApiFormat = 'YYYY-MM-DD';
census.dateTimeApiFormat = "YYYY-MM-DD'T'HH:mm:ss";
// graph colors
census.graphBlue = {
  background: 'rgba(54, 162, 235, 0.5)',
  border: 'rgba(54, 162, 235, 1)'
};
census.graphRed = {
  background: 'rgba(255, 99, 132, 0.5)',
  border: 'rgba(255, 99, 132, 1)'
};
census.graphYellow = {
  background: 'rgba(255, 206, 86, 0.5)',
  border: 'rgba(255, 206, 86, 1)'
};
census.graphGreen = {
  background: 'rgba(75, 192, 192, 0.5)',
  border: 'rgba(75, 192, 192, 1)'
};
census.graphPurple = {
  background: 'rgba(153, 102, 255, 0.5)',
  border: 'rgba(153, 102, 255, 1)'
};
census.graphOrange = {
  background: 'rgba(255, 159, 64, 0.5)',
  border: 'rgba(255, 159, 64, 1)'
};
census.graphOrange = {
  background: 'rgba(255, 159, 64, 0.5)',
  border: 'rgba(255, 159, 64, 1)'
};
census.graphGrey = {
  background: 'rgba(184, 187, 188, 0.5)',
  border: 'rgba(184, 187, 188, 1)'
};

// BEGIN filters
census.formatDate = function (value) {
  if (!value || value == null) {
    return '';
  }
  return value.format(census.dateFormat);
};
census.formatDateTime = function (value) {
  if (!value || value == null) {
    return '';
  }
  return value.format(census.dateTimeFormat);
};
census.formatNumber = formatNumber({
  integerSeparator: '.',
  decimalsSeparator: '.',
  decimal: ',',
  truncate: 2
});
// END filters

// create services
census.SpinService = new SpinService('spinner');
// For every service there is a mock variant. The mock variant generates data
// at runtime. There is one main boolean that decides if mocks or real services
// are created.
// TODO: set the boolean based on the environment - now it is hard coded before the build
if (census.mock) {
  census.AppService = new MockAppService();
  census.SourceipService = new MockSourceipService();
  census.DashboardService = new MockDashboardService();
  census.ScheduleService = new MockScheduleService();
} else {
  census.AppService = new AppService();
  census.SourceipService = new SourceipService();
  census.DashboardService = new DashboardService();
  census.ScheduleService = new ScheduleService();
}

// utilities for consuming REST services
census.formatDateForConsumption = function (date) {
  return date.format('YYYY-MM-DD');
};

/**
* Generic REST consuming function. This function centralizes logging, error
* handling and managging the spinner.
* @param promiseReturningFunction A function that returns a promis - typically this will be a promise for the result of a REST service call.
* @param responseHandlingfunction A function to handle the response in case of success.
* @param vm The vue instance (optional)
*/
census.consume = function (promiseReturningFunction, responseHandlingfunction, vm) {
  census.SpinService.up();
  promiseReturningFunction().then(function (response) {
    if (!PRODUCTION) {
      console.log(response);
    }
    responseHandlingfunction(response);
    census.SpinService.down();
  }).catch(function (error) {
    census.SpinService.down();
    console.log(error);
    if (vm != null) {
      vm.$emit('error', {
        title: 'Error consuming service',
        service: true,
        url: error.config.url,
        method: error.config.method,
        status: error.response.status,
        message: error.response.data
      });
    }
  });
};
