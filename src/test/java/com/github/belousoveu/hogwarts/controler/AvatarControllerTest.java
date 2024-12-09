package com.github.belousoveu.hogwarts.controler;

import com.github.belousoveu.hogwarts.model.entity.Avatar;
import com.github.belousoveu.hogwarts.service.AvatarService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvatarController.class)
@ActiveProfiles("test")
class AvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    @InjectMocks
    AvatarController avatarController;

    @Value("${avatars.path}")
    String path;


    @Test
    void test_getAllAvatars() throws Exception {

        Page<Avatar> expectedPage = new PageImpl<>(Collections.singletonList(new Avatar()));

        when(avatarService.getAllAvatars(0, 5)).thenReturn(expectedPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatar/all")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0]").exists());

    }

    @Test
    void test_getPreviewAvatar() throws Exception {

        Avatar expectedAvatar = new Avatar();
        expectedAvatar.setId(1L);
        expectedAvatar.setMediaType("image/png");
        expectedAvatar.setImageData(new byte[10]);
        when(avatarService.getAvatarByStudentId(anyLong())).thenReturn(expectedAvatar);

        mockMvc.perform(get("/avatar/{studentId}/preview", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(expectedAvatar.getImageData()));
    }

    @Test
    void test_getAvatarImage() throws Exception {
        long studentId = 1L;
        Avatar expectedAvatar = new Avatar();
        expectedAvatar.setFilePath(path + "/test.png");
        expectedAvatar.setMediaType("image/png");
        expectedAvatar.setFileSize(1024L);

        when(avatarService.getAvatarByStudentId(studentId)).thenReturn(expectedAvatar);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/avatar/{studentId}/image", studentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("image/png"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Length", "1024"))
                .andReturn().getResponse();

    }

    @Test
    void test_uploadAvatar() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/{studentId}", 1L)
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(avatarService, times(1)).uploadAvatar(1L, file);
    }

    @Test
    public void test_uploadAvatar_FileNotSelected() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "", "", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/{studentId}", 1L)
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Файл не выбран"));

        verify(avatarService, never()).uploadAvatar(anyLong(), any(MultipartFile.class));

    }

    @Test
    public void test_uploadAvatar_InternalServerError() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "test data".getBytes());

        doThrow(new IOException("Test exception")).when(avatarService).uploadAvatar(1L, file);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/avatar/{studentId}", 1L)
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Test exception"));

        verify(avatarService, times(1)).uploadAvatar(1L, file);
    }
}