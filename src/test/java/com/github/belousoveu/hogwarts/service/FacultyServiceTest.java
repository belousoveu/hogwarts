package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.exception.NotUniqueFacultyNameException;
import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@Import(FacultyService.class)
class FacultyServiceTest {

    private int facultyId;

    @MockBean
    private FacultyMapper facultyMapper;

    @Autowired
    private FacultyService facultyService;

    @BeforeEach()
    void setup(@Autowired TestEntityManager entityManager) {
        Faculty faculty1 = TestData.getMockFaculty("test1", "red");
        entityManager.persist(faculty1);
        facultyId = faculty1.getId();
        Faculty faculty2 = TestData.getMockFaculty("test2", "blue");
        entityManager.persist(faculty2);
        Faculty faculty3 = TestData.getMockFaculty("test3", "red");
        entityManager.persist(faculty3);
        entityManager.flush();
    }

    @Test
    @Transactional
    void test_getFaculties() {
        Collection<Faculty> actual = facultyService.getFaculties();
        assertEquals(3, actual.size());
    }

    @Test
    @Transactional
    void test_findFacultiesByColor_whenRecordExists() {
        assertEquals(2, facultyService.findFacultiesByColor("red").size());
    }

    @Test
    @Transactional
    void test_findFacultiesByColor_whenRecordNotExists() {
        assertTrue(facultyService.findFacultiesByColor("green").isEmpty());
    }

    @Test
    @Transactional
    void test_getFaculty_byIdWhenRecordExists() {
        FacultyDto expected = TestData.getMockFacultyDto("test1", "red");
        when(facultyMapper.toDto(any(Faculty.class))).thenReturn(expected);

        FacultyDto actual = facultyService.getFaculty(facultyId);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void test_getFaculty_byIdWhenRecordNotExists() {
        assertThrows(FacultyNotFoundException.class, () -> facultyService.getFaculty(0));
    }

    @Test
    @Transactional
    void test_getFaculty_byNameWhenRecordExists() {
        FacultyDto expected = TestData.getMockFacultyDto("test1", "red");
        when(facultyMapper.toDto(any(Faculty.class))).thenReturn(expected);

        FacultyDto actual = facultyService.getFaculty("test1");

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void test_getFaculty_byNameWhenRecordNotExists() {
        assertThrows(FacultyNotFoundException.class, () -> facultyService.getFaculty("absent"));
    }

    @Test
    @Transactional
    void test_updateFaculty_whenCorrectIdAndData() {
        Faculty expected = TestData.getMockFaculty("test1", "blue");
        FacultyDto expectedDto = TestData.getMockFacultyDto("test1", "blue");
        expected.setId(facultyId);
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expected);
        int expectedSize = facultyService.getFaculties().size();

        Faculty actual = facultyService.updateFaculty(facultyId, expectedDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertEquals(expectedSize, facultyService.getFaculties().size());

    }

    @Test
    @Transactional
    void test_updateFaculty_whenIncorrectId() {
        FacultyDto expected = TestData.getMockFacultyDto("test2", "blue");

        assertThrows(FacultyNotFoundException.class, () -> facultyService.updateFaculty(0, expected));

    }


    @Test
//    @Disabled("not correct test")
    @Transactional
    void test_updateFaculty_whenIncorrectData() {
        FacultyDto expectedDto = TestData.getMockFacultyDto("test2", "blue");
        Faculty expected = TestData.getMockFaculty("test2", "blue");
        expected.setId(facultyId);
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expected);


        assertThrows(NotUniqueFacultyNameException.class, () -> facultyService.updateFaculty(facultyId, expectedDto));

    }

    @Test
    @Transactional
    void test_findFaculty_withBothArguments() {
        Collection<Faculty> actual = facultyService.findFaculty("test2", "red");

        assertEquals(3, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withColorArguments() {
        Collection<Faculty> actual = facultyService.findFaculty(null, "red");

        assertEquals(2, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withNameArguments() {
        Collection<Faculty> actual = facultyService.findFaculty("test1", null);

        assertEquals(1, actual.size());
    }

    @Test
    @Transactional
    void test_findFaculty_withoutArguments() {
        Collection<Faculty> actual = facultyService.findFaculty(null, null);

        assertEquals(3, actual.size());
        assertIterableEquals(facultyService.getFaculties(), actual);
    }

    @Test
    @Transactional
    void test_deleteFaculty() {
        int expectedSize = facultyService.getFaculties().size();
        facultyService.deleteFaculty(facultyId);
        assertEquals(expectedSize - 1, facultyService.getFaculties().size());
        assertThrows(FacultyNotFoundException.class, () -> facultyService.getFaculty(facultyId));
    }


    @Test
    @Transactional
    void test_addFaculty_whenCorrectData() {
        FacultyDto expected = TestData.getMockFacultyDto("test4", "green");
        Faculty expectedEntity = TestData.getMockFaculty("test4", "green");
        when(facultyMapper.toEntity(any(FacultyDto.class))).thenReturn(expectedEntity);
        int expectedSize = facultyService.getFaculties().size();

        Faculty actual = facultyService.addFaculty(expected);

        assertNotNull(actual);
        assertEquals(expectedEntity, actual);
        assertEquals(expectedSize + 1, facultyService.getFaculties().size());
    }
}