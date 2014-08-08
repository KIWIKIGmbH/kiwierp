'use strict';

angular.module('inventory.services')
  .factory('inventoryService', ['$http',
    function ($http) {
      return {
        addInventory: function (partsId, description, quantity, token) {
          var url = '/api/v1/inventories?token=' + token;
          var data = {
            partsId: partsId,
            description: description,
            quantity: quantity
          };

          return $http.post(url, data);
        },

        editInventory: function (inventoryId, description, quantity, token) {
          var url = '/api/v1/inventories/' + inventoryId + '?token=' + token;
          var data = {
            description: description,
            quantity: quantity
          };

          return $http.patch({
            method: 'patch',
            url: url,
            data: data
          });
        },

        removeInventory: function (inventoryId, token) {
          var url ='/api/v1/inventories/' + inventoryId + '?token=' + token;

          return $http.delete(url);
        }
      };
    }]);
