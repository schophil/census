// vim: set sw=2 ts=2:

var census = census || {};

(function (census) {

	function drill(target) {
		console.log('Drill to ', target);
		if (target) {
			this.target.subject = target.subject;
			this.target.date = target.date;
			this.level = 2;
		}
	}

	function goToStart() {
		this.level = 1;
	}

	Vue.component('census-dashboard', {
		template: `
		<div id="censusDashboard">
			<div>
				<ol class="breadcrumb">
					<li><a href="#" v-on:click.stop="goToStart">Start</a></li>
					<li v-if="!isLevel1">{{target.subject.name}}</li>
					<li v-if="isLevel2">{{target.date.format('dd D.M.YY')}}</li>
				</ol>
			</div>
			<div id="censusDashboardLevel1" v-show="isLevel1">	
				<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" v-bind:subject="s"></census-dashboard-subject>
			</div>
			<div id="censusDashboardLevel2" v-if="isLevel2">
				<census-dashboard-subject-day v-bind:subject="target.subject" v-bind:date="target.date"></census-dashboard-subject-day>
			</div>	
		</div>
		`,
		props: ['subjects'],
		data: function () {
			return {
				level: 1,
				crumbs: [],
				target: {
					subject: null,
					date: null
				}
			};
		},
		methods: {
			drill: drill,
			goToStart: goToStart
		},
		computed: {
			isLevel1: function () {
				return this.level === 1;
			},
			isLevel2: function () {
				return this.level === 2;
			}
		}
	});

})(census);
