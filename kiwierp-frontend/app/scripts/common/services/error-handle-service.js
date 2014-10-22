'use strict';

angular.module('common.services')
  .factory('errorHandleService', ['$q', '$location', 'accessTokenService', 'redirectService',
    function ($q, $location, accessTokenService, redirectService) {
      return function (error) {
        var deferred = $q.defer();

        switch (error.status) {
          case 400:
            deferred.reject('Something wrong with your input(s).');
            break;
          case 401:
            accessTokenService.remove();

            if ($location.path() !== '/login') {
              return redirectService.toLoginPage();
            }

            deferred.reject();
            break;
          case 403:
            // TODO
            break;
          case 500:
            // TODO
            break;
          default:
            // TODO
            break;
        }

        return deferred.promise;
      };
    }]);
