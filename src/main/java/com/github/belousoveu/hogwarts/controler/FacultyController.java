package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping()
    public Collection<Faculty> getFacultyList() {
        return facultyService.getFaculties();
    }

    @PostMapping("/add")
    public Faculty addFaculty(@Valid @RequestBody FacultyDto dto) {
        return facultyService.addFaculty(dto);
    }

    @GetMapping("/{id}")
    public FacultyDto getFacultyById(@PathVariable int id) {
        return facultyService.getFaculty(id);
    }

    @PutMapping("/{id}")
    public Faculty updateFaculty(@PathVariable int id, @Valid @RequestBody FacultyDto dto) {
        return facultyService.updateFaculty(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteFaculty(@PathVariable int id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.status(204).body(id);
    }


    @GetMapping("/search")
    public Collection<Faculty> getFilteredFaculty(@RequestParam(required = false) String name, @RequestParam(required = false) String color) {
        return facultyService.findFaculty(name, color);
    }


}
