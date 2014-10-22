'use strict';

angular.module('common.directives')
  .directive('keNavbar', [
    function () {
      return {
        replace: true,
        templateUrl: '/views/common/navbar.html'
      };
    }]);
