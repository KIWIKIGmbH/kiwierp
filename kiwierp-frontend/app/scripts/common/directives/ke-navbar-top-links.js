'use strict';

angular.module('common.directives')
  .directive('keNavbarTopLinks', ['$rootScope', function ($rootScope) {
    return {
      templateUrl: '/views/common/navbar-top-links.html',
      replace: true,
      link: function (scope, element) {
        $rootScope.$watch('isLoggedIn', function (newVal) {
          newVal ? element.show() : element.hide();
        });
      }
    };
  }]);