package com.eazybytes.accounts.controller;

import org.springframework.web.bind.annotation.*;

import com.eazybytes.accounts.dto.UserRequestDto;
import com.eazybytes.accounts.dto.UserResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.eazybytes.accounts.service.UserService;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {

        UserResponseDto response = userService.createUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable java.util.UUID id){
        UserResponseDto response = userService.findUserById(id);
        return ResponseEntity
                .ok(response);
    }
}
