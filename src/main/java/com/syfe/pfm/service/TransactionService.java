package com.syfe.pfm.service;

import com.syfe.pfm.dto.TransactionRequest;
import com.syfe.pfm.dto.TransactionResponse;
import com.syfe.pfm.dto.TransactionListResponse;
import com.syfe.pfm.dto.TransactionUpdateRequest;
import com.syfe.pfm.dto.MessageResponse;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.exception.BadRequestException;
import com.syfe.pfm.repository.CategoryRepository;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.repository.UserRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Transaction date cannot be in the future");
        }
        
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        Category category = categoryRepository.findByNameAndUserIdOrNotCustom(request.getCategory(), userId)
            .orElseThrow(() -> new BadRequestException("Invalid category"));
            
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);
        transaction.setType(category.getType());
        transaction.setUser(user);
        
        Transaction saved = transactionRepository.save(transaction);
        
        return mapToResponse(saved);
    }
    
    public TransactionListResponse getTransactions(LocalDate startDate, LocalDate endDate, String categoryName, CategoryType type) {
        Long userId = securityUtils.getCurrentUserId();
        List<Transaction> transactions = transactionRepository.findFilteredTransactions(userId, startDate, endDate, categoryName, type);
        
        List<TransactionResponse> responses = transactions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        return new TransactionListResponse(responses);
    }
    
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
            
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findByNameAndUserIdOrNotCustom(request.getCategory(), userId)
                .orElseThrow(() -> new BadRequestException("Invalid category"));
            transaction.setCategory(category);
            transaction.setType(category.getType());
        }
        
        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }
    
    @Transactional
    public MessageResponse deleteTransaction(Long id) {
        Long userId = securityUtils.getCurrentUserId();
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
            
        transactionRepository.delete(transaction);
        return new MessageResponse("Transaction deleted successfully");
    }
    
    private TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
            t.getId(),
            t.getAmount(),
            t.getDate(),
            t.getCategory().getName(),
            t.getDescription(),
            t.getType()
        );
    }
}
