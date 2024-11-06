package com.github.belousoveu.hogwarts.model.dto;

import com.github.belousoveu.hogwarts.model.Faculty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private long id;
    private String name;
    private String surname;
    @Size(min = 11, max = 18, message = "Invalid age")
    private int age;
    private Faculty faculty;
}
