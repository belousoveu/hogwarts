package com.github.belousoveu.hogwarts.service;


import com.github.belousoveu.hogwarts.exception.FileSavingException;
import com.github.belousoveu.hogwarts.exception.ImageNotFoundException;
import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.AvatarRepository;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import com.github.belousoveu.hogwarts.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Slf4j
public class AvatarService {

    private static final int WIDTH_AVATAR = 150;
    private static final int HEIGHT_AVATAR = 150;

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    private final String avatarsPath;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository,
                         @Qualifier("avatarPath") String avatarsPath) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.avatarsPath = avatarsPath;
    }

    public void uploadAvatar(long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException(studentId));
        Path filePath = Path.of(avatarsPath, file.getOriginalFilename());
        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);
            try (
                    InputStream is = file.getInputStream();
                    OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                    BufferedInputStream bis = new BufferedInputStream(is, 1024);
                    BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
            ) {
                bis.transferTo(bos);
            }

            Avatar avatar = new Avatar();

            avatar.setStudent(student);
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());
            avatar.setImageData(ImageUtils.getPreviewImage(file, WIDTH_AVATAR, HEIGHT_AVATAR));
            avatarRepository.save(avatar);
        } catch (IOException e) {
            throw new FileSavingException(e);
        }

        log.info("Avatar for student {} has been uploaded", student);

    }

    public Avatar getAvatarByStudentId(long studentId) {
        log.debug("Was trying to get avatar for student with id {}", studentId);
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> new ImageNotFoundException(studentId));
    }



    public Page<Avatar> getAllAvatars(int page, int size) {
        log.debug("Was trying to get all avatars");
        return avatarRepository.findAll(PageRequest.of(page, size));
    }
}
