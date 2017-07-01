import Vue from 'vue';
import census from '../census';

function fetchData() {
  var vm = this;
  census.consume(
    function () {
      return census.ScheduleService.getScheduled();
    },
    function (response) {
      vm.data = response.data;
    }
  );
}

Vue.component('census-schedule', {
  template: '\
  <div>\
  <census-panel>\
    <div>\
    </div>\
  </census-panel>\
  <census-panel>\
    <table class="table table-striped">\
      <thead>\
        <tr>\
          <th>Target</th>\
          <th>Scheduled on</th>\
          <th>Started on</th>\
          <th>Status</th>\
        </tr>\
      </thead>\
      <tbody>\
        <tr v-for="d in data">\
          <td>{{ d.target | formatDate }}</td>\
          <td>{{ d.scheduledOn | formatDate }}</td>\
          <td>{{ d.startedOn | formatDate }}</td>\
          <td>{{ d.status }}</td>\
        </tr>\
      </tbody>\
    </table>\
  </census-panel>\
  </div>\
  \
  ',
  data: function () {
		return {
			data: null
		};
	},
  filters: {
    formatDate: census.formatDate
  },
  created: fetchData
});
