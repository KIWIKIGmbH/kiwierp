'use strict';

angular.module('inventory.services')
  .factory('inventoryService', ['apiService',
    function (apiService) {
      var baseResource = '/inventories';

      return {
        add: function (inventory) {
          var data = {
            partsId: inventory.partsId,
            description: inventory.description,
            quantity: inventory.quantity
          };

          return apiService.post(baseResource, data);
        },

        edit: function (inventory) {
          var resource = baseResource + '/' + inventory.id;
          var data = {
            description: inventory.description,
            quantity: inventory.quantity
          };

          return apiService.patch(resource, data);
        },

        remove: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.delete(resource);
        }
      };
    }]);
