package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    private int facultyId;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp(@Autowired TestEntityManager entityManager) {
        Faculty faculty1 = TestData.getMockFaculty("test1", "color1");
        Faculty faculty2 = TestData.getMockFaculty("test2", "color2");
        entityManager.persist(faculty1);
        entityManager.persist(faculty2);
        facultyId = faculty1.getId();

        entityManager.persist(Student.builder().name("name1").surname("surname1").age(11).faculty(faculty1).build());
        entityManager.persist(Student.builder().name("name2").surname("surname2").age(12).faculty(faculty1).build());
        entityManager.persist(Student.builder().name("name3").surname("surname3").age(11).faculty(faculty2).build());
        entityManager.persist(Student.builder().name("name4").surname("surname4").age(13).faculty(faculty2).build());
        entityManager.flush();
    }

    @Test
    @Transactional
    void test_findAllByAge_whenRecordsExist() {

        Collection<Student> students = studentRepository.findAllByAge(11);

        assertEquals(2, students.size());
    }

    @Test
    @Transactional
    void test_findAllByAge_whenRecordsNotExist() {

        Collection<Student> students = studentRepository.findAllByAge(20);

        assertTrue(students.isEmpty());
    }

    @Test
    @Transactional
    void test_findAllByFaculty_Id_whenRecordsExist() {

        Collection<Student> students = studentRepository.findAllByFaculty_Id(facultyId);

        assertEquals(2, students.size());
    }

    @Test
    @Transactional
    void test_findAllByFaculty_Id_whenRecordsNotExist() {

        Collection<Student> students = studentRepository.findAllByFaculty_Id(5);

        assertTrue(students.isEmpty());
    }

}