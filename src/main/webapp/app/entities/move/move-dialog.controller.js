(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .controller('MoveDialogController', MoveDialogController);

    MoveDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Move', 'Game'];

    function MoveDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Move, Game) {
        var vm = this;

        vm.move = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.games = Game.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.move.id !== null) {
                Move.update(vm.move, onSaveSuccess, onSaveError);
            } else {
                Move.save(vm.move, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('hipTttApp:moveUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.timestamp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
