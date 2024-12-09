package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.TestData;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    String requestUrl;
    int facultyId;
    long studentId, studentId2, studentId3;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {

        studentRepository.deleteAll();
        facultyRepository.deleteAll();

        Faculty faculty1 = TestData.getMockFaculty("faculty1", "color1");
        Faculty faculty2 = TestData.getMockFaculty("faculty2", "color2");

        Student student1 = Student.builder().name("student1").surname("surname1").age(12).faculty(faculty1).build();
        Student student2 = Student.builder().name("student2").surname("surname2").age(13).faculty(faculty1).build();
        Student student3 = Student.builder().name("student3").surname("surname3").age(14).faculty(faculty2).build();

        facultyRepository.save(faculty1);
        facultyRepository.save(faculty2);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        studentRepository.flush();

        studentId = student1.getId();
        studentId2 = student2.getId();
        studentId3 = student3.getId();
        facultyId = faculty1.getId();
        requestUrl = "http://localhost:" + port + "/student";

    }

    @Test
    void contextLoads() {
        assertNotNull(studentController);
    }

    @Test
    void test_getAllStudents_withoutParameter() {

        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                requestUrl + "/all",
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
    void test_getAllStudents_withParameter() {

        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                requestUrl + "/all?faculty_id=" + facultyId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

    }

    @Test
    void test_getStudentById_whenExist() {
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(requestUrl + "/" + studentId, StudentDto.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals("student1", response.getBody().getName());

    }

    @Test
    void test_getStudentById_whenNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl + "/" + 0, String.class);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    void test_findStudentsByAge() {
        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                requestUrl + "/filter?age=12,13",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void test_addStudent() {
        StudentDto newStudent = StudentDto.builder().name("newName").surname("newSurname").age(12).facultyId(facultyId).build();
        ResponseEntity<StudentDto> response = restTemplate.postForEntity(requestUrl+"/add", newStudent, StudentDto.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals("newName", response.getBody().getName());

    }

    @Test
    void test_updateStudent_whenCorrectId() {
        StudentDto updatedStudent = StudentDto.builder().name("newName").surname("newSurname").age(12).facultyId(facultyId).build();
        ResponseEntity<StudentDto> response = restTemplate.exchange(requestUrl+"/"+studentId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                StudentDto.class,
                studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newName", response.getBody().getName());
    }

    @Test
    void test_updateStudent_whenIncorrectId() {
        long incorrectId = 0;
        StudentDto updatedStudent = StudentDto.builder().name("newName").surname("newSurname").age(12).facultyId(facultyId).build();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl+"/"+incorrectId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                String.class,
                incorrectId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void test_deleteStudent() {
        ResponseEntity<String> response = restTemplate.exchange(requestUrl+"/"+studentId,
                HttpMethod.DELETE,
                null,
                String.class,
                studentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

    }

    @Test
    void test_getTotalStudents() {
        ResponseEntity<Long> response = restTemplate.getForEntity(requestUrl + "/total", Long.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().longValue());
    }

    @Test
    void test_getAverageAge() {
        ResponseEntity<Double> response = restTemplate.getForEntity(requestUrl + "/average", Double.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(13.0, response.getBody().doubleValue());
    }

    @Test
    void test_getLastStudents_withParameter() {
        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                requestUrl + "/last?amount=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        List<StudentDto> students = response.getBody().stream().toList();
        assertEquals(studentId2, students.get(0).getId());
        assertEquals(studentId3, students.get(1).getId());
    }

}