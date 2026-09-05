package com.syfe.pfm.service;

import com.syfe.pfm.dto.GoalRequest;
import com.syfe.pfm.dto.GoalResponse;
import com.syfe.pfm.dto.GoalListResponse;
import com.syfe.pfm.dto.GoalUpdateRequest;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Goal;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.BadRequestException;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.repository.GoalRepository;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.repository.UserRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock private GoalRepository goalRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private GoalService goalService;
    private User user;

    @BeforeEach
    void setUp() { user = new User(); user.setId(1L); }

    @Test
    void testCreateSuccess() {
        GoalRequest req = new GoalRequest(); req.setTargetDate(LocalDate.now().plusDays(10)); req.setTargetAmount(BigDecimal.TEN);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        Goal g = new Goal(); g.setUser(user); g.setTargetAmount(BigDecimal.TEN); g.setStartDate(LocalDate.now());
        when(goalRepository.save(any())).thenReturn(g);
        
        GoalResponse res = goalService.createGoal(req);
        assertNotNull(res);
    }
    
    @Test
    void testCreateUserNotFound() {
        GoalRequest req = new GoalRequest(); req.setTargetDate(LocalDate.now().plusDays(10)); req.setTargetAmount(BigDecimal.TEN);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> goalService.createGoal(req));
    }

    @Test
    void testCreatePastDate() {
        GoalRequest req = new GoalRequest(); req.setTargetDate(LocalDate.now().minusDays(10));
        assertThrows(BadRequestException.class, () -> goalService.createGoal(req));
    }

    @Test
    void testGetAllGoals() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Goal g = new Goal(); g.setUser(user); g.setTargetAmount(BigDecimal.TEN); g.setStartDate(LocalDate.now());
        when(goalRepository.findAllByUserId(1L)).thenReturn(Collections.singletonList(g));
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(any(), any(), any())).thenReturn(null);
        GoalListResponse res = goalService.getAllGoals();
        assertEquals(1, res.getGoals().size());
        assertEquals(BigDecimal.ZERO, res.getGoals().get(0).getCurrentProgress());
        assertEquals(0.0, res.getGoals().get(0).getProgressPercentage());
    }

    @Test
    void testGetGoalProgressMath() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Goal g = new Goal(); g.setUser(user); g.setTargetAmount(new BigDecimal("100")); g.setStartDate(LocalDate.now());
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(g));
        
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(1L, CategoryType.INCOME, g.getStartDate())).thenReturn(new BigDecimal("150"));
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(1L, CategoryType.EXPENSE, g.getStartDate())).thenReturn(new BigDecimal("100"));
        
        GoalResponse res = goalService.getGoal(1L);
        assertEquals(new BigDecimal("50"), res.getCurrentProgress());
        assertEquals(50.0, res.getProgressPercentage());
        assertEquals(new BigDecimal("50"), res.getRemainingAmount());
    }

    @Test
    void testGetGoalProgressMathOverTarget() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Goal g = new Goal(); g.setUser(user); g.setTargetAmount(new BigDecimal("100")); g.setStartDate(LocalDate.now());
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(g));
        
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(1L, CategoryType.INCOME, g.getStartDate())).thenReturn(new BigDecimal("200"));
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(1L, CategoryType.EXPENSE, g.getStartDate())).thenReturn(new BigDecimal("0"));
        
        GoalResponse res = goalService.getGoal(1L);
        assertEquals(new BigDecimal("200"), res.getCurrentProgress());
        assertEquals(200.0, res.getProgressPercentage());
        assertEquals(new BigDecimal("0"), res.getRemainingAmount());
    }

    @Test
    void testGetGoalNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> goalService.getGoal(1L));
    }

    @Test
    void testUpdateSuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Goal g = new Goal(); g.setUser(user); g.setTargetAmount(new BigDecimal("100")); g.setStartDate(LocalDate.now());
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(g));
        when(goalRepository.save(any())).thenReturn(g);
        GoalUpdateRequest req = new GoalUpdateRequest(); req.setTargetDate(LocalDate.now().plusDays(10)); req.setTargetAmount(new BigDecimal("200"));
        GoalResponse res = goalService.updateGoal(1L, req);
        assertEquals(new BigDecimal("200"), res.getTargetAmount());
    }

    @Test
    void testUpdatePastDate() {
        GoalUpdateRequest req = new GoalUpdateRequest(); req.setTargetDate(LocalDate.now().minusDays(10));
        assertThrows(BadRequestException.class, () -> goalService.updateGoal(1L, req));
    }

    @Test
    void testDeleteSuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Goal g = new Goal(); g.setUser(user);
        when(goalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(g));
        goalService.deleteGoal(1L);
        verify(goalRepository).delete(g);
    }
}
