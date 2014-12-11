'use strict';

angular.module('inventory.services')
  .factory('inventoryService', ['apiService',
    function (apiService) {
      var baseResource = '/inventory-management/components/';
      var subResource = '/inventories';

      return {
        add: function (inventory) {
          var data = {
            description: inventory.description,
            quantity: inventory.quantity
          };

          return apiService.post(baseResource + inventory.componentId + subResource, data);
        },

        edit: function (componentId, inventory) {
          var resource = baseResource + componentId + subResource + '/' + inventory.id;
          var data = {
            description: inventory.description,
            quantity: inventory.quantity
          };

          return apiService.patch(resource, data);
        },

        remove: function (componentId, id) {
          var resource = baseResource + componentId + subResource + '/' + id;

          return apiService.delete(resource);
        }
      };
    }]);
