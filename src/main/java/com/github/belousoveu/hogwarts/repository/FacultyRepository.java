package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Collection<Faculty> findByColor(String color);

    Optional<Faculty> findByName(String name);

    Collection<Faculty> findByNameOrColor(String name, String color);
}
