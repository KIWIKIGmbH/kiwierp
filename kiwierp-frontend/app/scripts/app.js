'use strict';

angular.module('app', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.bootstrap',

  'common',
  'authentication',
  'inventory'
])
  .config(['$locationProvider',
    function ($locationProvider) {
      $locationProvider.html5Mode(true);
    }]);

