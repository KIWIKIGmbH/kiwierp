'use strict';

angular.module('common.services', ['angularLocalStorage']);

angular.module('common.directives', []);

angular.module('common', [
  'common.services',
  'common.directives',
])
  .config(['$locationProvider', '$routeProvider', 'datepickerConfig', 'timepickerConfig',
    function ($locationProvider, $routeProvider, datepickerConfig, timepickerConfig) {
      $locationProvider.html5Mode(true);

      $routeProvider.otherwise({ redirectTo: '/inventories' });

      datepickerConfig.showWeeks = false;
      timepickerConfig.showMeridian = false;
    }]);
