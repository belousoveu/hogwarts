package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import(StudentServiceImp.class)
class StudentServiceTest {

    private int facultyId;
    private long studentId;

    @MockBean
    private StudentMapper studentMapper;

    @MockBean
    private FacultyServiceImp facultyServiceImp;

    @Autowired
    private StudentServiceImp studentServiceImp;

    @BeforeEach
    void setUp(@Autowired TestEntityManager entityManager) {
        Faculty faculty1 = TestData.getMockFaculty("test1", "color1");
        Faculty faculty2 = TestData.getMockFaculty("test2", "color2");
        entityManager.persist(faculty1);
        entityManager.persist(faculty2);
        facultyId = faculty1.getId();

        Student student1 = Student.builder().name("name1").surname("surname1").age(11).faculty(faculty1).build();
        entityManager.persist(student1);
        studentId = student1.getId();
        System.out.println("studentId = " + studentId);
        entityManager.persist(Student.builder().name("name2").surname("surname2").age(12).faculty(faculty1).build());
        entityManager.persist(Student.builder().name("name3").surname("surname3").age(11).faculty(faculty2).build());
        entityManager.persist(Student.builder().name("name4").surname("surname4").age(13).faculty(faculty2).build());
        entityManager.flush();
        when(studentMapper.toEntity(any(StudentDto.class))).thenAnswer(i -> {
            StudentDto dto = i.getArgument(0);
            return Student.builder().id(dto.getId()).name(dto.getName()).surname(dto.getSurname()).age(dto.getAge()).build();
        });
    }

    @Test
    @Transactional
    void test_addStudent() {
        StudentDto studentDto = StudentDto.builder().name("name5").surname("surname5").age(11).build();
        int expectedSize = studentServiceImp.getAllStudents().size();

        Student actualStudent = studentServiceImp.addStudent(studentDto);

        assertNotNull(actualStudent);
        assertEquals(expectedSize + 1, studentServiceImp.getAllStudents().size());
        assertTrue(studentServiceImp.getAllStudents().contains(actualStudent));

    }

    @Test
//    @Transactional
    void test_updateStudent_whenCorrectId() {
        StudentDto studentDto = StudentDto.builder().name("name5").surname("surname5").age(11).build();
        int expectedSize = studentServiceImp.getAllStudents().size();

        Student actualStudent = studentServiceImp.updateStudent(studentId, studentDto);

        assertNotNull(actualStudent);
        assertEquals(expectedSize, studentServiceImp.getAllStudents().size());
        assertTrue(studentServiceImp.getAllStudents().contains(actualStudent));

    }

    @Test
    @Transactional
    void test_updateStudent_whenIncorrectId() {
        StudentDto studentDto = StudentDto.builder().name("name5").surname("surname5").age(11).build();

        assertThrows(StudentNotFoundException.class, () -> studentServiceImp.updateStudent(0, studentDto));

    }

    @Test
    @Transactional
    void test_deleteStudent() {
        int expectedSize = studentServiceImp.getAllStudents().size();

        studentServiceImp.deleteStudent(studentId);

        assertEquals(expectedSize - 1, studentServiceImp.getAllStudents().size());
        assertThrows(StudentNotFoundException.class, () -> studentServiceImp.getStudent(studentId));
    }

    @Test
    @Transactional
    void test_getStudent_whenCorrectId() {

        Student actualStudent = studentServiceImp.getStudent(studentId);

        assertNotNull(actualStudent);
        assertEquals(studentId, actualStudent.getId());
    }

    @Test
    @Transactional
    void test_getStudent_whenIncorrectId() {

        assertThrows(StudentNotFoundException.class, () -> studentServiceImp.getStudent(0L));
    }

    @Test
    @Transactional
    void test_getAllStudents() {

        assertEquals(4, studentServiceImp.getAllStudents().size());
    }

    @Test
    @Transactional
    void test_findStudentByAge_whenRecordsExist() {
        assertEquals(2, studentServiceImp.findStudentByAge(11).size());
    }

    @Test
    @Transactional
    void test_findStudentByAge_whenRecordsNotExist() {
        assertTrue(studentServiceImp.findStudentByAge(100).isEmpty());
    }

    @Test
    @Transactional
    void test_findStudentByFaculty_whenRecordsNotExist() {

        FacultyDto facultyDto = TestData.getMockFacultyDto("test1", "color1");
        when(facultyServiceImp.getFaculty(anyString())).thenReturn(facultyDto);
        facultyDto.setId(0);

        assertTrue(studentServiceImp.findStudentByFaculty("test3").isEmpty());
//        assertThrows(FacultyNotFoundException.class,() -> studentService.findStudentByFaculty("test3"));
    }

    @Test
    @Transactional
    void test_findStudentByFaculty_whenRecordsExist() {

        FacultyDto facultyDto = TestData.getMockFacultyDto("test1", "color1");
        when(facultyServiceImp.getFaculty(anyString())).thenReturn(facultyDto);
        facultyDto.setId(facultyId);

        Collection<Student> actual = studentServiceImp.findStudentByFaculty("test1");
        assertEquals(2, actual.size());
        assertEquals("test1", actual.iterator().next().getFaculty().getName());
    }
}