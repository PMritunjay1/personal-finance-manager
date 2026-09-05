package com.syfe.pfm.service;

import com.syfe.pfm.dto.TransactionRequest;
import com.syfe.pfm.dto.TransactionResponse;
import com.syfe.pfm.dto.TransactionListResponse;
import com.syfe.pfm.dto.TransactionUpdateRequest;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.BadRequestException;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.repository.CategoryRepository;
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
public class TransactionServiceTest {
    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private TransactionService transactionService;
    private User user;
    private Category cat;

    @BeforeEach
    void setUp() { 
        user = new User(); user.setId(1L); 
        cat = new Category("Salary", CategoryType.INCOME, false, null);
    }

    @Test
    void testCreateSuccess() {
        TransactionRequest req = new TransactionRequest(); req.setAmount(new BigDecimal("10")); req.setDate(LocalDate.now()); req.setCategory("Salary");
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByNameAndUserIdOrNotCustom("Salary", 1L)).thenReturn(Optional.of(cat));
        
        Transaction t = new Transaction(); t.setId(1L); t.setAmount(new BigDecimal("10")); t.setCategory(cat); t.setType(CategoryType.INCOME);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(t);
        
        TransactionResponse res = transactionService.createTransaction(req);
        assertEquals(1L, res.getId());
    }

    @Test
    void testCreateFutureDate() {
        TransactionRequest req = new TransactionRequest(); req.setDate(LocalDate.now().plusDays(1));
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void testCreateUserNotFound() {
        TransactionRequest req = new TransactionRequest(); req.setDate(LocalDate.now()); req.setCategory("Salary");
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void testCreateCategoryInvalid() {
        TransactionRequest req = new TransactionRequest(); req.setDate(LocalDate.now()); req.setCategory("Salary");
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByNameAndUserIdOrNotCustom("Salary", 1L)).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(req));
    }

    @Test
    void testGetTransactions() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t = new Transaction(); t.setId(1L); t.setCategory(cat);
        when(transactionRepository.findFilteredTransactions(1L, null, null, null, null)).thenReturn(Collections.singletonList(t));
        TransactionListResponse res = transactionService.getTransactions(null, null, null, null);
        assertEquals(1, res.getTransactions().size());
    }

    @Test
    void testUpdateSuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t = new Transaction(); t.setId(1L); t.setCategory(cat);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(t));
        
        Category cat2 = new Category("Food", CategoryType.EXPENSE, false, null);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("Food", 1L)).thenReturn(Optional.of(cat2));
        when(transactionRepository.save(any())).thenReturn(t);
        
        TransactionUpdateRequest req = new TransactionUpdateRequest();
        req.setAmount(new BigDecimal("20")); req.setDescription("Desc"); req.setCategory("Food");
        TransactionResponse res = transactionService.updateTransaction(1L, req);
        assertEquals("Food", res.getCategory());
        assertEquals("Desc", res.getDescription());
    }

    @Test
    void testUpdateNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(1L, new TransactionUpdateRequest()));
    }
    
    @Test
    void testUpdateInvalidCategory() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t = new Transaction(); t.setId(1L); t.setCategory(cat);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(t));
        when(categoryRepository.findByNameAndUserIdOrNotCustom("Foo", 1L)).thenReturn(Optional.empty());
        TransactionUpdateRequest req = new TransactionUpdateRequest(); req.setCategory("Foo");
        assertThrows(BadRequestException.class, () -> transactionService.updateTransaction(1L, req));
    }

    @Test
    void testDeleteSuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t = new Transaction(); t.setId(1L); t.setCategory(cat);
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(t));
        transactionService.deleteTransaction(1L);
        verify(transactionRepository).delete(t);
    }
}
