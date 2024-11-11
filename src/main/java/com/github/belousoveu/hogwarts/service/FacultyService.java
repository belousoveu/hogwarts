package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.model.Faculty;
import com.github.belousoveu.hogwarts.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Collection<Faculty> getFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Faculty getFaculty(int id) {
        return facultyRepository.getReferenceById(id);
    }

    public Faculty getFaculty(String name) {
        return facultyRepository.findByName(name);
    }

    public Faculty updateFaculty(int id, String color) {
        Faculty faculty = getFaculty(id);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> findFaculty(String name, String color) {

        return facultyRepository.findByNameAndColor(name, color);
    }
}
