'use strict';

angular.module('common.services', ['angularLocalStorage']);

angular.module('common.directives', []);

angular.module('common.controllers', ['inventory.services']);

angular.module('common', [
  'common.services',
  'common.directives',
  'common.controllers',
])
  .config(['$routeProvider', '$httpProvider', 'contentType', 'datepickerConfig', 'timepickerConfig',
    function ($routeProvider, $httpProvider, contentType, datepickerConfig, timepickerConfig) {
      $routeProvider
        .when('/', {
          templateUrl: '/views/common/dashboard.html',
          controller: 'DashboardCtrl'
        })
        .otherwise({ redirectTo: '/' });

      $httpProvider.interceptors.push('httpInterceptor');

      $httpProvider.defaults.transformRequest = function (data) {
        return data === undefined ? data : angular.element.param(data);
      };

      $httpProvider.defaults.headers.post['Content-Type'] = contentType;
      $httpProvider.defaults.headers.patch['Content-Type'] = contentType;

      datepickerConfig.showWeeks = false;
      timepickerConfig.showMeridian = false;
    }]);