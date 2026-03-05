package com.eazybytes.accounts.service.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eazybytes.accounts.dto.UserRequestDTO;
import com.eazybytes.accounts.dto.UserResponseDTO;
import com.eazybytes.accounts.entity.User;
import com.eazybytes.accounts.repository.UserRepository;
import com.eazybytes.accounts.service.IUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = new User();

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User with this email already exists"
            );
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User savedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        return response;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(UUID.fromString(id.toString())).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        
        return response;
    }

}
