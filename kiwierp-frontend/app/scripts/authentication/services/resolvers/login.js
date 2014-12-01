'use strict';

angular.module('authentication.services')
  .provider('login', {
    data: ['login',
      function (login) {
        return login();
      }],

    $get: ['accessTokenService', 'sessionService', 'redirectService',
      function (accessTokenService, sessionService, redirectService) {
        return function () {
          var returnNullFunc = function () {
            return null;
          };

          return accessTokenService.token() ? sessionService.show().then(redirectService.toIndexPage, returnNullFunc) : null;
        };
      }]
  });
