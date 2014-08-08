'use strict';

angular.module('common.controllers')
  .controller('DashboardCtrl', ['$scope', 'productService', 'authorizationService',
    function ($scope, productService, authorizationService) {
      // TODO
      $scope.token = authorizationService.token();

      productService.products($scope.token)
        .success(function (data) {
          $scope.dashboardProducts = data.results;
        });
    }]);
