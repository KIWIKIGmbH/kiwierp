'use strict';

angular.module('inventory.directives')
  .directive('kePartsPanels', [function () {
    return {
      templateUrl: '/views/inventory/parts-panels.html',
      replace: true
    };
  }]);