'use strict';

angular.module('common.services')
  .factory('formatDate', [
    function () {
      return function (date) {
        return moment(date).format('YYYY-MM-DD HH:mm');
      };
    }]);
