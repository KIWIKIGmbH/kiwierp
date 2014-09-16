'use strict';

angular.module('inventory.controllers')
  .controller('PartsCtrl', ['$scope', '$modal', 'partsService', 'inventoryService', 'orderService', 'supplierService', 'removeAlert',
    function ($scope, $modal, partsService, inventoryService, orderService, supplierService, removeAlert) {
      $scope.sumQuantity = function (results) {
        var quantitySum = 0;
        results.forEach(function (r) {
          quantitySum += r.quantity;
        });
        return quantitySum;
      };

      $scope.openEditPartsForm = function (parts) {
        var modalInstance = partsService.openPartsForm($scope);

        $scope.partsForm = {
          name: parts.name,
          description: parts.description,
          neededQuantity: parts.neededQuantity
        };

        $scope.partsFormHeader = 'Edit Parts';

        $scope.isNewPartsForm = false;

        $scope.removePartsAlert = function () {
          var removeModalInstance = removeAlert.openRemoveAlert($scope);

          $scope.removeAlertMessage = 'Are you sure to remove the parts?';

          modalInstance.close();

          $scope.removeAlertFunc = function () {
            $scope.removeParts();
          };

          $scope.removeParts = function () {
            partsService.removeParts(parts.id, $scope.token)
              .success(function () {
                $scope.reloadProduct();
                removeModalInstance.close();
              });
          };
        };

        $scope.editParts = function () {
          partsService.editParts(
            parts.id,
            $scope.partsForm.name,
            $scope.partsForm.description,
            $scope.partsForm.neededQuantity,
            $scope.token
          )
            .success(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };

      $scope.openInventoryForm = function () {
        return $modal.open({
          templateUrl: '/views/inventory/modals/inventory-form.html',
          scope: $scope
        });
      };

      $scope.openNewInventoryForm = function (parts) {
        var modalInstance = $scope.openInventoryForm();

        $scope.inventoryForm = {};

        $scope.inventoryFormHeader = 'Add Inventory';

        $scope.isNewInventoryForm = true;

        $scope.addInventory = function () {
          inventoryService.addInventory(
            parts.id,
            $scope.inventoryForm.description,
            $scope.inventoryForm.quantity,
            $scope.token
          )
            .success(function (inventory) {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };

      $scope.openOrderPartsForm = function (parts) {
        var modalInstance = $modal.open({
          templateUrl: '/views/inventory/modals/new-order-form.html',
          scope: $scope
        });

        supplierService.suppliers($scope.token)
          .success(function (data) {
            $scope.suppliers = data.results;
          });

        $scope.parts = parts;

        $scope.orderForm = {};

        $scope.orderParts = function () {

          orderService.orderParts(
            $scope.parts,
            $scope.orderForm.supplierId,
            $scope.orderForm.quantity,
            $scope.orderForm.orderedDate,
            $scope.token
          )
            .success(function (inventoryOrder) {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };

      $scope.openClassifyPartsForm = function (parts) {
        var modalInstance = $modal.open({
          templateUrl: '/views/inventory/modals/classify-parts-form.html',
          scope: $scope
        });

        $scope.parts = parts;

        $scope.inventories = parts.inventories;

        $scope.classifyForm = {};

        $scope.classifyParts = function () {
          partsService.classifyParts(
            $scope.parts.id,
            $scope.classifyForm.classifiedQuantity,
            $scope.classifyForm.inventoryId,
            $scope.classifyForm.inventoryDescription,
            $scope.token
          )
            .success(function (data) {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };
    }]);
