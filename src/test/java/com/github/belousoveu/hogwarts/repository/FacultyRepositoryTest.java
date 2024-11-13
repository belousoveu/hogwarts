package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FacultyRepositoryTest {


    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach()
    void setup(@Autowired TestEntityManager entityManager) {
        Faculty faculty1 = TestData.getMockFaculty("test1", "red");
        entityManager.persist(faculty1);
        Faculty faculty2 = TestData.getMockFaculty("test2", "blue");
        entityManager.persist(faculty2);
        Faculty faculty3 = TestData.getMockFaculty("test3", "red");
        entityManager.persist(faculty3);
        entityManager.flush();
    }

    @Test
    @Transactional
    void test_findByColor_whenRecordExists() {

        Collection<Faculty> facultyList = facultyRepository.findByColor("red");
        assertEquals(2, facultyList.size());

    }

    @Test
    @Transactional
    void test_findByColor_whenRecordNotExists() {

        Collection<Faculty> facultyList = facultyRepository.findByColor("green");
        assertEquals(0, facultyList.size());

    }

    @Test
    @Transactional
    void test_findByName_whenRecordExists() {

        Faculty actual = facultyRepository.findByName("test1").orElse(null);

        assertNotNull(actual);
        assertEquals("test1", actual.getName());
    }

    @Test
    void test_findByName_whenRecordNotExists() {

        Faculty actual = facultyRepository.findByName("test4").orElse(null);

        assertNull(actual);
    }

    @Test
    @Transactional
    void test_findByNameOrColor_withBothArguments() {

        Collection<Faculty> actual = facultyRepository.findAllByNameOrColor("test2", "red");

        assertEquals(3, actual.size());
    }

    @Test
    @Transactional
    void test_findByNameOrColor_withColorArgument() {

        Collection<Faculty> actual = facultyRepository.findAllByNameOrColor(null, "red");

        assertEquals(2, actual.size());
    }

    @Test
    @Transactional
    void test_findByNameOrColor_withNameArgument() {

        Collection<Faculty> actual = facultyRepository.findAllByNameOrColor("test1", null);

        assertEquals(1, actual.size());
    }

    @Test
    @Transactional
    void test_findByNameOrColor_withoutArgument() {

        Collection<Faculty> actual = facultyRepository.findAllByNameOrColor(null, null);

        assertEquals(0, actual.size());
    }
}