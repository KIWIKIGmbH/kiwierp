'use strict';

angular.module('inventory.services')
  .factory('productService', ['$location', 'apiService',
    function ($location, apiService) {
      var baseResource = '/products';

      return {
        search: function () {
          return apiService.get(baseResource);
        },

        show: function (id) {
          var resource = baseResource + '/' + id;

          return !!id ? apiService.get(resource) : $location.path('/');
        },

        add: function (product) {
          var data = {
            name: product.name,
            description: product.description
          };

          return apiService.post(baseResource, data);
        },

        edit: function (product) {
          var resource = baseResource + '/' + product.id;
          var data = {
            name: product.name,
            description: product.description
          };

          return apiService.patch(resource, data);
        },

        remove: function (id) {
          var resource = baseResource + '/' + id;

          return apiService.delete(resource);
        }
      };
    }]);
