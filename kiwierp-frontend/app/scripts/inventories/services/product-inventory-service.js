'use strict';

angular.module('inventory.services')
  .factory('productInventoryService', ['apiService',
    function (apiService) {
      var baseResource = '/inventory-management/products/';
      var subResource = '/inventories';

      return {
        add: function (inventory) {
          var data = {
            description: inventory.description,
            status: inventory.status,
            quantity: inventory.quantity
          };

          return apiService.post(baseResource + inventory.productId + subResource, data);
        },

        edit: function (productId, inventory) {
          var resource = baseResource + productId + subResource + '/' + inventory.id;
          var data = {
            description: inventory.description,
            status: inventory.status,
            quantity: inventory.quantity
          };

          return apiService.patch(resource, data);
        },

        remove: function (productId, id) {
          var resource = baseResource + productId + subResource + '/' + id;

          return apiService.delete(resource);
        }
      }
    }]);
