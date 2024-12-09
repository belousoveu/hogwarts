package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.service.AvatarService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/all")
    public Page<Avatar> getAllAvatars(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return avatarService.getAllAvatars(page, size);

    }

    @GetMapping("/{studentId}/preview")
    public ResponseEntity<byte[]> getPreviewAvatar(@PathVariable long studentId) {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getImageData().length);
        return ResponseEntity.ok().headers(headers).body(avatar.getImageData());
    }

    @GetMapping("/{studentId}/image")
    public void getAvatarImage(@PathVariable long studentId, HttpServletResponse response) {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);
        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable long studentId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не выбран");
        }
        try {
            avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.ok().build();
        } catch (SQLException | IOException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
