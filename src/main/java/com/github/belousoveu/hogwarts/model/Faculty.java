package com.github.belousoveu.hogwarts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@NoArgsConstructor
@Entity(name = "faculties")
public class Faculty {
//    GRYFFINDOR("Гриффиндор"),
//    HUFFLEPUFF("Пуффендуй"),
//    RAVENCLAW("Когтевран"),
//    SLYTHERIN("Слизерин");

    @Id
    @GeneratedValue
    private int id;
    @UniqueElements(message = "Faculty name must be unique")
    private String name;
    private String color;


    public Faculty(String name) {
        this.name = name;
    }


}
