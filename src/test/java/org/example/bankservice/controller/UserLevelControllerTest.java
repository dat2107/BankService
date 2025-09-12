package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.dto.UserLevelDTO;
import org.example.bankservice.model.UserLevel;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.UserLevelService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserLevelController.class)
@Import(SecurityConfig.class)
class UserLevelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserLevelService userLevelService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    // POST /api/userlevel
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void insert_success() throws Exception {
        UserLevelDTO dto = new UserLevelDTO();
        dto.setLevelName("VIP");

        UserLevel saved = new UserLevel();
        saved.setId(1L);
        saved.setLevelName("VIP");

        Mockito.when(userLevelService.insert(Mockito.any(UserLevelDTO.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/userlevel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Thêm thành công")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void insert_fail() throws Exception {
        Mockito.when(userLevelService.insert(Mockito.any(UserLevelDTO.class)))
                .thenThrow(new RuntimeException("Lỗi thêm"));

        mockMvc.perform(post("/api/userlevel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLevelDTO())))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lỗi thêm"));
    }

    // GET /api/userlevel
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAll_success() throws Exception {
        UserLevel l1 = new UserLevel();
        l1.setId(1L);
        l1.setLevelName("VIP");
        UserLevel l2 = new UserLevel();
        l2.setId(2L);
        l2.setLevelName("NORMAL");

        Mockito.when(userLevelService.getAll()).thenReturn(List.of(l1, l2));

        mockMvc.perform(get("/api/userlevel"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].levelName").value("VIP"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].levelName").value("NORMAL"));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAll_fail() throws Exception {
        Mockito.when(userLevelService.getAll())
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/userlevel"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("DB error"));
    }

    // GET /api/userlevel/{id}
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getById_success() throws Exception {
        UserLevel level = new UserLevel();
        level.setId(1L);
        level.setLevelName("VIP");

        Mockito.when(userLevelService.getById(1L)).thenReturn(level);

        mockMvc.perform(get("/api/userlevel/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelName").value("VIP"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getById_fail() throws Exception {
        Mockito.when(userLevelService.getById(99L))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(get("/api/userlevel/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

    // PUT /api/userlevel/{id}
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_success() throws Exception {
        UserLevelDTO dto = new UserLevelDTO();
        dto.setLevelName("VIP-updated");

        UserLevel updated = new UserLevel();
        updated.setId(1L);
        updated.setLevelName("VIP-updated");

        Mockito.when(userLevelService.update(Mockito.eq(1L), Mockito.any(UserLevelDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/userlevel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sửa thành công")));
    }

    @Test
    void update_fail() throws Exception {
        Mockito.when(userLevelService.update(Mockito.eq(1L), Mockito.any(UserLevelDTO.class)))
                .thenThrow(new RuntimeException("Không tìm thấy"));

        mockMvc.perform(put("/api/userlevel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLevelDTO())))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }

    // DELETE /api/userlevel/{id}
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/userlevel/1"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Xóa thành công")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("Không tìm thấy"))
                .when(userLevelService).delete(99L);

        mockMvc.perform(delete("/api/userlevel/99"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy"));
    }
}

