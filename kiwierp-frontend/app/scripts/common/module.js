'use strict';

angular.module('common.services', ['angularLocalStorage']);

angular.module('common.directives', []);

angular.module('common', [
  'common.services',
  'common.directives',
])
  .config(['$locationProvider', '$routeProvider', '$httpProvider', 'contentType', 'datepickerConfig', 'timepickerConfig',
    function ($locationProvider, $routeProvider, $httpProvider, contentType, datepickerConfig, timepickerConfig) {
      $locationProvider.html5Mode(true);

      $routeProvider.otherwise({ redirectTo: '/inventories' });

      $httpProvider.defaults.transformRequest = function (data) {
        return data === undefined ? data : angular.element.param(data);
      };

      $httpProvider.defaults.headers.post['Content-Type'] = contentType;
      $httpProvider.defaults.headers.patch['Content-Type'] = contentType;

      datepickerConfig.showWeeks = false;
      timepickerConfig.showMeridian = false;
    }]);
