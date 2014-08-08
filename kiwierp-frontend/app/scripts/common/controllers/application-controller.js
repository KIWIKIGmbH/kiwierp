'use strict';

angular.module('common.controllers')
  .controller('ApplicationCtrl', ['authorizationService',
    function (authorizationService) {
      // TODO
      authorizationService.isLoggedIn();
    }]);
