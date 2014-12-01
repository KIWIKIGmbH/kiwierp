'use strict';

angular.module('authentication.services')
  .factory('authenticationService', ['$http', 'endpointRoot', 'redirectService', 'accessTokenService',
    function ($http, endpointRoot, redirectService, accessTokenService) {
      return {
        login: function (name, password) {
          var url = endpointRoot + '/sessions';
          var data = {
            username: name,
            password: password,
            grant_type: 'password'
          };

          return $http.post(url, angular.element.param(data), {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            }
          })
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
