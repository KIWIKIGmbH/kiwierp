'use strict';

angular.module('inventory.controllers')
  .controller('SuppliersCtrl', ['$rootScope', '$scope', '$modal', 'supplierService', 'authorizationService', 'removeAlert',
    function ($rootScope, $scope, $modal, supplierService, authorizationService, removeAlert) {
      $scope.reloadSuppliers = function () {
        supplierService.suppliers($scope.token)
          .success(function (data) {
            $scope.suppliers = data.results;
          });
      };

      $rootScope.$watch('isLoggedIn', function (newVal) {
        if (newVal) {
          $scope.token = authorizationService.token();
          $scope.reloadSuppliers();
        } else {
          $scope.token = null;
          $scope.suppliers = {};
        }
      });

      $scope.openSupplierForm = function () {
        return $modal.open({
          templateUrl: '/views/inventory/modals/supplier-form.html',
          scope: $scope
        });
      };

      $scope.openNewSupplierForm = function () {
        var modalInstance = $scope.openSupplierForm();

        $scope.supplierForm = {};

        $scope.supplierFormHeader = 'Add Supplier';

        $scope.isNewSupplierForm = true;

        $scope.addSupplier = function () {
          supplierService.addSupplier(
            $scope.supplierForm.companyName,
            $scope.supplierForm.personalName,
            $scope.supplierForm.phoneNumber,
            $scope.token
          )
            .success(function (supplier) {
              $scope.reloadSuppliers();
              modalInstance.close();
            });
        };
      };

      $scope.openEditSupplierForm = function (supplier) {
        var modalInstance = $scope.openSupplierForm();

        $scope.supplierForm = {
          companyName: supplier.companyName,
          personalName: supplier.personalName,
          phoneNumber: supplier.phoneNumber
        };

        $scope.supplierFormHeader = 'Edit Supplier';

        $scope.isNewSupplierForm = false;

        $scope.removeSupplierAlert = function () {
          var removeModalInstance = removeAlert.openRemoveAlert($scope);

          $scope.removeAlertMessage = 'Are you sure to remove the supplier?';

          modalInstance.close();

          $scope.removeAlertFunc = function () {
            $scope.removeSupplier();
          };

          $scope.removeSupplier = function () {
            supplierService.removeSupplier(supplier.id, $scope.token)
              .success(function () {
                $scope.reloadSuppliers();
                removeModalInstance.close();
              });
          };
        };

        $scope.editSupplier = function () {
          supplierService.editSupplier(
            supplier.id,
            $scope.supplierForm.companyName,
            $scope.supplierForm.personalName,
            $scope.supplierForm.phoneNumber,
            $scope.token
          )
            .success(function () {
              $scope.reloadSuppliers();
              modalInstance.close();
            });
        };
      };
    }]);
