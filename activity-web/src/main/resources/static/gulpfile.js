var gulp = require('gulp');
var sass = require('gulp-sass');
var minifyCss = require('gulp-minify-css');//css压缩
var auto = require('gulp-autoprefixer');//解决浏览器兼容问题的插件
// var watch = require('gulp-watch');


// (编译命令：gulp activity)
//APP
gulp.task('sasslist', function () {
    return gulp.src(['./mobile/assets/sass/**/*.scss','./mobile/assets/sass/*.scss'])      //需要编译的文件目录
        .pipe(sass({
            outputStyle: 'expanded', // 输出方式
        }))
        .pipe(auto({//处理兼容
            overrideBrowserslist: [
                "Android 4.1",
                "iOS 7.1",
                "Chrome > 31",
                "ff > 20",
                "ie >= 8",
                'last 10 versions'// 所有主流浏览器最近10版本用
            ]
        }))
        .pipe(gulp.dest('./mobile/assets/css'));    //存放编译之后的目录
});
gulp.task('watchlist', function () {
    gulp.watch(['./mobile/assets/sass/**/*.scss','./mobile/assets/sass/*.scss'],  ['sasslist']);// 监听的文件
});

//pc
gulp.task('sasslist1', function () {
    return gulp.src(['./pc/assets/sass/**/*.scss','./pc/assets/sass/*.scss'])      //需要编译的文件目录
        .pipe(sass({
            outputStyle: 'expanded', // 输出方式
        }))
        .pipe(auto({//处理兼容
            overrideBrowserslist: [
                "Chrome > 31",
                "ff > 20",
                "ie >= 8",
                'last 10 versions'// 所有主流浏览器最近10版本用
            ]
        }))
        .pipe(gulp.dest('./pc/assets/css'));    //存放编译之后的目录
});
gulp.task('watchlist1', function () {
    gulp.watch(['./pc/assets/sass/**/*.scss','./pc/assets/sass/*.scss'],  ['sasslist1']);// 监听的文件
});
gulp.task('default', ['sasslist', 'watchlist','sasslist1', 'watchlist1']);

