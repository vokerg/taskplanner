const app = angular.module('taskplannerUi', []);

app.controller('TaskplannerController', ['$http', function TaskplannerController($http) {
    const vm = this;

    vm.projects = [];
    vm.tasks = [];
    vm.projectsLoading = false;
    vm.tasksLoading = false;
    vm.errorMessage = '';
    vm.selectedProjectId = '';
    vm.selectedProjectTitle = '';
    vm.filters = {
        status: '',
        priority: '',
        dueDateAfter: '',
        dueDateBefore: '',
        sortBy: ''
    };

    vm.loadProjects = function loadProjects() {
        vm.projectsLoading = true;
        vm.errorMessage = '';

        $http.get('/api/projects').then(function(response) {
            vm.projects = response.data;
            vm.projectsLoading = false;

            if (vm.projects.length && !vm.selectedProjectId) {
                vm.selectProject(vm.projects[0].id);
            }
        }).catch(function() {
            vm.projectsLoading = false;
            vm.errorMessage = 'Could not load projects from /api/projects.';
        });
    };

    vm.selectProject = function selectProject(projectId) {
        const project = vm.projects.find(function(item) {
            return item.id === projectId;
        });

        vm.selectedProjectId = projectId;
        vm.selectedProjectTitle = project ? project.title : '';
        vm.refreshTasks();
    };

    vm.resetFilters = function resetFilters() {
        vm.filters = {
            status: '',
            priority: '',
            dueDateAfter: '',
            dueDateBefore: '',
            sortBy: ''
        };
        vm.refreshTasks();
    };

    vm.refreshTasks = function refreshTasks() {
        if (!vm.selectedProjectId) {
            return;
        }

        const params = [];

        if (vm.filters.status) {
            params.push('status=' + encodeURIComponent(vm.filters.status));
        }
        if (vm.filters.priority) {
            params.push('priority=' + encodeURIComponent(vm.filters.priority));
        }
        if (vm.filters.dueDateAfter) {
            params.push('dueDateAfter=' + encodeURIComponent(vm.toInstant(vm.filters.dueDateAfter)));
        }
        if (vm.filters.dueDateBefore) {
            params.push('dueDateBefore=' + encodeURIComponent(vm.toInstant(vm.filters.dueDateBefore)));
        }
        if (vm.filters.sortBy) {
            params.push('sortBy=' + encodeURIComponent(vm.filters.sortBy));
        }

        const query = params.length ? '?' + params.join('&') : '';
        const url = '/api/projects/' + vm.selectedProjectId + '/tasks' + query;

        vm.tasksLoading = true;
        vm.errorMessage = '';

        $http.get(url).then(function(response) {
            vm.tasks = response.data;
            vm.tasksLoading = false;
        }).catch(function() {
            vm.tasksLoading = false;
            vm.errorMessage = 'Could not load tasks. Check your filters and API state.';
        });
    };

    vm.toInstant = function toInstant(localDateTime) {
        return new Date(localDateTime).toISOString();
    };

    vm.formatDate = function formatDate(value) {
        if (!value) {
            return '';
        }

        return new Date(value).toLocaleString();
    };

    vm.statusBadgeClass = function statusBadgeClass(status) {
        if (status === 'DONE') {
            return 'done';
        }
        if (status === 'IN_PROGRESS') {
            return 'progress';
        }
        return 'todo';
    };

    vm.priorityBadgeClass = function priorityBadgeClass(priority) {
        if (priority === 'HIGH') {
            return 'priority-high';
        }
        if (priority === 'MEDIUM') {
            return 'priority-medium';
        }
        return 'priority-low';
    };

    vm.loadProjects();
}]);
