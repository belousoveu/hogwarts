package com.github.belousoveu.hogwarts.exception;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(long studentId) {

        super("Image not found for student Id: " + studentId);
    }
}
