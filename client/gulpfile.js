var gulp  = require('gulp');
var server = require('gulp-server-livereload');
var minify = require('gulp-jsmin');
var less = require('gulp-less');

// Simple tasks
gulp.task('scriptsdev', function () {
	gulp.src('src/**/*.js')
		.pipe(gulp.dest('target/dist'));
});

gulp.task('scriptsprod', function () {
	gulp.src('src/**/*.js')
		.pipe(minify())
		.pipe(gulp.dest('target/dist'));
});

gulp.task('resources', function () {
	gulp.src(['src/**/*.*', '!src/**/*.js', '!src/**/*.less'])
		.pipe(gulp.dest('target/dist'));
});

gulp.task('vendors', function () {
	gulp.src(['bower_components/**/dist/**/*.*', 'bower_components/*/*.js'])
		.pipe(gulp.dest('target/dist/vendor'));
});

gulp.task('less', function () {
  gulp.src('src/css/*.less')
    .pipe(less())
    .pipe(gulp.dest('./target/dist/css'));
});

// Development
gulp.task('serve', function () {
	gulp.src('./target/dist')
		.pipe(server({
			defaultFile: 'index.html'
		}));
});

gulp.task('reload', function () {
	gulp.watch('src/**/*.js', ['scriptsdev']);
	gulp.watch('src/css/*.less', ['less']);
	gulp.watch(['src/**/*.*', '!src/**/*.js', '!src/**/*.less'], ['resources']);
});

gulp.task('dev', ['scriptsdev', 'resources', 'less', 'vendors', 'reload', 'serve']);

gulp.task('dist', ['scriptsprod', 'resources', 'less', 'vendors']);
