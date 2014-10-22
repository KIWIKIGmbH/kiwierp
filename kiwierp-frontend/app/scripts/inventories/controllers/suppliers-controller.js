'use strict';

angular.module('inventory.controllers')
  .controller('SuppliersCtrl', ['$scope', 'supplierService', 'formService', 'suppliers',
    function ($scope, supplierService, formService, suppliers) {
      var reloadSuppliers = function () {
        supplierService.search()
          .then(function (data) {
            $scope.suppliers = data.results;
          });
      };

      $scope.suppliers = suppliers;

      $scope.openAddSupplierForm = function () {
        var modalInstance = null;

        $scope.supplierFormInputs = {};

        $scope.supplierFormHeader = 'Add Supplier';

        $scope.isAddSupplierForm = true;

        $scope.addSupplier = function () {
          supplierService.add($scope.supplierFormInputs)
            .then(function () {
              reloadSuppliers();
              modalInstance.close();
            });
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/supplier-form.html',
          scope: $scope
        });
      };

      $scope.openEditSupplierForm = function (supplier) {
        var modalInstance = null;

        $scope.supplierFormInputs = {
          id: supplier.id,
          companyName: supplier.companyName,
          personalName: supplier.personalName,
          phoneNumber: supplier.phoneNumber
        };

        $scope.supplierFormHeader = 'Edit Supplier';

        $scope.isAddSupplierForm = false;

        $scope.editSupplier = function () {
          supplierService.edit($scope.supplierFormInputs)
            .then(function () {
              reloadSuppliers();
              modalInstance.close();
            });
        };

        $scope.removeSupplierAlert = function () {
          var removeAlertInstance = null;

          modalInstance.close();

          $scope.removeAlertMessage = 'Are you sure to remove the supplier?';

          $scope.removeAlertFunc = function () {
            supplierService.remove($scope.supplierFormInputs.id)
              .then(function () {
                reloadSuppliers();
              removeAlertInstance.close();
            });
          };

          removeAlertInstance = formService.openRemoveAlert($scope);
        };

        modalInstance = formService.openForm({
          templateUrl: '/views/inventories/modals/supplier-form.html',
          scope: $scope
        });
      };

    }]);
