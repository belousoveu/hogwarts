package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler({FacultyNotFoundException.class, StudentNotFoundException.class})
    public ResponseEntity<String> handleFacultyNotFoundException(FacultyNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}