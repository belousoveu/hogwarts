package com.github.belousoveu.hogwarts.service;

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
        return Faculty.values()[id];
    }

    public Faculty getFaculty(String name) {
        return Faculty.valueOf(name);
    }

    public Faculty updateFaculty(int id, String color) {
        Faculty.values()[id].setColor(color);
        return Faculty.values()[id];
    }
}
