'use strict';

angular.module('common.services')
  .factory('authorizationService', ['sessionService', 'accessTokenService', 'redirectService',
    function (sessionService, accessTokenService, redirectService) {
      return {
        authorize: function () {
          var token = accessTokenService.token();

          return !!token ? sessionService.show() : redirectService.toLoginPage();
        }
      };
    }]);
