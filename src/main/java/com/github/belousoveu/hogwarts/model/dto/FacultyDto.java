package com.github.belousoveu.hogwarts.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyDto {

    private int id;
    @NotEmpty(message = "Name is required")
    private String name;
    private String color;
}
