package com.github.belousoveu.hogwarts.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.service.FacultyServiceImp;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FacultyController.class)
class FacultyControllerWithMockTest {

    private Faculty faculty1, faculty2;
    private FacultyDto facultyDto1, facultyDto2;
    private StudentDto studentDto1, studentDto2;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyServiceImp facultyService;

    @MockBean
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyController out;

    @BeforeEach
    void setUp() {
        faculty1 = TestData.getMockFaculty("test1", "color1");
        faculty2 = TestData.getMockFaculty("test2", "color2");

        facultyDto1 = TestData.getMockFacultyDto("test1", "color1");
        facultyDto2 = TestData.getMockFacultyDto("test2", "color2");

        studentDto1 = StudentDto.builder().id(1L).name("name1").surname("surname1").facultyId(1).build();
        studentDto2 = StudentDto.builder().id(2L).name("name2").surname("surname2").facultyId(1).build();

    }


    @Test
    void test_getFacultyList() throws Exception {

        when(facultyService.getFaculties()).thenReturn(List.of(faculty1, faculty2));
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);
        when(facultyMapper.toDto(faculty2)).thenReturn(facultyDto2);


        mockMvc.perform(get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].name").value("test2"));

    }

    @Test
    void test_addFaculty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        when(facultyService.addFaculty(facultyDto1)).thenReturn(faculty1);
        int id = faculty1.getId();
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);
        facultyDto1.setId(id);

        String jsonContent = objectMapper.writeValueAsString(facultyDto1);
        mockMvc.perform(MockMvcRequestBuilders.post("/faculty/add")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    void test_getFacultyById_whenCorrectId() throws Exception {
        when(facultyService.getFaculty(1)).thenReturn(faculty1);
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);

        mockMvc.perform(get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test1"));

    }

    @Test
    void test_getFacultyById_whenIncorrectId() throws Exception {
        when(facultyService.getFaculty(0)).thenThrow(FacultyNotFoundException.class);

        mockMvc.perform(get("/faculty/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void test_getFacultyStudents() throws Exception {
        when(facultyService.getFacultyStudents(1)).thenReturn(List.of(studentDto1, studentDto2));

        mockMvc.perform(get("/faculty/1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].name").value("name2"));

    }

    @Test
    void test_updateFaculty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int id = faculty1.getId();
        when(facultyService.updateFaculty(id, facultyDto1)).thenReturn(faculty1);
        facultyDto1.setId(id);
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);

        String jsonContent = objectMapper.writeValueAsString(facultyDto1);
        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/"+id)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void test_deleteFaculty_whenCorrectId() throws Exception {
        doNothing().when(facultyService).deleteFaculty(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isNoContent());

    }


    @Test
    void test_getFilteredFaculty_withEmptyParameters() throws Exception {

        when(facultyService.findFaculty(null, null)).thenReturn(List.of(faculty1, faculty2));
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);
        when(facultyMapper.toDto(faculty2)).thenReturn(facultyDto2);

        mockMvc.perform(get("/faculty/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].name").value("test2"));

    }

    @Test
    void test_getFilteredFaculty_withOnlyNameParameter() throws Exception {

        when(facultyService.findFaculty("test1", null)).thenReturn(List.of(faculty1));
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);

        mockMvc.perform(get("/faculty/search?name=test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"));

    }

    @Test
    void test_getFilteredFaculty_withOnlyColorParameter() throws Exception {

        when(facultyService.findFaculty(null, "color2")).thenReturn(List.of(faculty2));
        when(facultyMapper.toDto(faculty2)).thenReturn(facultyDto2);

        mockMvc.perform(get("/faculty/search?color=color2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test2"));

    }

    @Test
    void test_getFilteredFaculty_withBothParameters() throws Exception {

        when(facultyService.findFaculty("test1", "color2")).thenReturn(List.of(faculty1, faculty2));
        when(facultyMapper.toDto(faculty1)).thenReturn(facultyDto1);
        when(facultyMapper.toDto(faculty2)).thenReturn(facultyDto2);

        mockMvc.perform(get("/faculty/search?name=test1&color=color2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].name").value("test2"));

    }
}