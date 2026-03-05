package com.eazybytes.accounts.controller;

import org.springframework.web.bind.annotation.*;
import com.eazybytes.accounts.dto.UserRequestDTO;
import com.eazybytes.accounts.dto.UserResponseDTO;

import org.springframework.http.MediaType;
import com.eazybytes.accounts.service.IUserService;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/create")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto) {
        return userService.createUser(dto);
    }

}
