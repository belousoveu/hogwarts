package com.github.belousoveu.hogwarts.model;

import lombok.Getter;
import lombok.Setter;

public enum Faculty {
    GRYFFINDOR ("Гриффиндор"),
    HUFFLEPUFF ("Пуффендуй"),
    RAVENCLAW ("Когтевран"),
    SLYTHERIN ("Слизерин");

    Faculty(String name) {}

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String color;


}
