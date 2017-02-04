// vim: set sw=2 ts=2:

var census = census || {};

(function (census) {

	function drill(target) {
		console.log('Drill to ', target);
	}

	Vue.component('census-dashboard', {
		template: `
		<div id="censusDashboard">
			<div id="censusDashboardLevel1">	
				<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" v-bind:subject="s"></census-dashboard-subject>
			</div>
			<div id="censusDashboardLevel2">
				
			</div>	
		</div>
		`,
		props: ['subjects'],
		methods: {
			drill: drill
		}
	});

})(census);
