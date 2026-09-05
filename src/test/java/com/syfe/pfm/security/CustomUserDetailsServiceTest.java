package com.syfe.pfm.security;

import com.syfe.pfm.entity.User;
import com.syfe.pfm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void testLoadUserSuccess() {
        User u = new User("test", "pass", "Name", "123");
        u.setId(1L);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(u));
        UserDetails details = service.loadUserByUsername("test");
        assertNotNull(details);
        assertEquals("test", details.getUsername());
    }

    @Test
    void testLoadUserNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
    }
}
