package com.eazybytes.accounts.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eazybytes.accounts.entity.User;

@Repository
public interface UserRespository extends JpaRepository<User, UUID>{
    
    Optional<User> findByPhoneNumber(String phoneNumber);
}
