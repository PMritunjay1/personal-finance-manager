package com.syfe.pfm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.pfm.dto.TransactionRequest;
import com.syfe.pfm.dto.TransactionResponse;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateTransaction() throws Exception {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(new BigDecimal("100"));
        req.setDate(LocalDate.now());
        req.setCategory("Food");
        
        TransactionResponse res = new TransactionResponse(1L, new BigDecimal("100"), LocalDate.now(), "Food", "", CategoryType.EXPENSE);
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(res);
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @WithMockUser
    void testCreateTransactionInvalidAmount() throws Exception {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(new BigDecimal("-100")); // invalid
        req.setDate(LocalDate.now());
        req.setCategory("Food");
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
