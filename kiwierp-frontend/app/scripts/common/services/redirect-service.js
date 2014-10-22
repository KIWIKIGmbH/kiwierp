'use strict';

angular.module('common.services')
  .factory('redirectService', ['$q', '$window',
    function ($q, $window) {
      var to = function (path) {
        $window.location.href = path;
        return $q.reject();
      };

      return {
        toIndexPage: function () {
          return to('/');
        },

        toLoginPage: function () {
          return to('/login');
        }
      };
    }]);
