import Vue from 'vue';
import census from '../census';

function drawGraph() {
  drawResponseTimeGraph(this);
}

function drawResponseTimeGraph(vm) {
  if (!vm.data) {
    return;
  }
	// prepare graph data
	var labels = vm.data.map(function (el) {
		return el.hour;
	});

  var rmin = { };
	rmin.label = 'min response time'
  rmin.data = vm.data.map(function (el) {
    return el.minResponseTime;
  });
	rmin.backgroundColor = census.graphGreen.background;
	rmin.borderColor = census.graphGreen.border;
	rmin.borderWidth = 1;

  var ravg = { };
	ravg.label = 'avg response time'
  ravg.data = vm.data.map(function (el) {
    return el.averageResponseTime;
  });
	ravg.backgroundColor = census.graphBlue.background;
	ravg.borderColor = census.graphBlue.border;
	ravg.borderWidth = 1;

  var rmax = { };
	rmax.label = 'max response time'
  rmax.data = vm.data.map(function (el) {
    return el.maxResponseTime;
  });
	rmax.backgroundColor = census.graphRed.background;
	rmax.borderColor = census.graphRed.background;
	rmax.borderWidth = 1;

  // start drawing
	var ctx = document.getElementById(vm.gid);
	var myChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: labels,
			datasets: [rmin, ravg, rmax]
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

Vue.component('census-rtime-per-hour', {
  template: '<canvas v-bind:id="gid" width="400" height="100"></canvas>',
  props: ['data', 'gid'],
  watch: {
		data: drawGraph
  },
  created: drawGraph
});