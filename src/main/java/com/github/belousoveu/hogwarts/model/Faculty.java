package com.github.belousoveu.hogwarts.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Faculty {
    GRYFFINDOR("Гриффиндор"),
    HUFFLEPUFF("Пуффендуй"),
    RAVENCLAW("Когтевран"),
    SLYTHERIN("Слизерин");

    Faculty(String title) {
        this.title = title;
    }

    private final String title;

    @Setter
    private String color;

}
