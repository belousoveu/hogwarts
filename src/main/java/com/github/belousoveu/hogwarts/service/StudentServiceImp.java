package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class StudentServiceImp implements StudentService {

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;
    private final FacultyService facultyService;


    public StudentServiceImp(StudentMapper studentMapper, StudentRepository studentRepository, FacultyService facultyService) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
        this.facultyService = facultyService;
    }

    @Transactional
    @Override
    public Student addStudent(StudentDto studentDto) {
        return studentRepository.save(studentMapper.toEntity(studentDto));
    }

    @Transactional
    @Override
    public Student updateStudent(long id, StudentDto studentDto) {
        if (studentRepository.existsById(id)) {
            studentDto.setId(id);
            Student student = studentMapper.toEntity(studentDto);
            return studentRepository.save(student);
        }
        throw new StudentNotFoundException(id);
    }

    @Override
    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Collection<Student> findStudentByAge(List<Integer> age) {
        if (age == null || age.isEmpty()) {
            return studentRepository.findAll();
        } else if (age.size() == 1) {
            return studentRepository.findAllByAge(age.get(0));
        } else if (age.size() == 2) {
            return studentRepository
                    .findAllByAgeBetween(Math.min(age.get(0), age.get(1)), Math.max(age.get(0), age.get(1)));
        } else {
            return studentRepository.findAllByAgeIn(age);
        }
    }

    @Override
    public Collection<Student> findStudentByFaculty(String faculty) {
        return studentRepository.findAllByFacultyId(facultyService.getFaculty(faculty).getId());
    }

    @Override
    public Collection<Student> findStudentByFaculty(int facultyId) {
        return studentRepository.findAllByFacultyId(facultyId);
    }

    @Override
    public Long getTotalStudents() {
        return studentRepository.count();
    }

    @Override
    public Double getAverageAge() {
        return studentRepository.averageAge();
    }

    @Override
    public Collection<Student> getLastStudents(long amount) {
        return studentRepository.getLastStudents(amount);
    }
}
