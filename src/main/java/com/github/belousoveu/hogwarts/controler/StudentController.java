package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Student;
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
    public Collection<Student> getAllStudents(@RequestParam(name="faculty_id", required = false) Integer facultyId) {
        if (facultyId == null) {
            return studentService.getAllStudents();
        } else {
            return studentService.findStudentByFaculty(facultyId);
        }
    }

    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable long id) {
        return studentMapper.toDto(studentService.getStudent(id));
    }

    @GetMapping("/filter")
    public Collection<Student> findStudentsByAge(@RequestParam(required = false) List<Integer> age) {
        return studentService.findStudentByAge(age);
    }


    @PostMapping("/add")
    public Student addStudent(@Valid @RequestBody StudentDto dto) {
        return studentService.addStudent(dto);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable long id, @Valid @RequestBody StudentDto dto) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.status(204).body(id);
    }

}
