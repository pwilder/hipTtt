(function() {
    'use strict';

    angular
        .module('hipTttApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('playArea', {
            parent: 'app',
            url: '/playArea',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/playArea/playArea.html',
                    controller: 'PlayAreaController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('playArea');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
