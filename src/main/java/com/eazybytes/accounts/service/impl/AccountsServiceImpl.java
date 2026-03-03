package com.eazybytes.accounts.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.eazybytes.accounts.dto.UserDto;
import com.eazybytes.accounts.entity.Account;
import com.eazybytes.accounts.entity.AccountType;
import com.eazybytes.accounts.entity.User;
import com.eazybytes.accounts.entity.UserRole;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.exception.UserAlreadyExistsException;
import com.eazybytes.accounts.mapper.UserMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.UserRespository;
import com.eazybytes.accounts.service.IAccountsService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private AccountsRepository accountsRepository;
    private UserRespository userRepository;

    @Override
    public void createAccount(UserDto userDto) {
        // Check if user already exists
        Optional<User> optionalUser = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already registered with given phone number: " 
                + userDto.getPhoneNumber());
        }

        // Create new user
        User user = UserMapper.mapToUser(userDto, new User());
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);

        // Create new account
        Account account = createNewAccount(savedUser);
        accountsRepository.save(account);
    }

    @Override
    public UserDto fetchAccount(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new ResourceNotFoundException("User", "phoneNumber", phoneNumber));
        
        Account account = accountsRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", "userId", user.getId().toString()));

        UserDto userDto = UserMapper.mapToUserDto(user, new UserDto());
        userDto.setAccountNumber(account.getAccountNumber());
        userDto.setAccountType(account.getAccountType().name());
        
        return userDto;
    }

    /**
     * Create a new account with random account number
     * @param user - User Object
     * @return the new account details
     */
    private Account createNewAccount(User user) {
        Account newAccount = new Account();
        newAccount.setUser(user);
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        
        newAccount.setAccountNumber(Long.toString(randomAccNumber));
        newAccount.setAccountType(AccountType.SAVINGS);
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCurrency("INR");
        
        return newAccount;
    }
}