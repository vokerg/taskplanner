create table projects (
    id varchar(255) not null,
    title varchar(255) not null,
    description varchar(2000),
    created_at timestamp with time zone not null,
    completed boolean not null,
    constraint pk_projects primary key (id)
);

create table tasks (
    id varchar(255) not null,
    title varchar(255) not null,
    description varchar(2000),
    status varchar(255) not null,
    priority varchar(255) not null,
    project_id varchar(255) not null,
    created_at timestamp with time zone not null,
    due_date timestamp with time zone,
    constraint pk_tasks primary key (id),
    constraint fk_tasks_project foreign key (project_id) references projects (id),
    constraint chk_tasks_status check (status in ('TODO', 'IN_PROGRESS', 'DONE')),
    constraint chk_tasks_priority check (priority in ('LOW', 'MEDIUM', 'HIGH'))
);
