'use strict';

angular.module('common.controllers')
  .controller('DashboardCtrl', ['$rootScope', '$location',
    function ($rootScope, $location) {
      // TODO
      $rootScope.$watch('isLoggedIn', function (newVal) {
        if (!newVal) {
          $location.path('/login');
        }
      });
    }]);