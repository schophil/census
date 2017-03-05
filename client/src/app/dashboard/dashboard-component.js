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

  function applyFilter() {
    this.filter = this.filterField;
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
      <census-panel>
        <form class="form-inline" v-on:submit.prevent="applyFilter">
				<div class="form-group">
					<label for="filter">User category filter</label>
					<input type="text" class="form-control" id="filter" v-model="filterField" placeholder="Exclude or include categories">
				</div>
			  <button type="submit" v-bind:disabled="!canApplyFilter" class="btn btn-primary">Apply</button>
			</form>
      </census-panel>
			<div id="censusDashboardLevel1" v-show="isLevel1">	
				<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" v-bind:filter="filter" v-bind:subject="s"></census-dashboard-subject>
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
        filter: null,
        filterField: null,
				target: {
					subject: null,
					date: null
				}
			};
		},
		methods: {
			drill: drill,
			goToStart: goToStart,
      applyFilter: applyFilter
		},
		computed: {
			isLevel1: function () {
				return this.level === 1;
			},
			isLevel2: function () {
				return this.level === 2;
			},
      canApplyFilter: function () {
        return this.filterField != null;
      }
		}
	});

})(census);
