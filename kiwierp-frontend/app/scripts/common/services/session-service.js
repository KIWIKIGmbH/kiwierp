'use strict';

angular.module('common.services')
  .factory('sessionService', ['apiService',
    function (apiService) {
      var baseResource = '/sessions';

      return {
        show: function () {
          return apiService.get(baseResource);
        }
      };
    }]);
