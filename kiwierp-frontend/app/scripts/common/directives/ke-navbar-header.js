'use strict';

angular.module('common.directives')
  .directive('keNavbarHeader', [function () {
    return {
      templateUrl: '/views/common/navbar-header.html',
      replace: true
    };
  }]);