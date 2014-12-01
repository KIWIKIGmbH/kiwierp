'use strict';

angular.module('inventory.services')
  .factory('orderService', ['formatDate', 'apiService',
    function (formatDate, apiService) {
      var baseResource = '/inventory-management/orders';

      var service = {
        selectDelivered: function (orders) {
          return orders.filter(function (order) {
            return service.isDelivered(order.status);
          });
        },

        isDelivered: function (status) {
          return status === "delivered";
        },

        nextStatusList: function (status) {
          var nextStatusList = {
            ordered: ['shipped'],
            shipped: ['delivered']
          };

          return nextStatusList[status] || [];
        },

        add: function (order) {
          var data = {
            componentId: order.componentId,
            supplierId: order.supplierId,
            quantity: order.quantity,
            orderedDate: formatDate(order.orderedDate)
          };

          return apiService.post(baseResource, data);
        },

        edit: function (order) {
          var resource = baseResource + '/' + order.id;
          var data = {
            status: order.status,
            statusChangedDate: formatDate(order.statusChangedDate)
          };

          return apiService.patch(resource, data);
        },

        remove: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.delete(resource);
        }
      };

      return service;
    }]);
