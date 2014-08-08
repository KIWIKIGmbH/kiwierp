'use strict';

angular.module('common.services')
  .factory('authorizationService', ['$q', '$rootScope', '$http', '$location', 'storage',
    function ($q, $rootScope, $http, $location, storage) {
      var key = 'accessToken';

      var getAccessToken = function() {
        return storage.get(key);
      };

      var token = function () {
        var accessToken = getAccessToken();

        return !!accessToken ? accessToken.token : null;
      };

      var userId = function () {
        var accessToken = getAccessToken();

        return !!accessToken ? accessToken.userId : null;
      };

      var user = function (token) {
        var uid = userId();

        if (!!uid) {
          var url = '/api/v1/users/' + uid + "?token=" + token;

          return $http.get(url);
        } else {
          return $q.reject();
        }
      };

      return {
        token: function () {
          return token();
        },

        isLoggedIn: function () {
          var t = token();

          if (!!t) {
            return user(t)
              .success(function (user) {
                $rootScope.isLoggedIn = true;
                $rootScope.user = user;
              })
              .error(function () {
                $rootScope.isLoggedIn = false;
                $location.path('/login');
              });
          } else {
            return $q.reject();
          }
        }
      };
    }]);

