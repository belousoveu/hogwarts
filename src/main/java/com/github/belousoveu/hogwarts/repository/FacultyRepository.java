package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Collection<Faculty> findByColor(String color);

    Faculty findByName(String name);

    Collection<Faculty> findByNameAndColor(String name, String color);
}
