package com.github.belousoveu.hogwarts.service;


import com.github.belousoveu.hogwarts.exception.ImageNotFoundException;
import com.github.belousoveu.hogwarts.exception.StudentNotFoundException;
import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.model.entity.Student;
import com.github.belousoveu.hogwarts.repository.AvatarRepository;
import com.github.belousoveu.hogwarts.repository.StudentRepository;
import com.github.belousoveu.hogwarts.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
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

    public void uploadAvatar(long studentId, MultipartFile file) throws IOException, SQLException {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException(studentId));
        Path filePath = setFullPath(student, file);
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

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setImageData(ImageUtils.getPreviewImage(file, WIDTH_AVATAR, HEIGHT_AVATAR));
        avatarRepository.save(avatar);

    }

    public Avatar getAvatarByStudentId(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> new ImageNotFoundException(studentId));
    }


    private Path setFullPath(Student student, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String ext = "";
        if (fileName != null && fileName.contains(".")) {
            ext = fileName.substring(fileName.lastIndexOf("."));
        }
        return Path.of(avatarsPath, student.getSurname() + student.getName() + ext);
    }
}
