package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.Student;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {

    private final StudentMapper studentMapper;

    private final Map<Long, Student> students = new HashMap<>();
    private long nextId = 1;

    public StudentService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    public Student addStudent(StudentDto studentDto) {
        studentDto.setId(nextId++);
        students.put(studentDto.getId(), studentMapper.toEntity(studentDto));
        return students.get(studentDto.getId());
    }

    public Student updateStudent(long id, StudentDto studentDto) {
        studentDto.setId(id);
        students.put(id, studentMapper.toEntity(studentDto));
        return students.get(studentDto.getId());
    }

    public Student deleteStudent(long id) {
        if (students.containsKey(id)) {
            return students.remove(id);
        }
        throw new StudentNotFoundException(id);
    }

    public Student getStudent(Long id) {
        if (students.containsKey(id)) {
            return students.get(id);
        }
        throw new StudentNotFoundException(id);
    }

    public Collection<Student> getAllStudents() {
        return students.values().stream().toList();
    }

    public Collection<Student> findStudentByAge(int age) {
        return students.values().stream().filter(s -> s.getAge() == age).toList();
    }

    public Collection<Student> findStudentByFaculty(String faculty) {
        return students.values().stream().filter(s -> s.getFaculty().getName().equals(faculty)).toList();
    }

}
