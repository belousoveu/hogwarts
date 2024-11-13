package com.github.belousoveu.hogwarts.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String name;
    private String color;
    @OneToMany(mappedBy = "faculty", fetch = FetchType.EAGER)
    private Set<Student> students;


}
