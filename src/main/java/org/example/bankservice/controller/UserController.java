package org.example.bankservice.controller;

import org.example.bankservice.model.User;
import org.example.bankservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public String getCurrentUser(Authentication authentication) {
        return "Xin chào, " + authentication.getName();
    }


    @GetMapping
    public List<User> getAll(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return userService.searchUsers(keyword);
        }
        return userService.getAllUsers();
    }

}
