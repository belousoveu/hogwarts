package com.github.belousoveu.hogwarts.mapper;

import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    private final ModelMapper modelMapper;


    public StudentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public StudentDto toDto(Student student) {
        return modelMapper.map(student, StudentDto.class);
    }

    public Student toEntity(StudentDto studentDto) {
        Student student = modelMapper.map(studentDto, Student.class);
        if (studentDto.getFacultyId() != 0) {
            Faculty faculty = new Faculty();
            faculty.setId(studentDto.getFacultyId());
            student.setFaculty(faculty);
        } else {
            student.setFaculty(null);
        }
        return student;
    }
}
