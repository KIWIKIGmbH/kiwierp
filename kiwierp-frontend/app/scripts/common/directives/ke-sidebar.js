'use strict';

angular.module('common.directives')
  .directive('keSidebar', ['$rootScope', function ($rootScope) {
    return {
      templateUrl: '/views/common/sidebar.html',
      replace: true,
      link: function (scope, element) {
        $rootScope.$watch('isLoggedIn', function (newVal) {
          newVal ? element.show() : element.hide();
        });
      }
    };
  }]);