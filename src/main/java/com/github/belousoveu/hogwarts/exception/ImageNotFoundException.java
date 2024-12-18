package com.github.belousoveu.hogwarts.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(long studentId) {

        super("Image not found for student Id: " + studentId);
        log.warn((this.getMessage()));
    }
}
