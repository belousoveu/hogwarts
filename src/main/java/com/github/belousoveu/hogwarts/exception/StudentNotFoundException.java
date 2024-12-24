package com.github.belousoveu.hogwarts.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(long id) {
        super("Student with id " + id + " not found");
        log.warn((this.getMessage()));
    }
}
