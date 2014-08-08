'use strict';

angular.module('common.services')
  .factory('removeAlert', ['$modal',
    function ($modal) {
      return {
        openRemoveAlert: function (scope) {
          return $modal.open({
            templateUrl: '/views/common/remove-alert.html',
            scope: scope
          });
        }
      };
    }]);