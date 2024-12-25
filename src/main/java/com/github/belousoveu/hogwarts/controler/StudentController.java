package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    @GetMapping("/all")
    public Collection<StudentDto> getAllStudents(@RequestParam(name = "faculty_id", required = false) Integer facultyId) {
        if (facultyId == null) {
            return studentService.getAllStudents().stream().map(studentMapper::toDto).toList();
        } else {
            return studentService.findStudentByFaculty(facultyId).stream().map(studentMapper::toDto).toList();
        }
    }

    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable long id) {
        return studentMapper.toDto(studentService.getStudent(id));
    }

    @GetMapping("/filter")
    public Collection<StudentDto> findStudentsByAge(@RequestParam(required = false) List<Integer> age) {
        return studentService.findStudentByAge(age).stream().map(studentMapper::toDto).toList();
    }

    @PostMapping("/add")
    public StudentDto addStudent(@Valid @RequestBody StudentDto dto) {
        return studentMapper.toDto(studentService.addStudent(dto));
    }

    @PutMapping("/{id}")
    public StudentDto updateStudent(@PathVariable long id, @Valid @RequestBody StudentDto dto) {
        return studentMapper.toDto(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.status(204).body(id);
    }

    @GetMapping("/total")
    public Long getTotalStudents() {
        return studentService.getTotalStudents();
    }

    @GetMapping("/average")
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last")
    public Collection<StudentDto> getLastStudents(@RequestParam(required = false, defaultValue = "5") long amount) {
        return studentService.getLastStudents(amount).stream().map(studentMapper::toDto).toList();
    }

    @GetMapping("/start_with_a")
    public Collection<String> getStartWithAStudents() {
        return studentService.getStartWithAStudents();
    }

    @GetMapping("/print-parallel")
    public void printParallel() {
        studentService.printParallel();
    }

    @GetMapping("/print-sync")
    public void printSync() {
        studentService.printSynchronized();
    }


}
