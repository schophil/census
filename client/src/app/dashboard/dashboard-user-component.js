import Vue from 'vue';
import Chart from 'chart.js';
import census from '../census';
import moment from 'moment';

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
  requests.backgroundColor = 'rgba(75, 192, 192, 0.9)';
  requests.borderColor = 'rgba(75, 192, 192, 1)';
  requests.borderWidth = 1;

  var responseTime = {};
  responseTime.label = 'avg response time';
  responseTime.data = vm.data.activityPerHour.map(function (h) {
    return h.averageResponseTime;
  });
  responseTime.backgroundColor = 'rgba(75, 192, 192, 0.9)';
  responseTime.borderColor = 'rgba(75, 192, 192, 1)';
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
			onClick: function (ev, el) {
				if (el && el.length > 0) {
					vm.drillDown(el[0]._index);
				}
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

  var ctx2 = document.getElementById(this.uidb);
	var myChart2 = new Chart(ctx2, {
		type: 'bar',
		data: {
			labels: labels,
			datasets: [responseTime]
		},
		options: {
			legend: {
				display: true
			},
			onClick: function (ev, el) {
				if (el && el.length > 0) {
					vm.drillDown(el[0]._index);
				}
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


	//this.chart = myChart;
}

Vue.component('census-dashboard-subject-day-user', {
  template: '\
  <div>\
    <census-panel title="Stats per hour">\
      <div class="row">\
        <div class="col-md-6"><canvas v-bind:id="uida" width="400" height="200"></canvas></div>\
        <div class="col-md-6"><canvas v-bind:id="uidb" width="400" height="200"></canvas></div>\
      </div>\
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
		}
	},
  watch: {
		data: drawGraph
  },
  created: fetchData
});
