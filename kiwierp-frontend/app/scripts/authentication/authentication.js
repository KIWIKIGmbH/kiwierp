'use strict';

angular.module('authentication.services', ['angularLocalStorage']);

angular.module('authentication.controllers', []);

angular.module('authentication', [
  'authentication.services',
  'authentication.controllers'
])
  .config(['$routeProvider',
    function ($routeProvider) {
      $routeProvider
        .when('/login', {
          templateUrl: '/views/authentication/login.html',
          controller: 'LoginCtrl'
        })
    }]);
