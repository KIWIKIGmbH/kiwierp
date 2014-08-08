'use strict';

angular.module('inventory.services')
  .factory('supplierService', ['$http', '$modal',
    function ($http, $modal) {
      return {
        suppliers: function (token) {
          var url = '/api/v1/suppliers?token=' + token;
          return $http.get(url);
        },

        addSupplier: function (companyName, personalName, phoneNumber, token) {
          var url = '/api/v1/suppliers?token=' + token;
          var data = {
            companyName: companyName,
            personalName: personalName,
            phoneNumber: phoneNumber
          };

          return $http.post(url, data);
        },

        editSupplier: function (supplierId, companyName, personalName, phoneNumber, token) {
          var url ='/api/v1/suppliers/' + supplierId + '?token=' + token;
          var data = {
            companyName: companyName,
            personalName: personalName,
            phoneNumber: phoneNumber
          };

          return $http({
            method: 'patch',
            url: url,
            data: data
          });
        },

        removeSupplier: function (supplierId, token) {
          var url ='/api/v1/suppliers/' + supplierId + '?token=' + token;

          return $http.delete(url);
        },

        openSupplierForm: function (args) {
          return $modal.open(args);
        }
      };
    }]);