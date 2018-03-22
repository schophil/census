import Vue from 'vue';
import Chart from 'chart.js';
import census from '../census';
import moment from 'moment';

import '../graphs/rtime-per-hour-graph-component';

function fetchData() {
  var vm = this;
  census.consume(
    function () {
      return census.DashboardService.userDetails(vm.subject, vm.date, vm.user);
    },
    function (response) {
      vm.data = response.data;
    },
    vm
  );
}

function drawGraph() {
  // start drawing
	var vm = this;

  var labels = vm.data.activityPerHour.map(function (h) {
    return h.hour;
  });

  var requests = {};
  requests.label = '# requests';
  requests.data = vm.data.activityPerHour.map(function (h) {
    return h.totalRequests;
  });
  requests.backgroundColor = census.graphGreen.background;
  requests.borderColor = census.graphGreen.border;
  requests.borderWidth = 1;

  var responseTime = {};
  responseTime.label = 'avg response time';
  responseTime.data = vm.data.activityPerHour.map(function (h) {
    return h.averageResponseTime;
  });
  responseTime.backgroundColor = census.graphBlue.background;
  responseTime.borderColor = census.graphBlue.border;
  responseTime.borderWidth = 1;

	var ctx = document.getElementById(this.uida);
	var myChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: labels,
			datasets: [requests]
		},
		options: {
			legend: {
				display: true
			},
			scales: {
				yAxes: [{
					ticks: {
						beginAtZero:true
					}
				}]
			}
		}
	});
}

Vue.component('census-dashboard-subject-day-user', {
  template: '\
  <div>\
    <census-panel title="Requests per hour">\
      <canvas v-bind:id="uida" width="400" height="100"></canvas>\
    </census-panel>\
    <census-panel title="Response time per hour">\
      <census-rtime-per-hour v-bind:data="responseTimes" gid="rtimegraph"></census-rtime-per-hour>\
    </census-panel>\
		<census-panel title="Popular resources">\
			<table class="table table-striped" v-if="data">\
				<thead>\
					<tr>\
						<th>Resource</th>\
            <th># requests</th>\
            <th># errors</th>\
            <th>avg response time (s)</th>\
						<th>min response time (s)</th>\
						<th>max response time (s)</th>\
					</tr>\
				</thead>\
				<tbody>\
					<tr v-for="r in data.popularResources">\
						<td>{{ r.path }}</td>\
            <td>{{ r.totalRequests | formatNumber }}</td>\
						<td>{{ r.totalRequestsInError | formatNumber }}</td>\
						<td>{{ r.averageResponseTime | formatNumber }}</td>\
						<td>{{ r.minResponseTime | formatNumber }}</td>\
						<td>{{ r.maxResponseTime | formatNumber }}</td>\
					</tr>\
				</tbody>\
			</table>\
		</census-panel>\
  </div>\
  \
  ',
  props: ['subject', 'date', 'user', 'id'],
  data: function () {
		return {
			data: null
		};
	},
  computed: {
		uida: function () {
			return 'census_dashboard_ga_' + this.id;
		},
		uidb: function () {
			return 'census_dashboard_gb_' + this.id;
    },
    responseTimes: function () {
      if (this.data) {
        return this.data.activityPerHour;
      }
      return null;
    }
	},
  watch: {
		data: drawGraph
  },
  created: fetchData
});
