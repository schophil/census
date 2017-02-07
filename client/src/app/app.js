// vim: set sw=2 ts=2:
'use strict';

var census = census || {};

(function (census) {

	function goToDashboard() {
		this.inDashboard = true;
		this.inAbout = false;
	}

	function goToAbout() {
		this.inDashboard = false;
		this.inAbout = true;
	}

	function _created() {
		console.log('Fetching the list of apps with', census.AppService);
		var vm = this;
		census.SpinService.up();

		census.AppService.getApps()
			.then(function (response) {
				console.log(response);
				vm.subjects = response.data;
				census.SpinService.down();
			}).catch(function (error) {
				console.log(error);
				census.SpinService.down();
				this.alerts.push({
					title: "Communication error ocurred",
					message: error
				});
			});
	}

	var vm = new Vue({
		el: '#censusApp',
		data: {
			subjects: [],
			inDashboard: true,
			inAbout: false,
			alerts: []
		},
		methods: {
			goToDashboard: goToDashboard,
			goToAbout: goToAbout
		},
		created: _created
	});

	census.vm = vm;

})(census);

