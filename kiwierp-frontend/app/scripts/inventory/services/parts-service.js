'use strict';

angular.module('inventory.services')
  .factory('partsService', ['$http', '$modal',
    function ($http, $modal) {
      var service = {
        openPartsForm: function (scope) {
          return $modal.open({
            templateUrl: '/views/inventory/modals/parts-form.html',
            scope: scope
          });
        },

        openNewPartsForm: function (scope, productId) {
          var modalInstance = service.openPartsForm(scope);

          scope.partsForm = {};

          scope.partsFormHeader = 'Add Parts';

          scope.isNewPartsForm = true;

          scope.addParts = function () {
            service.addParts(
              productId,
              scope.partsForm.name,
              scope.partsForm.description,
              scope.partsForm.neededQuantity,
              scope.token
            )
              .success(function () {
                modalInstance.close();
              });
          };
        },

        addParts: function (productId, name, description, neededQuantity, token) {
          var url = '/api/v1/parts?token=' + token;
          var data = {
            productId: productId,
            name: name,
            description: description,
            neededQuantity: neededQuantity
          };

          return $http.post(url, data);
        },

        editParts: function (partsId, name, description, neededQuantity, token) {
          var url = '/api/v1/parts/' + partsId + '?token=' + token;
          var data = {
            name: name,
            description: description,
            neededQuantity: neededQuantity
          };

          return $http({
            method: 'patch',
            url: url,
            data: data
          });
        },

        removeParts: function (partsId, token) {
          var url ='/api/v1/parts/' + partsId + '?token=' + token;

          return $http.delete(url);
        },

        classifyParts: function (partsId, classifiedQuantity, inventoryId, inventoryDescription, token) {
          var url = '/api/v1/parts/' + partsId + '/classification?token=' + token;
          var data = {
            classifiedQuantity: classifiedQuantity,
            inventoryId: inventoryId,
            inventoryDescription: inventoryDescription
          };

          return $http.post(url, data);
        }
      };

      return service;
    }]);
