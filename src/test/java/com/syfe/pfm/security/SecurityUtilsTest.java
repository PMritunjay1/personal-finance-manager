package com.syfe.pfm.security;

import com.syfe.pfm.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityUtilsTest {
    private SecurityUtils securityUtils;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserIdSuccess() {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        CustomUserDetails principal = new CustomUserDetails(1L, "user", "pass");
        when(auth.getPrincipal()).thenReturn(principal);

        Long id = securityUtils.getCurrentUserId();
        assertEquals(1L, id);
        
        // cover CustomUserDetails getters
        assertEquals("user", principal.getUsername());
        assertEquals("pass", principal.getPassword());
        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
        assertTrue(principal.isEnabled());
        assertTrue(principal.getAuthorities().isEmpty());
    }

    @Test
    void testGetCurrentUserIdNoAuth() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThrows(UnauthorizedException.class, () -> securityUtils.getCurrentUserId());
    }

    @Test
    void testGetCurrentUserIdNotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> securityUtils.getCurrentUserId());
    }

    @Test
    void testGetCurrentUserIdInvalidPrincipal() {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("anonymousUser");
        assertThrows(UnauthorizedException.class, () -> securityUtils.getCurrentUserId());
    }
}
