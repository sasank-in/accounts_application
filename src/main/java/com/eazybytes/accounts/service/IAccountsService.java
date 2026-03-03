package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.UserDto;

public interface IAccountsService {

    /**
     * Create a new account
     * @param userDto - UserDto Object
     */
    void createAccount(UserDto userDto);

    /**
     * Fetch account details
     * @param phoneNumber - Input phone number
     * @return Account Details based on a given phone number
     */
    UserDto fetchAccount(String phoneNumber);
}