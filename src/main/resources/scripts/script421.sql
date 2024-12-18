create table if not exists public.students
(
    id         bigint generated by default as identity
        primary key,
    age        integer not null check ( age >= 11 and age <= 18 ) default 11,
    name       varchar(255) not null,
    surname    varchar(255) not null,
    faculty_id integer
        constraint fkjy3uttpwfpb0s83e2pvpatg9j
            references public.faculties,
    avatar_id  bigint
        constraint ukrf2wov1e3uww63imid0paoo3r
            unique
        constraint fkaw1ddp6krgxjis7t4i5h38d4d
            references public.avatars
);

alter table public.students
    add constraint full_name_unique unique(name, surname),
    owner to root;

create table if not exists public.faculties
(
    id    integer generated by default as identity
        primary key,
    color varchar(255),
    name  varchar(255) not null
        constraint uk4u63apqkwoe8yh153mwyq93f5
            unique
);

alter table public.faculties
    add constraint name_color_unique unique (name, color),
    owner to root;

