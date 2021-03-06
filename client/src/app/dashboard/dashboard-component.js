import Vue from 'vue';
import census from '../census';

import './dashboard-subject-component';
import './dashboard-day-component';
import './dashboard-user-component';

function drill(target) {
	if (!PRODUCTION) {
		console.log('Drill to ', target);
	}
	if (target) {
		if (target.user) {
			this.target.subject = target.subject;
			this.target.date = target.date;
			this.target.user = target.user;
			this.level = 3;
		} else {
			this.target.subject = target.subject;
			this.target.date = target.date;
			this.level = 2;
		}
	}
}

function goToStart() {
	this.level = 1;
}

function goToLevel2() {
	this.level = 2;
}

function applyFilter() {
  var unchecked = 0;
  var checked = 0;
  this.categories.forEach(function (c) {
    if (c.checked) {
      checked++;
    } else {
      unchecked++;
    }
  });

  var filterValue = null;
  if (checked == 0 || checked > unchecked) {
    // build an exclude query
    this.categories.forEach(function (c) {
      if (!c.checked) {
        if (filterValue != null) {
          filterValue += ",!" + c.value;
        } else {
          filterValue = "!" + c.value;
        }
      }
    });
  } else if (unchecked != 0) {
    // build an include query
    this.categories.forEach(function (c) {
      if (c.checked) {
        if (filterValue != null) {
          filterValue += "," + c.value;
        } else {
          filterValue = c.value;
        }
      }
    });
  }

  if (this.filter == null || this.filter != filterValue) {
    this.filter = filterValue;
  }
}

function created() {
	var vm = this;
	census.consume(
		function () {
			return census.DashboardService.getCategories();
		},
		function (response) {
			response.data.forEach(function (value) {
        vm.categories.push({
          checked: true,
          value: value
        });
      });
		},
		vm
	);
}

function checkAll() {
  this.categories.forEach(function (c) {
    c.checked = true;
  });
}

function uncheckAll() {
  this.categories.forEach(function (c) {
    c.checked = false;
  });
}

Vue.component('census-dashboard', {
	template: '\
	<div id="censusDashboard">\
		<div>\
			<ol class="breadcrumb">\
				<li><a href="#" v-on:click.stop="goToStart">Start</a></li>\
				<li v-if="!isLevel1">{{ target.subject.name }}</li>\
				<li v-if="isLevel2 || isLevel3"><a href="#" v-on:click.stop="goToLevel2">{{ target.date.format("dd D.M.YY") }}</a></li>\
				<li v-if="isLevel3">{{ target.user }}</li>\
			</ol>\
		</div>\
    <census-panel v-if="isLevel1">\
      <form class="form" v-on:submit.prevent="applyFilter">\
        <div class="checkbox">\
          <label class="checkbox-inline" v-for="c in categories">\
            <input type="checkbox" v-bind:id="c" v-model="c.checked"> {{c.value}}\
          </label>\
        </div>\
		    <button type="submit" class="btn btn-primary">Apply</button>\
		    <button type="button" class="btn btn-default" v-on:click="checkAll" >Check all</button>\
		    <button type="button" class="btn btn-default" v-on:click="uncheckAll">Uncheck all</button>\
			</form>\
    </census-panel>\
		<div id="censusDashboardLevel1" v-show="isLevel1">\
			<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" :key="s.id" v-bind:filter="filter" v-bind:subject="s"></census-dashboard-subject>\
		</div>\
		<div id="censusDashboardLevel2" v-if="isLevel2">\
			<census-dashboard-subject-day v-on:drill="drill" v-bind:subject="target.subject" v-bind:date="target.date"></census-dashboard-subject-day>\
		</div>\
		<div id="censusDashboardLevel3" v-if="isLevel3">\
			<census-dashboard-subject-day-user v-bind:subject="target.subject" v-bind:date="target.date" v-bind:user="target.user"></census-dashboard-subject-day-user>\
		</div>\
	</div>\
	',
	props: ['subjects'],
	data: function () {
		return {
			level: 1,
			crumbs: [],
      filter: null,
      filterField: null,
      categories: [],
			target: {
				subject: null,
				date: null,
				user: null
			}
		};
	},
	methods: {
		drill: drill,
		goToStart: goToStart,
		goToLevel2: goToLevel2,
    applyFilter: applyFilter,
    checkAll: checkAll,
    uncheckAll: uncheckAll
	},
	computed: {
		isLevel1: function () {
			return this.level === 1;
		},
		isLevel2: function () {
			return this.level === 2;
		},
		isLevel3: function () {
			return this.level === 3;
		}
	},
  created: created
});
