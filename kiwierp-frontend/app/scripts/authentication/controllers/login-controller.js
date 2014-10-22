'use strict';

angular.module('authentication.controllers')
  .controller('LoginCtrl', ['$scope', 'authenticationService',
    function ($scope, authenticationService) {
      $scope.login = function () {
        return authenticationService.login($scope.name, $scope.password)
          .error(function () {
            $scope.isLoginFailed = true;
          });
      };
    }]);
