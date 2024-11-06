package com.github.belousoveu.hogwarts.exception;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(int facultyId) {
        super("Faculty with id " + facultyId + " not found");
    }
    public FacultyNotFoundException(String facultyName) {
        super("Faculty with name " + facultyName + " not found");
    }
}
