package com.eazybytes.accounts.dto;
import java.util.UUID;

import lombok.Data;

@Data
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;

}
