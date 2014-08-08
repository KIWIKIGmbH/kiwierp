'use strict';

angular.module('inventory.services', ['common.services']);

angular.module('inventory.directives', []);

angular.module('inventory.controllers', ['common.services']);

angular.module('inventory', [
  'inventory.services',
  'inventory.directives',
  'inventory.controllers'
])
  .config(['$routeProvider',
    function ($routeProvider) {
      $routeProvider
        .when('/inventory/products/:productId', {
          templateUrl: '/views/inventory/products.html',
          controller: 'ProductsCtrl'
        })
        .when('/inventory/suppliers', {
          templateUrl: '/views/inventory/suppliers.html',
          controller: 'SuppliersCtrl'
        });
    }]);
