'use strict';

angular.module('inventory.controllers')
  .controller('OrderCtrl', ['$scope', '$modal', 'orderService', 'removeAlert',
    function ($scope, $modal, orderService, removeAlert) {
      $scope.deliveredOrders = function (orders) {
        return orderService.deliveredOrders(orders);
      };

      $scope.isDelivered = function (status) {
        return status === "delivered";
      };

      $scope.openEditOrderForm = function (inventoryOrder) {
        var modalInstance = $modal.open({
          templateUrl: '/views/inventory/modals/edit-order-form.html',
          scope: $scope
        });

        $scope.orderForm = {};

        $scope.status = orderService.nextStatus(inventoryOrder.status);

        $scope.removeOrderAlert = function () {
          var removeModalInstance = removeAlert.openRemoveAlert($scope);

          $scope.removeAlertMessage = 'Are you sure to remove the order?';

          modalInstance.close();

          $scope.removeAlertFunc = function () {
            $scope.removeOrder();
          };

          $scope.removeOrder = function () {
            orderService.removeOrder(inventoryOrder.id, $scope.token)
              .success(function () {
                $scope.reloadProduct();
                removeModalInstance.close();
              });
          };
        };

        $scope.editOrder = function () {
          orderService.editOrder(
            inventoryOrder.id,
            $scope.orderForm.status,
            $scope.orderForm.statusChangedDate,
            $scope.token
          )
            .success(function () {
              $scope.reloadProduct();
              modalInstance.close();
            });
        };
      };
    }]);
