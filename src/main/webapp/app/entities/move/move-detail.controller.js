(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .controller('MoveDetailController', MoveDetailController);

    MoveDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Move', 'Game'];

    function MoveDetailController($scope, $rootScope, $stateParams, previousState, entity, Move, Game) {
        var vm = this;

        vm.move = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('hipTttApp:moveUpdate', function(event, result) {
            vm.move = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
