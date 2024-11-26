package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.model.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByStudentId(long studentId);
}
