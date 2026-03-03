package com.eazybytes.accounts.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eazybytes.accounts.entity.Account;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long>{
    
    Optional<Account> findByUserId(UUID userId);
}
