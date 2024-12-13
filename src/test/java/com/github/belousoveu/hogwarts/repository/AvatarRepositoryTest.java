package com.github.belousoveu.hogwarts.repository;

import com.github.belousoveu.hogwarts.TestData;
import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.model.entity.Faculty;
import com.github.belousoveu.hogwarts.model.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AvatarRepositoryTest {

    @Autowired
    private AvatarRepository avatarRepository;

    private long studentId, avatarId;

    @BeforeEach
    void setUp(@Autowired TestEntityManager entityManager) {
        avatarRepository.deleteAll();
        Faculty faculty = TestData.getMockFaculty("test", "test");
        Student student = Student.builder().faculty(faculty).build();
        Avatar avatar = Avatar.builder().student(student).build();
        entityManager.persist(faculty);
        entityManager.persist(student);
        entityManager.persist(avatar);
        entityManager.flush();
        studentId = student.getId();
        avatarId = avatar.getId();
    }

    @Test
    void test_findByStudentId_whenStudentExists() {
        Avatar avatar = avatarRepository.findByStudentId(studentId).get();
        assertNotNull(avatar);
        assertEquals(avatarId, avatar.getId());
    }

    @Test
    void test_findByStudentId_whenStudentDoesNotExist() {
        assertTrue(avatarRepository.findByStudentId(0L).isEmpty());
    }
}