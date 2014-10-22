'use strict';

angular.module('inventory.services')
  .factory('supplierService', ['apiService',
    function (apiService) {
      var baseResource = '/suppliers';

      return {
        search: function () {
          return apiService.get(baseResource);
        },

        add: function (supplier) {
          var data = {
            companyName: supplier.companyName,
            personalName: supplier.personalName,
            phoneNumber: supplier.phoneNumber
          };

          return apiService.post(baseResource, data);
        },

        edit: function (supplier) {
          var resource = baseResource + '/' + supplier.id;
          var data = {
            companyName: supplier.companyName,
            personalName: supplier.personalName,
            phoneNumber: supplier.phoneNumber
          };

          return apiService.patch(resource, data);
        },

        remove: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.delete(resource);
        }
      };
    }]);
