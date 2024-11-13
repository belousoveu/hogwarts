package com.github.belousoveu.hogwarts.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorResponse {
    private final List<Violation> violations;

    public ValidationErrorResponse(List<Violation> violations) {
        this.violations = violations;
    }

    @Getter
    public static class Violation {
        private final String fieldName;
        private final String message;

        public Violation(String fieldName, String message) {
            this.fieldName = fieldName;
            this.message = message;
        }

    }
}