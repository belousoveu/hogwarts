package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Collection<Faculty> findByColor(String color);

    Optional<Faculty> findByName(String name);

    @Query("SELECT f FROM faculties f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(f.color) LIKE LOWER(CONCAT('%', :color, '%'))")
    Collection<Faculty> findAllByNameOrColor(@Param("name") String name, @Param("color") String color);
}
