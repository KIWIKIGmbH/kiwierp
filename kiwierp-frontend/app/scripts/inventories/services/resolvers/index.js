'use strict';

angular.module('inventory.services')
  .provider('index', {
    index: ['index',
      function (index) {
        return index();
      }],

    $get: ['authorizationService', 'productService',
      function (authorizationService, productService) {
        return function () {
          return authorizationService.authorize()
            .then(function () {
              return productService.search();
            })
            .then(function (data) {
              return data.results;
            });
        };
      }]
  });
