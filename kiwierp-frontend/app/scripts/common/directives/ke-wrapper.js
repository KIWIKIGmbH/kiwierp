'use strict';

angular.module('common.directives')
  .directive('keWrapper', ['$rootScope', function ($rootScope) {
    return {
      link: function (scope, element) {
        $rootScope.$watch('isLoggedIn', function (newVal) {
          if (newVal) {
            element.removeClass('container');
            element.attr('id', 'page-wrapper');
          } else {
            element.addClass('container');
            element.removeAttr('id');
          }
        });
      }
    };
  }]);
