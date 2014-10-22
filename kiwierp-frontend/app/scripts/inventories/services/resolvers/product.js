'use strict';

angular.module('inventory.services')
  .provider('product', {
    product: ['product',
      function (product) {
        return product();
      }],

    $get: ['$location', 'authorizationService', 'productService',
      function ($location, authorizationService, productService) {
        return function () {
          return authorizationService.authorize()
            .then(function () {
              var productId = $location.path().split('/').pop();

              return productService.show(productId);
            });
        };
      }]
  });
