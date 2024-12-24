package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;
    private final FacultyMapper facultyMapper;

    public FacultyController(FacultyService facultyService, FacultyMapper facultyMapper) {
        this.facultyService = facultyService;
        this.facultyMapper = facultyMapper;
    }

    @GetMapping()
    public Collection<FacultyDto> getFacultyList() {
        return facultyService.getFaculties().stream().map(facultyMapper::toDto).toList();
    }

    @PostMapping("/add")
    public FacultyDto addFaculty(@Valid @RequestBody FacultyDto dto) {
        return facultyMapper.toDto(facultyService.addFaculty(dto));
    }

    @GetMapping("/{id}")
    public FacultyDto getFacultyById(@PathVariable int id) {
        return facultyMapper.toDto(facultyService.getFaculty(id));
    }

    @GetMapping("/{id}/students")
    public Collection<StudentDto> getFacultyStudents(@PathVariable int id) {
        return facultyService.getFacultyStudents(id);
    }

    @PutMapping("/{id}")
    public FacultyDto updateFaculty(@PathVariable int id, @Valid @RequestBody FacultyDto dto) {
        return facultyMapper.toDto(facultyService.updateFaculty(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteFaculty(@PathVariable int id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.status(204).body(id);
    }


    @GetMapping("/search")
    public Collection<FacultyDto> getFilteredFaculty(@RequestParam(required = false) String name, @RequestParam(required = false) String color) {
        return facultyService.findFaculty(name, color).stream().map(facultyMapper::toDto).toList();
    }

    @GetMapping("/longest_name")
    public String getLongestName() {
        return facultyService.getLongestName();
    }


}
