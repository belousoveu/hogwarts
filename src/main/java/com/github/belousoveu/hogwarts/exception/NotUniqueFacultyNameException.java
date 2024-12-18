package com.github.belousoveu.hogwarts.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotUniqueFacultyNameException extends RuntimeException {
    public NotUniqueFacultyNameException(String message) {

        super("Faculty name " + message + " is already exist");
        log.warn((this.getMessage()));
    }
}
