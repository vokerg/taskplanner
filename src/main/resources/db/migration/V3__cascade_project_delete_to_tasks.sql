alter table tasks drop constraint fk_tasks_project;

alter table tasks
    add constraint fk_tasks_project
    foreign key (project_id) references projects (id)
    on delete cascade;
