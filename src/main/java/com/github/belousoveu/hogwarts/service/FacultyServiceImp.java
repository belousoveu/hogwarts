package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.FacultyNotFoundException;
import com.github.belousoveu.hogwarts.exception.NotUniqueFacultyNameException;
import com.github.belousoveu.hogwarts.mapper.FacultyMapper;
import com.github.belousoveu.hogwarts.mapper.StudentMapper;
import com.github.belousoveu.hogwarts.model.dto.FacultyDto;
import com.github.belousoveu.hogwarts.model.dto.StudentDto;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.repository.FacultyRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FacultyServiceImp implements FacultyService {

    private final FacultyMapper facultyMapper;
    private final StudentMapper studentMapper;
    private final FacultyRepository facultyRepository;

    public FacultyServiceImp(FacultyMapper facultyMapper, StudentMapper studentMapper, FacultyRepository facultyRepository) {
        this.facultyMapper = facultyMapper;
        this.studentMapper = studentMapper;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Collection<Faculty> getFaculties() {
        log.debug("Getting all faculties");
        return facultyRepository.findAll();
    }

    @Override
    public Collection<Faculty> findFacultiesByColor(String color) {
        log.debug("Getting all faculties by color {}", color);
        return facultyRepository.findByColor(color);
    }

    @Override
    public Faculty getFaculty(int id) {
        log.debug("Trying getting faculty by id {}", id);
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));

    }

    @Override
    public Faculty getFaculty(String name) {
        log.debug("Trying getting faculty by name {}", name);
        return facultyRepository.findByName(name).orElseThrow(() -> new FacultyNotFoundException(name));
    }

    @Transactional
    @Override
    public Faculty updateFaculty(int id, FacultyDto dto) {
        if (facultyRepository.findById(id).isPresent()) {
            dto.setId(id);
            log.info("Updating faculty with id {}", id);
            return save(facultyMapper.toEntity(dto));
        }
        throw new FacultyNotFoundException(id);
    }

    @Override
    public Collection<Faculty> findFaculty(String name, String color) {
        if (color == null && name == null) {
            return facultyRepository.findAll();
        }
        log.debug("Getting all faculties by name {} or color {}", name, color);
        return facultyRepository.findAllByNameOrColor(name, color);
    }

    @Override
    public void deleteFaculty(int id) {
        facultyRepository.deleteById(id);
        log.info("Deleting faculty with id {}", id);
    }

    @Transactional
    @Override
    public Faculty addFaculty(FacultyDto dto) {
        log.info("Adding faculty with id {}", dto.getId());
        return save(facultyMapper.toEntity(dto));
    }

    @Override
    @Transactional
    public Collection<StudentDto> getFacultyStudents(int id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        log.debug("Getting all students of faculty with id {}", id);
        if (faculty.getStudents() == null) {
            return List.of();
        }
        return faculty.getStudents().stream().map(studentMapper::toDto).toList();
    }

    private Faculty save(Faculty faculty) {
        try {
            return facultyRepository.saveAndFlush(faculty);

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new NotUniqueFacultyNameException(faculty.getName());
            }
            throw e;
        }
    }
}
