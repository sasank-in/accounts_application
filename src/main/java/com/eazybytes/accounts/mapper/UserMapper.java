package com.eazybytes.accounts.mapper;

import com.eazybytes.accounts.dto.UserDto;
import com.eazybytes.accounts.entity.User;

public class UserMapper {

    public static UserDto mapToUserDto(User user, UserDto userDto) {
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        return userDto;
    }

    public static User mapToUser(UserDto userDto, User user) {
        user.setUsername(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        // Set a default password hash - in production, this should be properly hashed
        user.setPasswordHash("defaultPassword123");
        return user;
    }
}