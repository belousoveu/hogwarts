package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {


    Collection<Student> findAllByAge(int age);

    Collection<Student> findAllByFaculty_Id(int id);
}
