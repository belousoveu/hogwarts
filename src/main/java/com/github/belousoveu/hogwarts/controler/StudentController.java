package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.model.Student;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public Collection<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable long id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/filter")
    public Collection<Student> findStudentsByAge(@RequestParam(required = false) int age) {
        return studentService.findStudentByAge(age);
    }


    @PostMapping("/add")
    public Student addStudent(@RequestBody StudentDto dto) {
        return studentService.addStudent(dto);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable long id, @RequestBody StudentDto dto) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.status(204).body(id);
    }

}
