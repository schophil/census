// vim: set sw=2 ts=2:

(function () {

	Vue.component('census-panel', {
		template: `
			<div class="panel panel-default">
			 	<div class="panel-heading">
					<span>{{title}}</span> 
					<a href="#" v-on:click.stop="togglePanel">
						<span v-if="!showPanel" class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
						<span v-if="showPanel" class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
					</a>
				</div>
				<div class="panel-body" v-show="showPanel">
					<slot></slot>
				</div>
			</div>
		`,
		props: ['title', 'startOpen'],
		data: function () {
			return {
				showPanel: true
			};
		},
		methods: {
			togglePanel: function () {
				this.showPanel = !this.showPanel;
			}
		},
		created: function () {
			if (this.startOpen) {
				this.showPanel = this.startOpen;
			}
		}
	});

})();
