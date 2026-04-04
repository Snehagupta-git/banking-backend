package com.banking.app.service;

import com.banking.app.dto.UserDto;
import com.banking.app.dto.LoginDto;

public interface AuthService {

    String register(UserDto userDto);

    String login(LoginDto loginDto);
}
