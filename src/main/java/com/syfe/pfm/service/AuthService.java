package com.syfe.pfm.service;

import com.syfe.pfm.dto.RegisterRequest;
import com.syfe.pfm.dto.RegisterResponse;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.ConflictException;
import com.syfe.pfm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already taken");
        }

        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getFullName(),
            request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);
        return new RegisterResponse("User registered successfully", savedUser.getId());
    }
}
