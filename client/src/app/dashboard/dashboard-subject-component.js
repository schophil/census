import Vue from 'vue';
import Chart from 'chart.js';
import census from '../census';

function fetchData() {
	var vm = this;
	census.consume(
		function () {
			return census.DashboardService.list(vm.subject, vm.days, vm.filter);
		},
		function (response) {
			vm.data = response.data;
		},
		vm
	);
}

function drawGraphs() {
  while (this.charts.length > 0) {
    this.charts.pop().destroy();
  }

	// prepare graph data
	var labels = this.data.map(function (el) {
		return el.date.format('dd D.M.YY');
	});
	var backgrounds = this.data.map(function (el) {
    return census.graphColors[el.date.day()].background;
	});
	var borders = this.data.map(function (el) {
		return census.graphColors[el.date.day()].border;
	});
	// draw graph for total requests
	{
		var data = this.data.map(function (el) {
			return el.totalRequests;
		});
		(_drawGraph.bind(this))(this.uida, '# requests', labels, data, backgrounds, borders);
	}
	// draw graph for response time
	{
		var data = this.data.map(function (el) {
			return el.averageResponseTime;
		});
		(_drawGraph.bind(this))(this.uidb, 'avg response time (seconds)', labels, data, backgrounds, borders);
	}
}

function _drawGraph(el, title, label, data, background, border) {
	if (!PRODUCTION) {
			console.log('Drawing single graph on ', el);
	}
	var vm = this;
	var ctx = document.getElementById(el);
	if (!PRODUCTION) {
		console.log('Drawing single graph on id ', el);
		console.log('Drawing single graph on ctx ', ctx);
	}
	var myChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: label,
			datasets: [
				{
					data: data,
					backgroundColor: background,
					borderColor: border,
					borderWidth: 1
				}
			]
		},
		options: {
			title: {
				display: true,
				text: title
      },
			legend: {
				display: false
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
  vm.charts.push(myChart);
}

Vue.component('census-dashboard-subject', {
	template: '\
		<census-panel v-bind:title="subject.name">\
				<div class="row">\
					<div class="col-md-6"><canvas v-bind:id="uida" width="400" height="200"></canvas></div>\
					<div class="col-md-6"><canvas v-bind:id="uidb" width="400" height="200"></canvas></div>\
				</div>\
				<div>\
					<p><button class="btn btn-default" v-on:click="toggleDetails">Toggle details</button></p>\
					<table v-if="showDetails" class="table table-striped">\
						<thead>\
							<tr>\
								<th>date</th>\
								<th># requests</th>\
								<th># errors</th>\
								<th>avg response time (s)</th>\
								<th>min response time (s)</th>\
								<th>max response time (s)</th>\
								<th># users</th>\
							</tr>\
						</thead>\
						<tbody>\
							<tr v-for="d in data">\
								<td>{{ d.date | formatDate }}</td>\
								<td>{{ d.totalRequests | formatNumber }}</td>\
								<td>{{ d.totalRequestsInError | formatNumber }}</td>\
								<td>{{ d.averageResponseTime | formatNumber }}</td>\
								<td>{{ d.minResponseTime | formatNumber }}</td>\
								<td>{{ d.maxResponseTime | formatNumber }}</td>\
								<td>{{ d.totalUserIds | formatNumber }}</td>\
							</tr>\
						</tbody>\
					</table>\
				</div>\
		</census-panel>\
	',
	props: ['subject', 'days', 'id', 'filter'],
	data: function () {
		return {
			data: null,
			showDetails: false,
      charts: []
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
	methods: {
		toggleDetails: function () {
			this.showDetails = !this.showDetails;
		},
		drillDown: function (idx) {
			if (!PRODUCTION) {
				console.log('Drilling down to ', this.data[idx].date.format('DD/MM/YYYY'));
			}
      this.$router.push({
        path: '/dashboard/subject/' + this.subject.id + '/date/' + this.data[idx].date.format(census.dateApiFormat)
      });
		}
	},
	filters: {
		formatDate: census.formatDate,
		formatNumber: census.formatNumber
	},
	watch: {
		data: drawGraphs,
    filter: fetchData
	},
	created: fetchData
});
