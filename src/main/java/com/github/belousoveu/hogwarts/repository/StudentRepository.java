package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {


    Collection<Student> findAllByAge(int age);

    Collection<Student> findAllByFacultyId(int id);

    Collection<Student> findAllByAgeBetween(int minAge, int maxAge);

    Collection<Student> findAllByAgeIn(List<Integer> age);

    @Query("SELECT AVG(s.age) FROM students s")
    Double averageAge();

    @Query(value = "SELECT * FROM (SELECT * FROM students s ORDER BY s.id DESC LIMIT :amount) AS subquery ORDER BY subquery.id ASC",  nativeQuery = true)
    Collection<Student> getLastStudents(@Param("amount") long amount);
}
