package com.eazybytes.accounts.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eazybytes.accounts.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>{

    User findByEmail(String email);
}
