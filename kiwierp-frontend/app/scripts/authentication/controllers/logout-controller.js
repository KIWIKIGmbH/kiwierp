'use strict';

angular.module('authentication.controllers')
  .controller('LogoutCtrl', ['$rootScope', '$scope', '$location', 'authenticationService',
    function ($rootScope, $scope, $location, authenticationService) {
    $scope.logout = function () {
      authenticationService.logout();

      $rootScope.isLoggedIn = false;

      $location.path('/login');
    };
  }]);
