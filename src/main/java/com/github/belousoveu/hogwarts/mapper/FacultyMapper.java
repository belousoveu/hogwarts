package com.github.belousoveu.hogwarts.mapper;

import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FacultyMapper {

    private final ModelMapper modelMapper;

    public FacultyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Faculty toEntity(FacultyDto dto) {
        return modelMapper.map(dto, Faculty.class);
    }

    public FacultyDto toDto(Faculty faculty) {
        return modelMapper.map(faculty, FacultyDto.class);
    }
}
