(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('replay', {
            parent: 'app',
            url: '/replay',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/replay/replay.html',
                    controller: 'ReplayController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('replay');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
