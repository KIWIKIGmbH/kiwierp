'use strict;'

angular.module('common.services')
  .factory('httpInterceptor', ['$q', '$rootScope', '$location',
    function ($q, $rootScope, $location) {
      return {
        responseError: function (response) {
          if (response.status === 401) {
            $rootScope.isLoggedIn = false;
            $location.path('/login');
          }

          return $q.reject(response);
        }
      };
  }]);
