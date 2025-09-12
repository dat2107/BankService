package org.example.bankservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankservice.config.SecurityConfig;
import org.example.bankservice.model.User;
import org.example.bankservice.security.JwtUtil;
import org.example.bankservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserService userService;
    @MockitoBean private UserDetailsService userDetailsService;

    // --- GET /api/users/me ---
    @Test
    @WithMockUser(roles = "ADMIN", username = "alice")
    void getCurrentUser_success() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .principal(() -> "alice")) // giả lập Authentication
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Xin chào, alice")));
    }

    @Test
    @WithAnonymousUser
    void getCurrentUser_fail_noAuth() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andDo(r -> {
                    System.out.println(">>> Status: " + r.getResponse().getStatus());
                    System.out.println(">>> Body: " + r.getResponse().getContentAsString());
                })
                .andExpect(status().isUnauthorized());
    }

    // --- GET /api/users (all) ---
    @Test  @WithMockUser(roles = "ADMIN")
    void getAllUsers_success() throws Exception {
        User u1 = new User(); u1.setUsername("alice");
        User u2 = new User(); u2.setUsername("bob");

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_fail() throws Exception {
        Mockito.when(userService.getAllUsers()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/users"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("DB error"));
    }

    // --- GET /api/users?keyword=xxx ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsers_success() throws Exception {
        User u1 = new User(); u1.setUsername("alice");

        Mockito.when(userService.searchUsers("ali")).thenReturn(List.of(u1));

        mockMvc.perform(get("/api/users").param("keyword", "ali"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test  @WithMockUser(roles = "ADMIN")
    void searchUsers_fail() throws Exception {
        Mockito.when(userService.searchUsers("zzz")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/users").param("keyword", "zzz"))
                .andDo(result -> {
                    System.out.println(">>> Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not found"));
    }
}
