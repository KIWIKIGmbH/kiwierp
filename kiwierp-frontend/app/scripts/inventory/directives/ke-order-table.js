'use strict';

angular.module('inventory.directives')
  .directive('keOrderTable', [function () {
    return {
      templateUrl: '/views/inventory/order-table.html',
      replace: true
    };
  }]);