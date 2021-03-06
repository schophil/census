import Vue from 'vue';
import census from './census';
import './census-inject';

import './alert/alert-component';
import './panel/panel-component';
import './about/about-component';
import './sourceip/sourceip-component';
import './dashboard/dashboard-component';
import './schedule/schedule-component';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/css/bootstrap-theme.min.css';

import '../css/census-theme.less';

// BEGIN Menu switching methods
function goToDashboard() {
  this.inDashboard = true;
  this.inAbout = false;
  this.inSourceip = false;
  this.inSchedule = false;
}

function goToAbout() {
  this.inDashboard = false;
  this.inAbout = true;
  this.inSourceip = false;
  this.inSchedule = false;
}

function goToSourceip() {
  this.inDashboard = false;
  this.inAbout = false;
  this.inSourceip = true;
  this.inSchedule = false;
}

function goToSchedule() {
  this.inDashboard = false;
  this.inAbout = false;
  this.inSourceip = false;
  this.inSchedule = true;
}
// END Menu switching methods

function clearAlerts() {
  this.alerts.splice(0, this.alerts.length);
}

function onError(e) {
  this.alerts.push(e);
}

function _created() {
  // Once created we want to load the list of applications analysed by this census instance
  if (!PRODUCTION) {
    console.log('Fetching the list of apps with', census.AppService);
  }
  census.consume(
    function () {
      return census.AppService.getApps();
    },
    function (response) {
      vm.subjects = response.data;
    },
    vm
  );
}

var vm = new Vue({
  el: '#censusApp',
  data: {
    subjects: [],
    inDashboard: true,
    inSourceip: false,
    inAbout: false,
    inSchedule: false,
    alerts: []
  },
  computed: {
    hasAlerts: function () {
      return this.alerts.length > 0;
    }
  },
  methods: {
    goToDashboard: goToDashboard,
    goToAbout: goToAbout,
    goToSourceip: goToSourceip,
    goToSchedule: goToSchedule,
    clearAlerts: clearAlerts,
    onError: onError
  },
  created: _created
});

census.vm = vm;
