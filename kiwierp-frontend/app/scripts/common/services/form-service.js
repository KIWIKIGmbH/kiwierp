'use strict';

angular.module('common.services')
  .factory('formService', ['$modal',
    function ($modal) {
      return {
        openForm: function (options) {
          return $modal.open(options);
        },

        openRemoveAlert: function (scope) {
          return $modal.open({
            templateUrl: '/views/common/modals/remove-alert.html',
            scope: scope
          });
        }
      };
    }]);
