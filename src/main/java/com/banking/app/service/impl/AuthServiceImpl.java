package com.banking.app.service.impl;

import com.banking.app.dto.UserDto;
import com.banking.app.config.JwtUtil;
import com.banking.app.dto.LoginDto;
import com.banking.app.entity.Account;
import com.banking.app.entity.User;
import com.banking.app.exception.AuthException;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.UserRepository;
import com.banking.app.service.AuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AccountRepository accountRepository ;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager, JwtUtil jwtUtil,AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
		this.accountRepository =accountRepository;
    }

    @Override
    public String register(UserDto userDto) {
    	
    	

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new AuthException("Username already exists");
        }

        User user = new User();
        		user.setName(userDto.getName());  // ✔ name save
        	    user.setUsername(userDto.getUsername());
        	    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        

        User savedUser = userRepository.save(user);

        // 🔥 ACCOUNT AUTO CREATE
        Account account = new Account();
        account.setAccountHolderName(savedUser.getName());
        account.setBalance(0.0);
        account.setUser(savedUser);
        account.setAccountNumber(generateAccountNumber());

        accountRepository.save(account);
        

        
        return "User registered successfully";
    }
    
    private final String generateAccountNumber() {
	    return "ACC" + System.currentTimeMillis();
	}
    
    
    
    @Override
    public String login(LoginDto loginDto) {

        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}