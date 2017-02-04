// vim: set sw=2 ts=2:
'use strict';

var census = census || {};

census.SpinService = function (census, el) {

	function SpinService(el) {
		
		this.count = 0;
		this.el = el;
		this.spinner = null;

		this.up = function () {
			console.log('Spin up', this);
			this.count++;
			if (this.count == 1) {
				var target = document.getElementById(this.el);
				if (this.spinner == null) {
					this.spinner = new Spinner({
						shadow: false,
						length: 10,
						width: 5,
						radius: 15,
						top: '30%'
					}).spin(target);
				} else {
					this.spinner.spin(target);
				}
			}
		};

		this.down = function () {
			console.log('Spin down', this);
			this.count--;
			if (this.count <= 0) {
				console.log('Stopping spinner...');
				this.spinner.stop();
			}
		};
	}

	return new SpinService(el);

}(census, 'spinner');
