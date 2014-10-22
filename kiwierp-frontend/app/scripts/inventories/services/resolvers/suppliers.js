'use strict';

angular.module('inventory.services')
  .provider('suppliers', {
    suppliers: ['suppliers',
      function (suppliers) {
        return suppliers();
      }],

    $get: ['authorizationService', 'supplierService',
      function (authorizationService, supplierService) {
        return function () {
          return authorizationService.authorize()
            .then(function () {
              return supplierService.search();
            })
            .then(function (data) {
              return data.results;
            });
        };
      }]
  });
