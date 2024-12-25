package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
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
        log.info("Add student {}", studentDto);
        return studentRepository.save(studentMapper.toEntity(studentDto));
    }

    @Transactional
    @Override
    public Student updateStudent(long id, StudentDto studentDto) {
        if (studentRepository.existsById(id)) {
            studentDto.setId(id);
            Student student = studentMapper.toEntity(studentDto);
            log.info("Update student {}", student);
            return studentRepository.save(student);
        }
        throw new StudentNotFoundException(id);
    }

    @Override
    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
        log.info("Delete student with id {}", id);
    }

    @Override
    public Student getStudent(Long id) {
        log.debug("Trying get student with id {}", id);
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    public Collection<Student> getAllStudents() {
        log.debug("Get all students");
        return studentRepository.findAll();
    }

    @Override
    public Collection<Student> findStudentByAge(List<Integer> age) {
        if (age == null || age.isEmpty()) {
            log.debug("Get all students");
            return studentRepository.findAll();
        } else if (age.size() == 1) {
            log.debug("Get student by age {}", age.get(0));
            return studentRepository.findAllByAge(age.get(0));
        } else if (age.size() == 2) {
            log.debug("Get student by age between {} and {}", age.get(0), age.get(1));
            return studentRepository
                    .findAllByAgeBetween(Math.min(age.get(0), age.get(1)), Math.max(age.get(0), age.get(1)));
        } else {
            return studentRepository.findAllByAgeIn(age);
        }
    }

    @Override
    public Collection<Student> findStudentByFaculty(String faculty) {
        log.debug("Getting students by faculty {}", faculty);
        return studentRepository.findAllByFacultyId(facultyService.getFaculty(faculty).getId());
    }

    @Override
    public Collection<Student> findStudentByFaculty(int facultyId) {
        log.debug("Getting students by faculty id {}", facultyId);
        return studentRepository.findAllByFacultyId(facultyId);
    }

    @Override
    public Long getTotalStudents() {
        log.debug("Getting total students");
        return studentRepository.count();
    }

    @Override
    public Double getAverageAge() {
        log.debug("Getting average age");
        return studentRepository.averageAge();
    }

    @Override
    public Collection<Student> getLastStudents(long amount) {
        log.debug("Getting last {} students", amount);
        return studentRepository.getLastStudents(amount);
    }

    @Override
    public Collection<String> getStartWithAStudents() {
        return studentRepository.getStartWithAStudents();
    }

    @Override
    public void printParallel() {
        List<Student> students = studentRepository.findFirst6ByOrderByIdAsc();

        System.out.println(students.get(0));
        System.out.println(students.get(1));

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println(students.get(2));
            System.out.println(students.get(3));
                });

        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            System.out.println(students.get(4));
            System.out.println(students.get(5));
        });

        CompletableFuture.allOf(f1, f2).join();
    }

    @Override
    public void printSynchronized() {
        List<Student> students = studentRepository.findFirst6ByOrderByIdAsc();

        printStudent(students.get(0));
        printStudent(students.get(1));

        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            printStudent(students.get(2));
            printStudent(students.get(3));
        });

        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            printStudent(students.get(4));
            printStudent(students.get(5));
        });

        CompletableFuture.allOf(f1, f2).join();

    }

    @Synchronized
    private void printStudent(Student student) {
        System.out.println(student);
    }


}
