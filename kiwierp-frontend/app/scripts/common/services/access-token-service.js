'use strict';

angular.module('common.services')
  .factory('accessTokenService', ['storage',
    function (storage) {
      var key = '__keat';

      var prop = function (propName) {
        var accessToken = storage.get(key);
        var p = null;

        if (!!accessToken) {
          p = accessToken[propName] || storage.remove(key);
        }

        return p;
      };

      return {
        set: function (accessToken) {
          return storage.set(key, accessToken);
        },

        remove: function () {
          return storage.remove(key);
        },

        token: function () {
          return prop('access_token');
        }
     };
    }]);
