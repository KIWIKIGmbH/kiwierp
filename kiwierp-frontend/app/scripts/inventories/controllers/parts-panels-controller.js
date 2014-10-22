'use strict';

angular.module('inventory.controllers')
  .controller('PartsPanelsCtrl', ['$scope', 'partsService', 'inventoryService', 'orderService', 'supplierService', 'formService',
    function ($scope, partsService, inventoryService, orderService, supplierService, formService) {
      $scope.totalQuantity = function (results) {
        var quantitySum = 0;

        results.forEach(function (result) {
          quantitySum += result.quantity;
        });

        return quantitySum;
      };

      $scope.openEditPartsForm = function (parts) {
        var modalInstance = null;

        $scope.partsFormInputs = {
          id: parts.id,
          name: parts.name,
          description: parts.description,
          neededQuantity: parts.neededQuantity
        };

        $scope.partsFormHeader = 'Edit Parts';

        $scope.isAddPartsForm = false;

        $scope.editParts = function () {
          partsService.edit($scope.partsFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        $scope.removePartsAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the parts?';

          $scope.removeAlertFunc = function () {
            partsService.remove($scope.partsFormInputs.id)
              .then(function () {
                $scope.reloadProduct();
                removeAlertInstance.close();
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/parts-form.html',
          scope: $scope
        });
      };

      $scope.openAddInventoryForm = function (parts) {
        var modalInstance = null;

        $scope.inventoryFormInputs = {
          partsId: parts.id
        };

        $scope.inventoryFormHeader = 'Add Inventory';

        $scope.isAddInventoryForm = true;

        $scope.addInventory = function () {
          inventoryService.add($scope.inventoryFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/inventory-form.html',
          scope: $scope
        });
      };

      $scope.openAddOrderForm = function (parts) {
        var modalInstance = null;

        supplierService.search()
          .then(function (data) {
            $scope.suppliers = data.results;
          });

        $scope.orderFormInputs = {
          partsId: parts.id,
          orderedDate: new Date()
        };

        $scope.addOrder = function () {
          orderService.add($scope.orderFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/add-order-form.html',
          scope: $scope
        });
      };

      $scope.openClassifyPartsForm = function (parts) {
        var modalInstance = null;

        $scope.classifyPartsFormInputs = {
          partsId: parts.id
        };

        $scope.parts = parts;

        $scope.inventories = $scope.parts.inventories;

        $scope.classifyParts = function () {
          partsService.classify($scope.classifyPartsFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/classify-parts-form.html',
          scope: $scope
        });
      };
    }]);
