package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.UserRequestDto;
import com.eazybytes.accounts.dto.UserResponseDto;
import java.util.List;
import java.util.UUID;

public interface UserService{
    UserResponseDto createUser(UserRequestDto dto);
    UserResponseDto findUserById(UUID id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(UUID id, UserRequestDto dto);
    void deleteUser(UUID id);
}
