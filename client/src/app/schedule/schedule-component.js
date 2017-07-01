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

function schedule() {
  var vm = this;
  census.consume(
    function () {
      return census.ScheduleService.schedule(vm.date, vm.subject);
    },
    function (response) {
      (fetchData.bind(vm))();
    }
  );
}

function canSchedule() {
  return this.date != null && this.subject != null;
}

Vue.component('census-schedule', {
  template: '\
  <div>\
  <census-panel>\
    <form class="form-inline" v-on:submit.prevent="schedule">\
			<div class="form-group">\
				<label for="date">Date</label>\
				<input type="text" class="form-control" id="date" v-model="date" placeholder="Target date...">\
			</div>\
			<div class="form-group">\
				<label for="subject">subject</label>\
				<input type="text" class="form-control" v-model="subject" placeholder="Subject..." id="subject">\
			</div>\
			<button type="submit" v-bind:disabled="!canSchedule" class="btn btn-primary">Schedule</button>\
    </form>\
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
			data: null,
      date: null,
      subject: null
		};
	},
  methods: {
    schedule: schedule
  },
  computed: {
    canSchedule: canSchedule
  },
  filters: {
    formatDate: census.formatDate
  },
  created: fetchData
});
