package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.model.Faculty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class FacultyService {

    public Collection<Faculty> getFaculties() {
        return Arrays.stream(Faculty.values()).toList();
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        return Arrays.stream(Faculty.values()).filter(faculty -> faculty.getColor().equals(color)).toList();
    }

    public Faculty getFaculty(int id) {
        if (id < 1 || id > Faculty.values().length) {
            throw new FacultyNotFoundException(id);
        }
        return Faculty.values()[id - 1];
    }

    public Faculty getFaculty(String name) {
        return Arrays.stream(Faculty.values())
                .filter(faculty -> faculty.getTitle().equals(name))
                .findFirst()
                .orElseThrow(() -> new FacultyNotFoundException(name));
    }

    public Faculty updateFaculty(int id, String color) {
        Faculty.values()[id].setColor(color);
        return Faculty.values()[id];
    }
}
