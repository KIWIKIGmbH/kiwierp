'use strict';

angular.module('authentication.services')
  .factory('authenticationService', ['$http', 'endpointRoot', 'redirectService', 'accessTokenService',
    function ($http, endpointRoot, redirectService, accessTokenService) {
      return {
        login: function (name, password) {
          var url = endpointRoot + '/users/authentication';
          var data = {
            name: name,
            password: password
          };

          return $http.post(url, data)
            .success(function (accessToken) {
              accessTokenService.set(accessToken);
              return redirectService.toIndexPage();
            });
        },

        logout: function () {
          accessTokenService.remove();
          return redirectService.toLoginPage();
        }
      };
    }]);
