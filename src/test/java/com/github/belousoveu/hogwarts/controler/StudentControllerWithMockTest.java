package com.github.belousoveu.hogwarts.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWithMockTest {

    private Student student1, student2, student3;
    private StudentDto studentDto1, studentDto2, studentDto3;
    private Faculty faculty1, faculty2;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentController out;

    @BeforeEach
    void setUp() {
        faculty1 = TestData.getMockFaculty("test1", "color1");
        faculty2 = TestData.getMockFaculty("test2", "color2");

        student1 = Student.builder().name("student1").surname("surname1").age(12).faculty(faculty1).build();
        student2 = Student.builder().name("student2").surname("surname2").age(13).faculty(faculty1).build();
        student3 = Student.builder().name("student3").surname("surname3").age(14).faculty(faculty2).build();

        studentDto1 = StudentDto.builder().id(student1.getId()).name("student1").surname("surname1").age(12).facultyId(faculty1.getId()).build();
        studentDto2 = StudentDto.builder().id(student2.getId()).name("student2").surname("surname2").age(13).facultyId(faculty1.getId()).build();
        studentDto3 = StudentDto.builder().id(student3.getId()).name("student3").surname("surname3").age(14).facultyId(faculty2.getId()).build();

    }

    @Test
    void test_getAllStudents_withoutParameter() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(student1, student2, student3));
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);
        when(studentMapper.toDto(student2)).thenReturn(studentDto2);
        when(studentMapper.toDto(student3)).thenReturn(studentDto3);

        mockMvc.perform(get("/student/all").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(studentDto2.getId()))
                .andExpect(jsonPath("$[2].id").value(studentDto3.getId()));

    }

    @Test
    void test_getAllStudents_withParameter() throws Exception {
        int facultyId = faculty1.getId();
        when(studentService.findStudentByFaculty(facultyId)).thenReturn(List.of(student1, student2));
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);
        when(studentMapper.toDto(student2)).thenReturn(studentDto2);

        mockMvc.perform(get("/student/all?faculty_id=" + facultyId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(studentDto2.getId()));

    }

    @Test
    void test_getStudentById_whenCorrectId() throws Exception {
        long id = student1.getId();
        when(studentService.getStudent(id)).thenReturn(student1);
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);

        mockMvc.perform(get("/student/" + id).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentDto1.getId()))
                .andExpect(jsonPath("$.name").value(studentDto1.getName()));
    }

    @Test
    void test_getStudentById_whenIncorrectId() throws Exception {
        doThrow(StudentNotFoundException.class).when(studentService).getStudent(anyLong());

        mockMvc.perform(get("/student/" + 0).contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_findStudentsByAge() throws Exception {
        when(studentService.findStudentByAge(anyList())).thenReturn(List.of(student1, student2));
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);
        when(studentMapper.toDto(student2)).thenReturn(studentDto2);

        mockMvc.perform(get("/student/filter?age=12,13").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(studentDto2.getId()));

    }

    @Test
    void test_addStudent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        long id = student1.getId();
        when(studentService.addStudent(studentDto1)).thenReturn(student1);
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);
        studentDto1.setId(id);
        String jsonContent = objectMapper.writeValueAsString(studentDto1);

        mockMvc.perform(MockMvcRequestBuilders.post("/student/add")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));


    }

    @Test
    void test_updateStudent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        long id = student1.getId();
        when(studentService.updateStudent(id, studentDto1)).thenReturn(student1);
        when(studentMapper.toDto(student1)).thenReturn(studentDto1);
        studentDto1.setId(id);
        String jsonContent = objectMapper.writeValueAsString(studentDto1);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/" + id)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    void test_deleteStudent() throws Exception {

        doNothing().when(studentService).deleteStudent(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(status().isNoContent());

    }
}