'use strict';

angular.module('inventory.services', ['common.services', 'ngRoute']);

angular.module('inventory.controllers', ['common.services']);

angular.module('inventory.directives', []);

angular.module('inventory', [
  'inventory.services',
  'inventory.directives',
  'inventory.controllers'
])
  .config(['$routeProvider', 'indexProvider', 'productProvider', 'suppliersProvider',
    function ($routeProvider, indexProvider, productProvider, suppliersProvider) {
      $routeProvider
        .when('/inventories', {
          templateUrl: '/views/inventories/index.html',
          controller: 'InventoryIndexCtrl',
          resolve: {
            checkAuthorized: indexProvider.checkAuthorized
          }
        })
        .when('/inventories/suppliers', {
          templateUrl: '/views/inventories/suppliers.html',
          controller: 'SuppliersCtrl',
          resolve: {
            suppliers: suppliersProvider.suppliers
          }
        })
        .when('/inventories/:productId', {
          templateUrl: '/views/inventories/product.html',
          controller: 'ProductsCtrl',
          resolve: {
            product: productProvider.product
          }
        });
    }]);
