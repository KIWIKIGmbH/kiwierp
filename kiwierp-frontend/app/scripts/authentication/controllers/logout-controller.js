'use strict';

angular.module('authentication.controllers')
  .controller('LogoutCtrl', ['$scope', 'authenticationService',
    function ($scope, authenticationService) {
      $scope.logout = function () {
        authenticationService.logout();
      };
    }]);
