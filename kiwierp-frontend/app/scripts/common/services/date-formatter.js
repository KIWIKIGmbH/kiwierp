'use strict';

angular.module('common.services')
  .factory('dateFormatter', [
    function () {
      return {
        format: function (date) {
          return moment(date).format('YYYY-MM-DD HH:mm');
        }
      };
    }]);