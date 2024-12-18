package com.github.belousoveu.hogwarts.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(int facultyId) {
        super("Faculty with id " + facultyId + " not found");
        log.warn((this.getMessage()));
    }

    public FacultyNotFoundException(String facultyName) {
        super("Faculty with name " + facultyName + " not found");
        log.warn((this.getMessage()));
    }
}
