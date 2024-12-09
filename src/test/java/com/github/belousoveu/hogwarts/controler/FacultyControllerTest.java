package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.FacultyRepository;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    private Faculty testFaculty;
    String requestUrl;
    String testColor = "blue";
    String testName = "test1";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;


    @BeforeEach
    void setUp() {


        Faculty faculty1 = TestData.getMockFaculty("test1", "red");
        Faculty faculty2 = TestData.getMockFaculty("test2", "blue");
        Faculty faculty3 = TestData.getMockFaculty("test3", "green");

        Student student1 = Student.builder().id(1L).name("name1").surname("surname1").faculty(faculty1).build();
        Student student2 = Student.builder().id(2L).name("name2").surname("surname2").faculty(faculty1).build();

        studentRepository.deleteAll();
        facultyRepository.deleteAll();
        facultyRepository.save(faculty1);
        facultyRepository.save(faculty2);
        facultyRepository.save(faculty3);

        studentRepository.save(student1);
        studentRepository.save(student2);

        facultyRepository.flush();

        testFaculty = faculty1;
        requestUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    void contextLoads() {
        assertNotNull(facultyController);
    }

    @Test
    void test_getFacultyList() {
        ResponseEntity<Collection<FacultyDto>> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());

    }

    @Test
    void test_addFaculty() {
        FacultyDto facultyDto = new FacultyDto();
        facultyDto.setName("new faculty");
        facultyDto.setColor(testColor);

        ResponseEntity<FacultyDto> response = restTemplate.postForEntity(requestUrl + "/add", facultyDto, FacultyDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new faculty", response.getBody().getName());
    }

    @Test
    void test_getFacultyById_whenIdExists() {
        FacultyDto actual = this.restTemplate.getForObject(requestUrl + "/" + testFaculty.getId(), FacultyDto.class);
        assertNotNull(actual);
        assertEquals(testFaculty.getId(), actual.getId());
    }

    @Test
    void test_getFacultyById_whenIdNotExists() {

        ResponseEntity<String> actual = this.restTemplate.getForEntity(requestUrl + "/0", String.class);
        assertNotNull(actual);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void test_getFacultyStudents() {
        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                requestUrl + "/" + testFaculty.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        response.getBody().forEach(System.out::println);
        assertEquals(2, response.getBody().size());
    }

    @Test
    void test_updateFaculty_whenCorrectId() {
        FacultyDto updated = new FacultyDto(1, testName, testColor);
        ResponseEntity<FacultyDto> response = restTemplate.exchange(
                requestUrl + "/" + testFaculty.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                FacultyDto.class,
                1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testColor, response.getBody().getColor());
    }

    @Test
    void test_updateFaculty_whenIncorrectId() {
        int incorrectId = 999;
        FacultyDto updated = new FacultyDto(incorrectId, testName, testColor);
        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl + "/" + incorrectId,
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                String.class,
                incorrectId
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void test_deleteFaculty_whenCorrectId() {
        int deletedId = 3;
        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl + "/" + deletedId,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void test_getFilteredFaculty_withEmptyParameters() {
        ResponseEntity<Collection<FacultyDto>> response = restTemplate.exchange(
                requestUrl + "/search",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        response.getBody().forEach(System.out::println);
        assertEquals(3, response.getBody().size());
    }

    @Test
    void test_getFilteredFaculty_withOnlyNameParameter() {
        ResponseEntity<Collection<FacultyDto>> response = restTemplate.exchange(
                requestUrl + "/search" + "?name=" + testName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        response.getBody().forEach(System.out::println);
        assertEquals(1, response.getBody().size());
        assertEquals(testName, response.getBody().iterator().next().getName());
    }

    @Test
    void test_getFilteredFaculty_withOnlyColorParameter() {
        ResponseEntity<Collection<FacultyDto>> response = restTemplate.exchange(
                requestUrl + "/search" + "?color=" + testColor,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        response.getBody().forEach(System.out::println);
        assertEquals(1, response.getBody().size());
        assertEquals(testColor, response.getBody().iterator().next().getColor());
    }

    @Test
    void test_getFilteredFaculty_withBothParameters() {
        ResponseEntity<Collection<FacultyDto>> response = restTemplate.exchange(
                requestUrl + "/search" + "?color=" + testColor + "&name=" + testName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        response.getBody().forEach(System.out::println);
        assertEquals(2, response.getBody().size());
    }

}