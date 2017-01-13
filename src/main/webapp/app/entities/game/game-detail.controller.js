(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .controller('GameDetailController', GameDetailController);

    GameDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Game', 'User'];

    function GameDetailController($scope, $rootScope, $stateParams, previousState, entity, Game, User) {
        var vm = this;

        vm.game = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('hipTttApp:gameUpdate', function(event, result) {
            vm.game = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
