'use strict';

angular.module('inventory.services')
  .provider('index', {
    checkAuthorized: ['index',
      function (index) {
        return index();
      }],

    $get: ['authorizationService',
      function (authorizationService) {
        return function () {
          return authorizationService.authorize();
        };
      }]
  });
