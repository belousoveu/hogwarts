package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.Student;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;
    private final FacultyService facultyService;


    public StudentService(StudentMapper studentMapper, StudentRepository studentRepository, FacultyService facultyService) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
        this.facultyService = facultyService;
    }

    public Student addStudent(StudentDto studentDto) {
        return studentRepository.save(studentMapper.toEntity(studentDto));
    }

    public Student updateStudent(long id, StudentDto studentDto) {
        studentDto.setId(id);
        return studentRepository.save(studentMapper.toEntity(studentDto));
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> findStudentByAge(int age) {
        return studentRepository.findAllByAge(age);
    }

    public Collection<Student> findStudentByFaculty(String faculty) {
        return studentRepository.findAllByFaculty_Id(facultyService.getFaculty(faculty).getId());
    }

}
