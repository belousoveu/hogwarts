package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler({FacultyNotFoundException.class, StudentNotFoundException.class, ImageNotFoundException.class})
    public ResponseEntity<String> handleFacultyNotFoundException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationErrorResponse.Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorResponse.Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ValidationErrorResponse(violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotUniqueFacultyNameException.class)
    public ResponseEntity<String> handleNotUniqueFacultyNameException(NotUniqueFacultyNameException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


    @ExceptionHandler(FileSavingException.class)
    public ResponseEntity<String> handleFileSavingException(FileSavingException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}