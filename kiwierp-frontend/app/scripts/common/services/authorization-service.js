'use strict';

angular.module('common.services')
  .factory('authorizationService', ['userService', 'accessTokenService', 'redirectService',
    function (userService, accessTokenService, redirectService) {
      return {
        authorize: function () {
          var userId = accessTokenService.userId();

          return !!userId ? userService.show(userId) : redirectService.toLoginPage();
        }
      };
    }]);
