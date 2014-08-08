'use strict';

angular.module('inventory.services')
  .factory('orderService', ['$http',
    function ($http) {
      return {
        deliveredOrders: function (orders) {
          return orders.filter(function (o) {
            return o.status === "delivered";
          });
        },

        nextStatus: function (status) {
          if (status ==='ordered') {
            return ['shipped'];
          } else if (status === 'shipped') {
            return ['delivered'];
          } else {
            return [];
          }
        },

        orderParts: function (parts, supplierId, orderedNum, orderedDate, token) {
          var url = '/api/v1/inventoryorders?token=' + token;
          var data = {
            partsId: parts.id,
            supplierId: supplierId,
            orderedNum: orderedNum,
            orderedDate: orderedDate
          };

          return $http.post(url, data);
        },

        editOrder: function (inventoryOrderId, status, statusChangedDate, token) {
          var url = '/api/v1/inventoryorders/' + inventoryOrderId + '?token=' + token;
          var data = {
            status: status,
            statusChangedDate: statusChangedDate
          };

          return $http({
            method: 'patch',
            url: url,
            data: data
          });
        },

        removeOrder: function (inventoryOrderId, token) {
          var url ='/api/v1/inventoryorders/' + inventoryOrderId + '?token=' + token;

          return $http.delete(url);
        }
      };
    }]);
