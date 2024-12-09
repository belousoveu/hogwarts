package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.exception.NotUniqueFacultyNameException;
import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import(FacultyServiceImp.class)
class FacultyServiceTest {

    private int facultyId, facultyId3;

    @MockBean
    private FacultyMapper facultyMapper;

    @MockBean
    private StudentMapper studentMapper;

    @Autowired
    private FacultyServiceImp facultyServiceImp;

    @BeforeEach()
    void setup(@Autowired TestEntityManager entityManager) {
        Faculty faculty1 = TestData.getMockFaculty("test1", "red");
        entityManager.persist(faculty1);
        facultyId = faculty1.getId();
        Faculty faculty2 = TestData.getMockFaculty("test2", "blue");
        entityManager.persist(faculty2);
        Faculty faculty3 = TestData.getMockFaculty("test3", "red");
        Student student1 = Student.builder().id(1L).name("name1").surname("surname1").faculty(faculty3).build();
        Student student2 = Student.builder().id(2L).name("name2").surname("surname2").faculty(faculty3).build();
        faculty3.setStudents(List.of(student1, student2));
        entityManager.persist(faculty3);
        facultyId3 = faculty3.getId();

        entityManager.flush();
    }

    @Test
    @Transactional
    void test_getFaculties() {
        Collection<Faculty> actual = facultyServiceImp.getFaculties();
        assertEquals(3, actual.size());
    }

    @Test
    @Transactional
    void test_findFacultiesByColor_whenRecordExists() {
        assertEquals(2, facultyServiceImp.findFacultiesByColor("red").size());
    }

    @Test
    @Transactional
    void test_findFacultiesByColor_whenRecordNotExists() {
        assertTrue(facultyServiceImp.findFacultiesByColor("green").isEmpty());
    }

    @Test
    @Transactional
    void test_getFaculty_byIdWhenRecordExists() {

        Faculty actual = facultyServiceImp.getFaculty(facultyId);

        assertNotNull(actual);
        assertEquals(facultyId, actual.getId());
    }

    @Test
    @Transactional
    void test_getFaculty_byIdWhenRecordNotExists() {
        assertThrows(FacultyNotFoundException.class, () -> facultyServiceImp.getFaculty(0));
    }

    @Test
    @Transactional
    void test_getFaculty_byNameWhenRecordExists() {

        Faculty actual = facultyServiceImp.getFaculty("test1");

        assertNotNull(actual);
        assertEquals("test1", actual.getName());
    }

    @Test
    @Transactional
    void test_getFaculty_byNameWhenRecordNotExists() {
        assertThrows(FacultyNotFoundException.class, () -> facultyServiceImp.getFaculty("absent"));
    }

    @Test
    @Transactional
    void test_updateFaculty_whenCorrectIdAndData() {
        Faculty expected = TestData.getMockFaculty("test1", "blue");
        FacultyDto expectedDto = TestData.getMockFacultyDto("test1", "blue");
        expected.setId(facultyId);
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expected);
        int expectedSize = facultyServiceImp.getFaculties().size();

        Faculty actual = facultyServiceImp.updateFaculty(facultyId, expectedDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertEquals(expectedSize, facultyServiceImp.getFaculties().size());

    }

    @Test
    @Transactional
    void test_updateFaculty_whenIncorrectId() {
        FacultyDto expected = TestData.getMockFacultyDto("test2", "blue");

        assertThrows(FacultyNotFoundException.class, () -> facultyServiceImp.updateFaculty(0, expected));

    }


    @Test
    @Transactional
    void test_updateFaculty_whenIncorrectData() {
        FacultyDto expectedDto = TestData.getMockFacultyDto("test2", "blue");
        Faculty expected = TestData.getMockFaculty("test2", "blue");
        expected.setId(facultyId);
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expected);

        assertThrows(NotUniqueFacultyNameException.class, () -> facultyServiceImp.updateFaculty(facultyId, expectedDto));

    }

    @Test
    @Transactional
    void test_findFaculty_withBothArguments() {
        Collection<Faculty> actual = facultyServiceImp.findFaculty("TEST2", "RED");

        assertEquals(3, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withColorArguments() {
        Collection<Faculty> actual = facultyServiceImp.findFaculty(null, "RED");

        assertEquals(2, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withNameArguments() {
        Collection<Faculty> actual = facultyServiceImp.findFaculty("TEST1", null);

        assertEquals(1, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withoutArguments() {
        Collection<Faculty> actual = facultyServiceImp.findFaculty(null, null);

        assertEquals(3, actual.size());
        assertIterableEquals(facultyServiceImp.getFaculties(), actual);
    }

    @Test
    @Transactional
    void test_deleteFaculty() {
        int expectedSize = facultyServiceImp.getFaculties().size();
        facultyServiceImp.deleteFaculty(facultyId);
        assertEquals(expectedSize - 1, facultyServiceImp.getFaculties().size());
        assertThrows(FacultyNotFoundException.class, () -> facultyServiceImp.getFaculty(facultyId));
    }


    @Test
    @Transactional
    void test_addFaculty_whenCorrectData() {
        FacultyDto expected = TestData.getMockFacultyDto("test4", "green");
        Faculty expectedEntity = TestData.getMockFaculty("test4", "green");
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expectedEntity);
        int expectedSize = facultyServiceImp.getFaculties().size();

        Faculty actual = facultyServiceImp.addFaculty(expected);

        assertNotNull(actual);
        assertEquals(expectedEntity, actual);
        assertEquals(expectedSize + 1, facultyServiceImp.getFaculties().size());
    }

    @Test
    @Transactional
    void test_addFaculty_whenIncorrectData() {
        FacultyDto expectedDto = TestData.getMockFacultyDto("test1", "green");

        Faculty expected = TestData.getMockFaculty("test1", "green");
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expected);

        assertThrows(NotUniqueFacultyNameException.class, () -> facultyServiceImp.updateFaculty(facultyId, expectedDto));

    }

    @Test
    @Transactional
    void test_getFacultyStudents_whenCorrectId() {
        Collection<StudentDto> actual = facultyServiceImp.getFacultyStudents(facultyId3);
        assertEquals(2, actual.size());
    }

    @Test
    @Transactional
    void test_getFacultyStudents_whenIncorrectId() {

        assertThrows(FacultyNotFoundException.class, () -> facultyServiceImp.getFacultyStudents(-1));
    }

    @Test
    @Transactional
    void test_getFacultyStudents_whenEmptyStudents() {

        Collection<StudentDto> actual = facultyServiceImp.getFacultyStudents(facultyId);
        assertTrue(actual.isEmpty());
    }
}