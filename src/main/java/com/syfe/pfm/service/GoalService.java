package com.syfe.pfm.service;

import com.syfe.pfm.dto.GoalRequest;
import com.syfe.pfm.dto.GoalResponse;
import com.syfe.pfm.dto.GoalListResponse;
import com.syfe.pfm.dto.GoalUpdateRequest;
import com.syfe.pfm.dto.MessageResponse;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Goal;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.BadRequestException;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.repository.GoalRepository;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.repository.UserRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public GoalService(GoalRepository goalRepository, TransactionRepository transactionRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public GoalResponse createGoal(GoalRequest request) {
        if (!request.getTargetDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future");
        }
        
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        Goal goal = new Goal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        goal.setUser(user);
        
        Goal saved = goalRepository.save(goal);
        return mapToResponse(saved);
    }
    
    public GoalListResponse getAllGoals() {
        Long userId = securityUtils.getCurrentUserId();
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        
        List<GoalResponse> responses = goals.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        return new GoalListResponse(responses);
    }
    
    public GoalResponse getGoal(Long id) {
        Long userId = securityUtils.getCurrentUserId();
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            
        return mapToResponse(goal);
    }
    
    @Transactional
    public GoalResponse updateGoal(Long id, GoalUpdateRequest request) {
        if (request.getTargetDate() != null && !request.getTargetDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future");
        }
        
        Long userId = securityUtils.getCurrentUserId();
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        
        Goal saved = goalRepository.save(goal);
        return mapToResponse(saved);
    }
    
    @Transactional
    public MessageResponse deleteGoal(Long id) {
        Long userId = securityUtils.getCurrentUserId();
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            
        goalRepository.delete(goal);
        return new MessageResponse("Goal deleted successfully");
    }
    
    private GoalResponse mapToResponse(Goal goal) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(
            goal.getUser().getId(), CategoryType.INCOME, goal.getStartDate()
        );
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateGreaterThanEqual(
            goal.getUser().getId(), CategoryType.EXPENSE, goal.getStartDate()
        );
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;
        
        BigDecimal currentProgress = totalIncome.subtract(totalExpense);
        
        double progressPercentage = 0.0;
        BigDecimal remainingAmount = goal.getTargetAmount();
        
        if (currentProgress.compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = currentProgress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).doubleValue();
                
            remainingAmount = goal.getTargetAmount().subtract(currentProgress).max(BigDecimal.ZERO);
        }
        
        return new GoalResponse(
            goal.getId(),
            goal.getGoalName(),
            goal.getTargetAmount(),
            goal.getTargetDate(),
            goal.getStartDate(),
            currentProgress,
            progressPercentage,
            remainingAmount
        );
    }
}
