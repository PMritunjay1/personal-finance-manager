package com.syfe.pfm.service;

import com.syfe.pfm.dto.RegisterRequest;
import com.syfe.pfm.dto.RegisterResponse;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.ConflictException;
import com.syfe.pfm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("a"); req.setPassword("b"); req.setFullName("c"); req.setPhoneNumber("d");
        when(userRepository.existsByUsername("a")).thenReturn(false);
        when(passwordEncoder.encode("b")).thenReturn("enc");
        User u = new User(); u.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(u);
        RegisterResponse res = authService.register(req);
        assertEquals(1L, res.getUserId());
    }

    @Test
    void testRegisterDuplicate() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("a");
        when(userRepository.existsByUsername("a")).thenReturn(true);
        assertThrows(ConflictException.class, () -> authService.register(req));
    }
}
