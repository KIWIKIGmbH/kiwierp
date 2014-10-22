'use strict';

angular.module('common.directives')
  .directive('keCollapse', ['$window',
    function ($window) {
      return {
        link: function (scope, element) {
          angular.element($window).bind("load resize", function () {
            var width = ($window.innerWidth > 0) ? $window.innerWidth : $window.screen.width;
            width < 768 ? element.addClass('collapse') : element.removeClass('collapse');
          });
        }
      };
    }]);
