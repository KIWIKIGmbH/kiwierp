'use strict';

angular.module('authentication.services', ['angularLocalStorage', 'common.services']);

angular.module('authentication.controllers', []);

angular.module('authentication', [
  'authentication.services',
  'authentication.controllers'
])
  .config(['$routeProvider', 'loginProvider',
    function ($routeProvider, loginProvider) {
      $routeProvider
        .when('/login', {
          templateUrl: '/views/authentication/login.html',
          controller: 'LoginCtrl',
          resolve: {
            data: loginProvider.data
          }
        });
    }]);
