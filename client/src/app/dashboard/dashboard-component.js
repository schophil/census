import Vue from 'vue';
import census from '../census';

import './dashboard-subject-component';
import './dashboard-day-component';

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
	census.SpinService.up();
  census.DashboardService.getCategories()
    .then(function (response) {
      response.data.forEach(function (value) {
        vm.categories.push({
          checked: true,
          value: value
        });
      });
			census.SpinService.down();
    }).catch(function (error) {
			this.$emit('error', {
				title: 'Error retrieving data',
				message: error
			});
			census.SpinService.down();
    });
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
				<li v-if="!isLevel1">{{target.subject.name}}</li>\
				<li v-if="isLevel2">{{target.date.format("dd D.M.YY")}}</li>\
			</ol>\
		</div>\
    <census-panel>\
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
			<census-dashboard-subject v-on:drill="drill" v-bind:id="idx" days="30" v-for="(s, idx) in subjects" v-bind:filter="filter" v-bind:subject="s"></census-dashboard-subject>\
		</div>\
		<div id="censusDashboardLevel2" v-if="isLevel2">\
			<census-dashboard-subject-day v-bind:subject="target.subject" v-bind:date="target.date"></census-dashboard-subject-day>\
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
				date: null
			}
		};
	},
	methods: {
		drill: drill,
		goToStart: goToStart,
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
		}
	},
  created: created
});
