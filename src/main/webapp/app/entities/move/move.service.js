(function() {
    'use strict';
    angular
        .module('hipTttApp')
        .factory('Move', Move);

    Move.$inject = ['$resource', 'DateUtils'];

    function Move ($resource, DateUtils) {
        var resourceUrl =  'api/moves/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.timestamp = DateUtils.convertDateTimeFromServer(data.timestamp);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
