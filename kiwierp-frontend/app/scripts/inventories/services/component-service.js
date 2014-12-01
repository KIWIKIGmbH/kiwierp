'use strict';

angular.module('inventory.services')
  .factory('componentService', ['apiService',
    function (apiService) {
      var baseResource = '/inventory-management/components';

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
          var resource = baseResource + '/' + classification.componentId + '/classification';
          var inventoryId = classification.inventoryId;

          if (!!inventoryId) {
            return apiService.post(resource, {
              classifiedQuantity: classification.classifiedQuantity,
              inventoryId: inventoryId
            });
          } else {
            return apiService.post(resource, {
              classifiedQuantity: classification.classifiedQuantity,
              inventoryDescription: classification.inventoryDescription
            });
          }
        }
      };
    }]);
