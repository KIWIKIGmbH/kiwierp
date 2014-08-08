'use strict';

angular.module('authentication.controllers')
  .controller('LoginCtrl', ['$rootScope', '$scope', '$location', 'authenticationService',
    function ($rootScope, $scope, $location, authenticationService) {
      authenticationService.logout();

      $scope.login = function () {
        $rootScope.isLoggedIn = false;

        authenticationService.login($scope.name, $scope.password)
          .success(function () {
            $rootScope.isLoggedIn = true;
            $location.path('/');
          })
          .error(function () {
            $scope.loginFailedAlert = true;
          });
      };
    }]);