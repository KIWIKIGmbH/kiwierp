'use strict';

angular.module('common.services')
  .factory('apiService', ['$http', 'endpointRoot', 'accessTokenService', 'redirectService', 'errorHandleService',
    function ($http, endpointRoot, accessTokenService, redirectService, errorHandleService) {
      var req = function (resource, method, data) {
        var token = accessTokenService.token();

        if (!token) {
          return redirectService.toLoginPage();
        }

        return $http({
          url: endpointRoot + resource + '?access_token=' + token,
          method: method,
          data: data
        })
          .then(function (res) {
            return res.data;
          }, function (error) {
            return errorHandleService(error);
          });
      };

      return {
        'get': function (resource) {
          return req(resource, 'GET', {});
        },

        'post': function (resource, data) {
          return req(resource, 'POST', data);
        },

        'patch': function (resource, data) {
          return req(resource, 'PATCH', data);
        },

        'delete': function (resource) {
          return req(resource, 'DELETE', {});
        }
      };
    }]);
