'use strict';

angular.module('inventory.controllers')
  .controller('ComponentPanelsCtrl', ['$scope', 'componentService', 'inventoryService', 'orderService', 'supplierService', 'formService',
    function ($scope, componentService, inventoryService, orderService, supplierService, formService) {
      $scope.totalQuantity = function (results) {
        var quantitySum = 0;

        results.forEach(function (result) {
          quantitySum += result.quantity;
        });

        return quantitySum;
      };

      $scope.openEditComponentForm = function (component) {
        var modalInstance = null;

        $scope.componentFormInputs = {
          id: component.id,
          name: component.name,
          description: component.description,
          neededQuantity: component.neededQuantity
        };

        $scope.componentFormHeader = 'Edit Component';

        $scope.isAddComponentForm = false;

        $scope.editComponent = function () {
          componentService.edit($scope.componentFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        $scope.removeComponentAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the Component?';

          $scope.removeAlertFunc = function () {
            componentService.remove($scope.componentFormInputs.id)
              .then(function () {
                $scope.reloadProduct();
                removeAlertInstance.close();
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/component-form.html',
          scope: $scope
        });
      };

      $scope.openAddInventoryForm = function (component) {
        var modalInstance = null;

        $scope.inventoryFormInputs = {
          componentId: component.id
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

      $scope.openAddOrderForm = function (component) {
        var modalInstance = null;

        supplierService.search()
          .then(function (data) {
            $scope.suppliers = data.results;
          });

        $scope.orderFormInputs = {
          componentId: component.id,
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

      $scope.openClassifyComponentForm = function (component) {
        var modalInstance = null;

        $scope.classifyComponentFormInputs = {
          componentId: component.id
        };

        $scope.component = component;

        $scope.inventories = $scope.component.inventories;

        $scope.classifyComponent = function () {
          componentService.classify($scope.classifyComponentFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/classify-Component-form.html',
          scope: $scope
        });
      };
    }]);
