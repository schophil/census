import census from './census';
import SpinService from './spinner/spinner-service';
import formatNumber from 'format-number';
import { AppService, MockAppService } from './app-service';
import { SourceipService, MockSourceipService } from './sourceip/sourceip-service';
import { DashboardService, MockDashboardService } from './dashboard/dashboard-service';
import { ScheduleService, MockScheduleService } from './schedule/schedule-service';

// settings
census.mock = true;
census.mockDelay = 1000;
census.dateFormat = 'dd D.M.YY';
census.dateApiFormat = 'YYYY-MM-DD';

// filters
census.formatDate = function (value) {
  if (!value) {
    return '';
  }
  return value.format(census.dateFormat);
};
census.formatNumber = formatNumber({
  integerSeparator: '.',
  decimalsSeparator: '.',
  decimal: ',',
  truncate: 2
});

// create services
census.SpinService = new SpinService('spinner');

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

// utilities
census.consume = function (promiseReturningFunction, responseHandlingfunction) {
  census.SpinService.up();
  promiseReturningFunction().then(function (response) {
    console.log(response);
    responseHandlingfunction(response);
    census.SpinService.down();
  }).catch(function (error) {
    this.$emit('error', {
      title: 'Error retrieving data',
      message: error
    });
    census.SpinService.down();
  });
};
