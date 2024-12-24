package com.github.belousoveu.hogwarts.exception;

public class FileSavingException extends RuntimeException {
    public FileSavingException(Throwable cause) {

        super(cause.getMessage());
    }
}
