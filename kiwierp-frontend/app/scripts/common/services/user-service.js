'use strict';

angular.module('common.services')
  .factory('userService', ['apiService',
    function (apiService) {
      var baseResource = '/users';

      return {
        show: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.get(resource);
        }
      };
    }]);
