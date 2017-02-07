// vim: set sw=2 ts=2:

(function () {

	Vue.component('census-alert', {
		template: `
		<div class="alert alert-danger alert-dismissible" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			<strong>{{title}}</strong> {{message}}
		</div>
		`,
		props: ['title', 'message']
	});

})();

