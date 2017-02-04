// vim: set sw=2 ts=2:

var census = census || {};

(function (census) {

	function fetchData() {
		var vm = this;
		census.SpinService.up();
		// fetch the data
		census.DashboardService.list(this.subject, this.days)
			.then(function (response) {
				console.log(response);
				vm.data = response;
				census.SpinService.down();
			}).catch(function (error) {
				console.log(error);
				census.SpinService.down();
			});
	}

	function drawGraph() {
		console.log('Drawing the graph in ', this.uid);
		// prepare graph data
		var labels = this.data.map(function (el) {
			return el.date.format('dd D.M.YY');
		});
		var data = this.data.map(function (el) {
			return el.totalRequests;
		});
		var backgrounds = this.data.map(function (el) {
			switch (el.date.day()) {
				case 1:
					return 'rgba(255, 99, 132, 0.2)';
				case 2:
					return 'rgba(54, 162, 235, 0.2)';
				case 3: 
					return 'rgba(255, 206, 86, 0.2)';
				case 4: 
					return 'rgba(75, 192, 192, 0.2)';
				case 5:
					return 'rgba(153, 102, 255, 0.2)';
				case 6:
					return 'rgba(255, 159, 64, 0.2)';
				default:
					return 'rgba(255, 157, 15, 0.2)';
			}
		});
		var borders = this.data.map(function (el) {
			switch (el.date.day()) {
				case 1:
					return 'rgba(255, 99, 132, 1)';
				case 2:
					return 'rgba(54, 162, 235, 1)';
				case 3: 
					return 'rgba(255, 206, 86, 1)';
				case 4: 
					return 'rgba(75, 192, 192, 1)';
				case 5:
					return 'rgba(153, 102, 255, 1)';
				case 6:
					return 'rgba(255, 159, 64, 1)';
				default:
					return 'rgba(255, 157, 15, 1)';
			}
		});
		// start drawing
		var vm = this;
		var ctx = document.getElementById(this.uid);
		var myChart = new Chart(ctx, {
			type: 'bar',
			data: {
				labels: labels,
				datasets: [
					{
						data: data,
						backgroundColor: backgrounds,
						borderColor: borders,
						borderWidth: 1
					}
				]
			},
			options: {
				title: {
					display: true,
					text: 'Total number of requests per day'
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
		this.chart = myChart;
	}

	Vue.component('census-dashboard-subject', {
		template: `
			<census-panel v-bind:title="subject.name">
					<canvas v-bind:id="uid" width="400" height="100"></canvas>
					<div>
						<p><button class="btn btn-default" v-on:click="toggleDetails">Toggle details</button></p>
						<table v-if="showDetails" class="table table-striped">
							<thead>
								<tr>
									<th>date</th>
									<th># requests</th>
									<th># errors</th>
									<th>avg response time (s)</th>
									<th>min response time (s)</th>
									<th>max response time (s)</th>
									<th># users</th>
								</tr>
							</thead>
							<tbody>
								<tr v-for="d in data">
									<td>{{d.date.format('dd D.MM.YY')}}</td>
									<td>{{d.totalRequests}}</td>
									<td>{{d.totalRequestsInError}}</td>
									<td>{{d.averageResponseTime}}</td>
									<td>{{d.minResponseTime}}</td>
									<td>{{d.maxResponseTime}}</td>
									<td>{{d.totalUserIds}}</td>
								</tr>
							</tbody>
						</table>
					</div>
			</census-panel>
		`,
		props: ['subject', 'days', 'id'],
		data: function () {
			return {
				data: null,
				showDetails: false
			};
		},
		computed: {
			uid: function () {
				return 'censusDashboardApp' + this.id;
			}
		},
		methods: {
			toggleDetails: function () {
				this.showDetails = !this.showDetails;
			},
			drillDown: function (idx) {
				console.log('Drilling down to ', this.data[idx].date.format('DD/MM/YYYY'));
				this.$emit('drill', { 
					subject: this.subject, 
					date: this.data[idx].date 
				});
			}
		},
		watch: {
			data: drawGraph
		},
		created: fetchData
	});

})(census);
