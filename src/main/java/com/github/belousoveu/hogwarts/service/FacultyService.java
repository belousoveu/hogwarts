package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import jakarta.transaction.Transactional;

import java.util.Collection;

public interface FacultyService {
    Collection<Faculty> getFaculties();

    Collection<Faculty> findFacultiesByColor(String color);

    FacultyDto getFaculty(int id);

    FacultyDto getFaculty(String name);

    @Transactional
    Faculty updateFaculty(int id, FacultyDto dto);

    Collection<Faculty> findFaculty(String name, String color);

    void deleteFaculty(int id);

    @Transactional
    Faculty addFaculty(FacultyDto dto);

//    Collection<Student> getFacultyStudents(int id);
}
