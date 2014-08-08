'use strict';

angular.module('common.directives')
  .directive('keSideMenu', [function () {
    return {
      link: function (scope, element) {
        element.metisMenu()
      }
    }
  }]);

/*
$(function() {

  $('#side-menu').metisMenu();

});
*/