(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('move', {
            parent: 'entity',
            url: '/move',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'hipTttApp.move.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/move/moves.html',
                    controller: 'MoveController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('move');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('move-detail', {
            parent: 'entity',
            url: '/move/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'hipTttApp.move.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/move/move-detail.html',
                    controller: 'MoveDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('move');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Move', function($stateParams, Move) {
                    return Move.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'move',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('move-detail.edit', {
            parent: 'move-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/move/move-dialog.html',
                    controller: 'MoveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Move', function(Move) {
                            return Move.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('move.new', {
            parent: 'move',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/move/move-dialog.html',
                    controller: 'MoveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                timestamp: null,
                                row: null,
                                col: null,
                                player: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('move', null, { reload: 'move' });
                }, function() {
                    $state.go('move');
                });
            }]
        })
        .state('move.edit', {
            parent: 'move',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/move/move-dialog.html',
                    controller: 'MoveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Move', function(Move) {
                            return Move.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('move', null, { reload: 'move' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('move.delete', {
            parent: 'move',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/move/move-delete-dialog.html',
                    controller: 'MoveDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Move', function(Move) {
                            return Move.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('move', null, { reload: 'move' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
