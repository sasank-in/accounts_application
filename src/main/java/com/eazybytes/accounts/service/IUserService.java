package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.UserRequestDTO;
import com.eazybytes.accounts.dto.UserResponseDTO;

public interface IUserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    UserResponseDTO getUserById(Long id);
}
