(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .controller('MoveDeleteController',MoveDeleteController);

    MoveDeleteController.$inject = ['$uibModalInstance', 'entity', 'Move'];

    function MoveDeleteController($uibModalInstance, entity, Move) {
        var vm = this;

        vm.move = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Move.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
