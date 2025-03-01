package com.github.belousoveu.hogwarts.service;

import com.github.belousoveu.hogwarts.exception.ImageNotFoundException;
import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.AvatarRepository;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import com.github.belousoveu.hogwarts.utils.ImageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private AvatarRepository avatarRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private AvatarService out;

    private Student student;
    private Avatar avatar;
    private MultipartFile file;

    @BeforeEach
    void setUp() {

        student = new Student();
        student.setId(1L);
        student.setName("John");
        student.setSurname("Doe");

        avatar = new Avatar();
        avatar.setId(1L);
        avatar.setStudent(student);
        avatar.setFilePath("path/to/avatar.jpg");
        avatar.setFileSize(1024L);
        avatar.setMediaType("image/jpeg");
        avatar.setImageData(new byte[0]);

        ReflectionTestUtils.setField(out, "avatarsPath", "path/to/avatars");

        file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "test image".getBytes());
    }

    @Test
    void test_UploadAvatar() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        try (MockedStatic<ImageUtils> imageUtils = mockStatic(ImageUtils.class)) {
            imageUtils.when(() -> ImageUtils.getPreviewImage(eq(file), anyInt(), anyInt())).thenReturn(new byte[0]);


            out.uploadAvatar(1L, file);
        }
        verify(avatarRepository, times(1)).save(any(Avatar.class));
        assertNotNull(avatar.getFilePath());
        assertEquals(file.getContentType(), avatar.getMediaType());
        assertNotNull(avatar.getImageData());
    }

    @Test
    void test_GetAvatarByStudentId() {
        when(avatarRepository.findByStudentId(1L)).thenReturn(Optional.of(avatar));

        Avatar result = out.getAvatarByStudentId(1L);

        assertEquals(avatar, result);
    }

    @Test
    void test_GetAvatarByStudentIdNotFound() {
        when(avatarRepository.findByStudentId(1L)).thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class, () -> out.getAvatarByStudentId(1L));
    }

    @Test
    void test_getAllAvatars() {
        Page<Avatar> expectedPage = new PageImpl<>(Collections.singletonList(new Avatar()));

        when(avatarRepository.findAll(PageRequest.of(0, 10))).thenReturn(expectedPage);

        Page<Avatar> result = out.getAllAvatars(0, 10);

        verify(avatarRepository, times(1)).findAll(PageRequest.of(0, 10));
        assertEquals(expectedPage, result);

    }
}