'use strict';

angular.module('inventory.controllers')
  .controller('OrderTableCtrl', ['$scope', 'orderService', 'formService',
    function ($scope, orderService, formService) {
      $scope.inventoryOrders = $scope.parts.inventoryOrders;

      $scope.deliveredOrders = function (orders) {
        return orderService.selectDelivered(orders);
      };

      $scope.isDelivered = function (order) {
        return orderService.isDelivered(order.status);
      };

      $scope.openEditOrderForm = function (inventoryOrder) {
        var modalInstance = null;

        $scope.orderFormInputs = {
          id: inventoryOrder.id,
          statusChangedDate: new Date()
        };

        $scope.statusList = orderService.nextStatusList(inventoryOrder.status);

        $scope.editOrder = function () {
          orderService.edit($scope.orderFormInputs)
            .then(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };

        $scope.removeOrderAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the order?';

          $scope.removeAlertFunc = function () {
            orderService.remove($scope.orderFormInputs.id)
              .then(function () {
                $scope.reloadProduct();
                removeAlertInstance.close();
              });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/edit-order-form.html',
          scope: $scope
        });
      };
    }]);
