-- liquibase formatted sql

-- changeset belousoveu:1
CREATE INDEX full_name_idx ON students (name, surname);

-- changeset belousoveu:2
CREATE INDEX name_color_ind ON faculties (name, color);