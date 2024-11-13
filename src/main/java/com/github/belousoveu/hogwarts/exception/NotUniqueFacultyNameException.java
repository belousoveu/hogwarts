package com.github.belousoveu.hogwarts.exception;

public class NotUniqueFacultyNameException extends RuntimeException {
    public NotUniqueFacultyNameException(String message) {

        super("Faculty name " + message + " is already exist");
    }
}
