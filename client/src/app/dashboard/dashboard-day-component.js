import Vue from 'vue';
import Chart from 'chart.js';
import census from '../census';
import moment from 'moment';
import _ from 'lodash';

import '../graphs/rtime-per-hour-graph-component';

function fetchData() {
	var vm = this;

  if (_.isString(vm.date)) {
    vm.date = moment(vm.date);
  }

	census.consume(
		function () {
			return census.DashboardService.dayDetails(vm.subject, vm.date);
		},
		function (response) {
			vm.data = response.data;
		},
		vm
	);
}

function drawGraph() {
  drawTotalRequestsGraph(this);
}

function drawTotalRequestsGraph(vm) {
	if (!PRODUCTION) {
			console.log('Drawing the graph in ', vm.uid);
	}
	// prepare graph data
	var labels = vm.data[1].activityPerHour.map(function (el) {
		return el.hour;
	});

	// prepare data for yesterday
	var yesterday = { };
	yesterday.label = vm.yesterday.format(census.dateFormat);
	if (vm.showAdjacentDays && vm.data[0].activityPerHour) {
		yesterday.data = vm.data[0].activityPerHour.map(function (el) {
			return el.totalRequests;
		});
	}
	yesterday.backgroundColor = census.graphGreen.background;
	yesterday.borderColor = census.graphGreen.border;
	yesterday.borderWidth = 1;

	var target = { };
	target.label = vm.date.format(census.dateFormat);
	target.data = vm.data[1].activityPerHour.map(function (el) {
		return el.totalRequests;
	});
	target.backgroundColor = census.graphBlue.background;
	target.borderColor = census.graphBlue.border;
	target.borderWidth = 1;

	var tomorrow = { };
	tomorrow.label = vm.tomorrow.format(census.dateFormat);
	if (vm.showAdjacentDays && vm.data[2].activityPerHour) {
		tomorrow.data = vm.data[2].activityPerHour.map(function (el) {
			return el.totalRequests;
		});
	}
	tomorrow.backgroundColor = census.graphGrey.background;
	tomorrow.borderColor = census.graphGrey.border;
	tomorrow.borderWidth = 1;

	// start drawing
	var ctx = document.getElementById(vm.uid);
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
}

Vue.component('census-dashboard-subject-day', {
	template: '\
	<div>\
		<census-panel title="Total requests per hour">\
				<canvas v-bind:id="uid" width="400" height="100"></canvas>\
				<div>\
					<p>\
						<button class="btn btn-default" v-on:click="toggleDetails">Toggle details</button>\
						<button class="btn btn-default" v-on:click="toggleAdjacentDays">Toggle adjacent days in graph</button>\
					</p>\
					<table v-if="showDetails" class="table table-striped">\
						<thead>\
							<tr>\
								<th>hour</th>\
								<th>{{ this.yesterday | formatDate }}</th>\
								<th>{{ this.date | formatDate }}</th>\
								<th>{{ this.tomorrow | formatDate }}</th>\
							</tr>\
						</thead>\
						<tbody>\
							<tr v-for="d in dataPivot">\
								<td>{{ d.hour | formatNumber }}</td>\
								<td>{{ d.yesterday | formatNumber }}</td>\
								<td>{{ d.target | formatNumber }}</td>\
								<td>{{ d.tomorrow | formatNumber }}</td>\
							</tr>\
						</tbody>\
					</table>\
				</div>\
		</census-panel>\
    <census-panel title="Response time per hour">\
        <census-rtime-per-hour v-bind:data="responseTimes" gid="rtimegraph"></census-rtime-per-hour>\
    </census-panel>\
		<census-panel title="Popular resources">\
			<table class="table table-striped" v-if="targetData">\
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
					<tr v-for="r in targetData.popularResources">\
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
		<census-panel title="Recorded users">\
			<form class="form-inline" v-on:submit.prevent="">\
				<div class="form-group">\
					<input type="text" class="form-control" id="filter" v-model="userFilter" placeholder="Filter...">\
				</div>\
			</form>\
			<table class="table table-striped" v-if="targetData">\
				<thead>\
					<tr>\
						<th>user</th>\
            <th># requests</th>\
            <th># errors</th>\
            <th>avg response time (s)</th>\
						<th>min response time (s)</th>\
						<th>max response time (s)</th>\
					</tr>\
				</thead>\
				<tbody>\
					<tr v-for="r in targetData.recordedUsers">\
						<template v-if="filterUser(r)">\
							<td><a href="#" @click.prevent="drillDown(r.userId)">{{ r.userId }}</a> - {{ r.userName }}</td>\
							<td>{{ r.totalRequests | formatNumber }}</td>\
							<td>{{ r.totalRequestsInError | formatNumber }}</td>\
							<td>{{ r.averageResponseTime | formatNumber }}</td>\
							<td>{{ r.minResponseTime | formatNumber }}</td>\
							<td>{{ r.maxResponseTime | formatNumber }}</td>\
						</template>\
					</tr>\
				</tbody>\
			</table>\
		</census-panel>\
	</div>\
	',
	props: ['subject', 'date'],
	data: function () {
		return {
			data: null,
			showDetails: false,
			showAdjacentDays: true,
			userFilter: null
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
					hour: i,
					yesterday: null,
					tomorrow: null,
					target: null
				};
				if (this.data[0] != null && this.data[0].activityPerHour != null) {
					pivot.yesterday = this.data[0].activityPerHour[i].totalRequests;
				}
				pivot.target = this.data[1].activityPerHour[i].totalRequests;
				if (this.data[2] != null && this.data[2].activityPerHour != null) {
						pivot.tomorrow = this.data[2].activityPerHour[i].totalRequests;
				}
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
    },
    responseTimes: function () {
      return this.data ? this.data[1].activityPerHour : null;
    }
	},
	methods: {
		toggleDetails: function () {
			this.showDetails = !this.showDetails;
		},
		toggleAdjacentDays: function () {
			this.showAdjacentDays = !this.showAdjacentDays;
		},
		filterUser: function (r) {
			if (this.userFilter == null) {
				return true;
			}
			var pattern = new RegExp(this.userFilter, "i");
			return r.userId.search(pattern) >= 0 || r.userName.search(pattern) >= 0;
		},
		drillDown: function (user) {
			if (!PRODUCTION) {
				console.log('Drilling down to ', user);
			}
			this.$emit('drill', {
				subject: this.subject,
				date: this.date,
				user: user
			});
		}
	},
	filters: {
		formatDate: census.formatDate,
		formatNumber: census.formatNumber
	},
	watch: {
		data: drawGraph,
		showAdjacentDays: drawGraph
	},
	created: fetchData
});
