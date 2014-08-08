'use strict';

angular.module('authentication.services')
  .factory('authenticationService', ['$q', '$http', 'storage',
    function ($q, $http, storage) {
      var key = 'accessToken';

      return {
        login: function (name, password) {
          var url = '/api/v1/users/authentication';
          var data = {
            name: name,
            password: password
          };

          return $http.post(url, data)
            .success(function (response) {
              storage.set(key, response);
            });
        },

        logout: function () {
          storage.remove(key);
        }
      };
    }]);