'use strict';

angular.module('inventory.services')
  .factory('partsService', ['apiService',
    function (apiService) {
      var baseResource = '/parts';

      return {
        add: function (parts) {
          var data = {
            productId: parts.productId,
            name: parts.name,
            description: parts.description,
            neededQuantity: parts.neededQuantity
          };

          return apiService.post(baseResource, data);
        },

        edit: function (parts) {
          var resource = baseResource + '/' + parts.id;
          var data = {
            name: parts.name,
            description: parts.description,
            neededQuantity: parts.neededQuantity
          };

          return apiService.patch(resource, data);
        },

        remove: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.delete(resource);
        },

        classify: function (classification) {
          var resource = baseResource + '/' + classification.partsId + '/classification';
          var data = {
            classifiedQuantity: classification.classifiedQuantity,
            inventoryId: classification.inventoryId,
            inventoryDescription: classification.inventoryDescription
          };

          return apiService.post(resource, data);
        }
      };
    }]);
