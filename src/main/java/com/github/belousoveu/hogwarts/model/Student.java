package com.github.belousoveu.hogwarts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {
    private long id;
    private String name;
    private String surname;
    private int age;
    private Faculty faculty;

}
