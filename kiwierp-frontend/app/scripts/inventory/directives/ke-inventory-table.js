'use strict';

angular.module('inventory.directives')
  .directive('keInventoryTable', [function () {
    return {
      templateUrl: '/views/inventory/inventory-table.html',
      replace: true
    };
  }]);