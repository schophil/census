import Vue from 'vue';
import VueRouter from 'vue-router';
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

Vue.use(VueRouter);

// Route components
const Dashboard = {
  template: '<census-dashboard v-bind:subjects="$router.app.subjects" v-on:error="$router.app.onError"></census-dashboard>' 
};
const DashboardSubject = {
  template: '<div><census-dashboard-subject days="30" v-bind:id="idx" v-for="(s, idx) in $router.app.subjects" :key="s.id" v-bind:subject="s"></census-dashboard-subject></div>'
};
const DashboardSubjectDate = {
  template: '<census-dashboard-subject-day :subject="subject" :date="date"></census-dashboard-subject-day>'
};
const SourceIP = {
  template: '<census-sourceip v-bind:subjects="$router.app.subjects" v-on:error="$router.app.onError"></census-sourceip>'
};
const Schedule = {
  template: '<census-schedule v-bind:subjects="$router.app.subjects" v-on:error="$router.app.onError"></census-schedule>'
};
const About = {
  template: '<census-about></census-about>'
};

/*<div id="censusDashboardLevel1" v-show="isLevel1">\*/
			//<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" :key="s.id" v-bind:filter="filter" v-bind:subject="s"></census-dashboard-subject>\
		//</div>\
		//<div id="censusDashboardLevel2" v-if="isLevel2">\
			//<census-dashboard-subject-day v-on:drill="drill" v-bind:subject="target.subject" v-bind:date="target.date"></census-dashboard-subject-day>\
		//</div>\
		//<div id="censusDashboardLevel3" v-if="isLevel3">\
			//<census-dashboard-subject-day-user v-bind:subject="target.subject" v-bind:date="target.date" v-bind:user="target.user"></census-dashboard-subject-day-user>\
		/*</div>\*/


const routes = [
  { 
    path: '/dashboard', component: Dashboard, alias: '/',
    children: [
      {
        path: '', component: DashboardSubject
      },
      {
        path: 'subject/:subject/date/:date', component: DashboardSubjectDate, props: true
      }
    ]
  },
  { path: '/sourceip', component: SourceIP },
  { path: '/schedule', component: Schedule },
  { path: '/about', component: About }
];

const router = new VueRouter({
  routes
});

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
    alerts: []
  },
  computed: {
    hasAlerts: function () {
      return this.alerts.length > 0;
    }
  },
  methods: {
    clearAlerts: clearAlerts,
    onError: onError
  },
  created: _created,
  router: router
});

census.vm = vm;
