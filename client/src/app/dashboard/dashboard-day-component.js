import Vue from 'vue';
import Chart from 'chart.js';
import census from '../census';

function fetchData() {
	var vm = this;
	census.SpinService.up();
	// fetch the data
	census.DashboardService.dayDetails(this.subject, this.date)
		.then(function (response) {
			console.log(response);
			vm.data = response.data;
			census.SpinService.down();
		}).catch(function (error) {
			this.$emit('error', {
				title: 'Error retrieving data',
				message: error
			});
			census.SpinService.down();
		});
}

function drawGraph() {
	console.log('Drawing the graph in ', this.uid);
	// prepare graph data
	var labels = this.data[1].activityPerHour.map(function (el) {
		return el.hour;
	});

	// prepare data for yesterday
	var yesterday = { };
	yesterday.label = this.yesterday.format(census.dateFormat);
	if (this.showAdjacentDays && this.data[0].activityPerHour) {
		yesterday.data = this.data[0].activityPerHour.map(function (el) {
			return el.totalRequests;
		});
	}
	yesterday.backgroundColor = 'rgba(75, 192, 192, 0.1)';
	yesterday.borderColor = 'rgba(75, 191, 192, 1)';
	yesterday.borderWidth = 1;

	var target = { };
	target.label = this.date.format(census.dateFormat);
	target.data = this.data[1].activityPerHour.map(function (el) {
		return el.totalRequests;
	});
	target.backgroundColor = 'rgba(75, 192, 192, 0.9)';
	target.borderColor = 'rgba(75, 192, 192, 1)';
	target.borderWidth = 1;

	var tomorrow = { };
	tomorrow.label = this.tomorrow.format(census.dateFormat);
	if (this.showAdjacentDays && this.data[2].activityPerHour) {
		tomorrow.data = this.data[2].activityPerHour.map(function (el) {
			return el.totalRequests;
		});
	}
	tomorrow.backgroundColor = 'rgba(75, 192, 192, 0.4)';
	tomorrow.borderColor = 'rgba(75, 192, 192, 1)';
	tomorrow.borderWidth = 1;

	// start drawing
	var vm = this;
	var ctx = document.getElementById(this.uid);
	var myChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: labels,
			datasets: [yesterday, target, tomorrow]
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
	this.chart = myChart;
}

Vue.component('census-dashboard-subject-day', {
	template: `
	<div>
		<census-panel title="Total requests per hour">
				<canvas v-bind:id="uid" width="400" height="100"></canvas>
				<div>
					<p>
						<button class="btn btn-default" v-on:click="toggleDetails">Toggle details</button>
						<button class="btn btn-default" v-on:click="toggleAdjacentDays">Toggle adjacent days in graph</button>
					</p>
					<table v-if="showDetails" class="table table-striped">
						<thead>
							<tr>
								<th>hour</th>
								<th>{{this.yesterday.format(census.dateFormat)}}</th>
								<th>{{this.date.format(census.dateFormat)}}</th>
								<th>{{this.tomorrow.format(census.dateFormat)}}</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="d in dataPivot">
								<td>{{d.hour}}</td>
								<td>{{d.yesterday}}</td>
								<td>{{d.target}}</td>
								<td>{{d.tomorrow}}</td>
							</tr>
						</tbody>
					</table>
				</div>
		</census-panel>
		<census-panel title="Popular resources">
			<table class="table table-striped" v-if="targetData">
				<thead>
					<tr>
						<th>Resource</th>
						<th>Hits</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="r in targetData.popularResources">
						<td>{{r.path}}</td>
						<td>{{r.hits}}</td>
					</tr>
				</tbody>
			</table>
		</census-panel>
	</div>
	`,
	props: ['subject', 'date'],
	data: function () {
		return {
			data: null,
			showDetails: false,
			showAdjacentDays: true
		};
	},
	computed: {
		uid: function () {
			return 'census_dashboard_day';
		},
		dataPivot: function () {
			var dataPivot = [];
			for (var i = 0; i < 24; i++) {
				var pivot = {
					hour: i
				};
				pivot.yesterday = this.data[0].activityPerHour[i].totalRequests;
				pivot.target = this.data[1].activityPerHour[i].totalRequests;
				pivot.tomorrow = this.data[2].activityPerHour[i].totalRequests;
				dataPivot.push(pivot);
			}
			return dataPivot;
		},
		yesterday: function () {
			return moment(this.date).subtract(1, 'days');
		},
		tomorrow: function () {
			return moment(this.date).add(1, 'days');
		},
		targetData: function () {
			return this.data ? this.data[1] : null;
		}
	},
	methods: {
		toggleDetails: function () {
			this.showDetails = !this.showDetails;
		},
		toggleAdjacentDays: function () {
			this.showAdjacentDays = !this.showAdjacentDays;
		}
	},
	watch: {
		data: drawGraph,
		showAdjacentDays: drawGraph
	},
	created: fetchData
});
