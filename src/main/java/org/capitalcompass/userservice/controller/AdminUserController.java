package org.capitalcompass.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.dto.UserDTO;
import org.capitalcompass.userservice.service.AdminUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users/admin-users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return adminUserService.getUsers();
    }

}