package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.exception.NotUniqueFacultyNameException;
import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.repository.FacultyRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class FacultyServiceImp implements FacultyService {

    private final FacultyMapper facultyMapper;
    private final FacultyRepository facultyRepository;

    public FacultyServiceImp(FacultyMapper facultyMapper, FacultyRepository facultyRepository) {
        this.facultyMapper = facultyMapper;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Collection<Faculty> getFaculties() {
        return facultyRepository.findAll();
    }

    @Override
    public Collection<Faculty> findFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    @Override
    public FacultyDto getFaculty(int id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        return facultyMapper.toDto(faculty);
    }

    @Override
    public FacultyDto getFaculty(String name) {
        Faculty faculty = facultyRepository.findByName(name).orElseThrow(() -> new FacultyNotFoundException(name));
        return facultyMapper.toDto(faculty);
    }

    @Transactional
    @Override
    public Faculty updateFaculty(int id, FacultyDto dto) {
        if (facultyRepository.findById(id).isPresent()) {
            dto.setId(id);
            return save(facultyMapper.toEntity(dto));
        }
        throw new FacultyNotFoundException(id);
    }

    @Override
    public Collection<Faculty> findFaculty(String name, String color) {
        if (color == null && name == null) {
            return facultyRepository.findAll();
        }
        return facultyRepository.findByNameOrColor(name, color);
    }

    @Override
    public void deleteFaculty(int id) {
        facultyRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Faculty addFaculty(FacultyDto dto) {
        return save(facultyMapper.toEntity(dto));
    }

    private Faculty save(Faculty faculty)  {
        try {
            return facultyRepository.save(faculty);

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new NotUniqueFacultyNameException(faculty.getName());
            }
            throw e;
        }
    }
}