package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.model.Faculty;
import com.github.belousoveu.hogwarts.service.FacultyService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/list")
    public Collection<Faculty> getFacultyList() {
        return facultyService.getFaculties();
    }

    @GetMapping("/{id}")
    public Faculty getFacultyById(@PathVariable int id) {
        return facultyService.getFaculty(id);
    }


    @GetMapping("/search")
    public Collection<Faculty> getFilteredFaculty(@RequestParam(required = false) String name, @RequestParam(required = false) String color) {
        return facultyService.findFaculty(name, color);
    }

    @PutMapping("/{id}")
    public Faculty updateFaculty(@PathVariable int id, @RequestBody String color) {
        return facultyService.updateFaculty(id, color);
    }


}
