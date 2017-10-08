import Vue from 'vue';
import census from '../census';

function searchSources() {
	if (!PRODUCTION) {
			console.log("Searching ", this.search);
	}
	var vm = this;
	census.consume(
    function () {
      return census.SourceipService.search(vm.search.filter, vm.search.days);
    },
    function (response) {
      vm.data = response.data;
    },
    vm
  );
}

Vue.component('census-sourceip', {
	template: '\
	<census-panel>\
		<form class="form-inline" v-on:submit.prevent="searchSources">\
			<div class="form-group">\
				<label for="filter">Filter</label>\
				<input type="text" class="form-control" id="filter" v-model="search.filter" placeholder="Search for...">\
			</div>\
			<div class="form-group">\
				<label for="days">Last days</label>\
				<input type="number" class="form-control" v-model="search.days" id="days">\
			</div>\
			<button type="submit" v-bind:disabled="!canSearch" class="btn btn-primary">Search</button>\
		</form>\
		<hr/>\
		<table class="table table-striped">\
			<thead>\
				<tr>\
					<th>user</th>\
					<th>ip</th>\
					<th>last used on</th>\
				</tr>\
			</thead>\
			<tbody>\
				<template v-for="d in data">\
					<tr v-for="(ip, index) in d.ips">\
						<td><span v-if="index == 0">{{ d.userid }}</span></td>\
						<td>{{ ip.ip }}</td>\
						<td>{{ ip.lastUsed | formatDate }}</td>\
					</tr>\
				</template>\
			</tbody>\
		</table>\
	</census-panel>\
	',
	props: ['subjects'],
	data: function () {
		return {
			search: {
				filter: null,
				days: 15
			},
			data: []
		};
	},
	methods: {
		searchSources: searchSources
	},
	computed: {
		canSearch: function () {
			return this.search.filter != null && this.search.days != null;
		},
		hasData: function () {
			return this.data.length > 0;
		}
	},
	filters: {
    formatDate: census.formatDate,
    formatDateTime: census.formatDateTime,
  }
});
