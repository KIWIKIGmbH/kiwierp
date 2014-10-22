'use strict';

angular.module('authentication.services')
  .provider('login', {
    data: ['login',
      function (login) {
        return login();
      }],

    $get: ['accessTokenService', 'userService', 'redirectService',
      function (accessTokenService, userService, redirectService) {
        return function () {
          var userId = accessTokenService.userId();
          var returnNullFunc = function () {
            return null;
          };

          return !!userId ? userService.show(userId).then(redirectService.toIndexPage, returnNullFunc) : null;
        };
      }]
  });
