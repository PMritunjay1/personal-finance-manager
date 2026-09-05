package com.syfe.pfm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.pfm.dto.*;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.service.*;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AllControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CategoryService categoryService;
    @MockBean private TransactionService transactionService;
    @MockBean private GoalService goalService;
    @MockBean private ReportService reportService;
    @MockBean private AuthService authService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCategoryController() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(new CategoryListResponse(Collections.emptyList()));
        mockMvc.perform(get("/api/categories")).andExpect(status().isOk());

        CategoryRequest req = new CategoryRequest(); req.setName("N"); req.setType(CategoryType.INCOME);
        when(categoryService.createCategory(any())).thenReturn(new CategoryResponse("N", CategoryType.INCOME, true));
        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated());

        when(categoryService.deleteCategory("N")).thenReturn(new MessageResponse("OK"));
        mockMvc.perform(delete("/api/categories/N")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testTransactionController() throws Exception {
        when(transactionService.getTransactions(any(), any(), any(), any())).thenReturn(new TransactionListResponse(Collections.emptyList()));
        mockMvc.perform(get("/api/transactions?startDate=2024-01-01&categoryId=1")).andExpect(status().isOk());

        TransactionRequest req = new TransactionRequest(); req.setAmount(BigDecimal.TEN); req.setDate(LocalDate.now()); req.setCategory("C");
        when(transactionService.createTransaction(any())).thenReturn(new TransactionResponse(1L, BigDecimal.TEN, LocalDate.now(), "C", "", CategoryType.INCOME));
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated());

        TransactionUpdateRequest ureq = new TransactionUpdateRequest(); ureq.setAmount(BigDecimal.ONE);
        when(transactionService.updateTransaction(eq(1L), any())).thenReturn(new TransactionResponse(1L, BigDecimal.ONE, LocalDate.now(), "C", "", CategoryType.INCOME));
        mockMvc.perform(put("/api/transactions/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ureq)))
               .andExpect(status().isOk());

        when(transactionService.deleteTransaction(1L)).thenReturn(new MessageResponse("OK"));
        mockMvc.perform(delete("/api/transactions/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGoalController() throws Exception {
        when(goalService.getAllGoals()).thenReturn(new GoalListResponse(Collections.emptyList()));
        mockMvc.perform(get("/api/goals")).andExpect(status().isOk());

        when(goalService.getGoal(1L)).thenReturn(new GoalResponse(1L, "N", BigDecimal.TEN, LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, 0, BigDecimal.TEN));
        mockMvc.perform(get("/api/goals/1")).andExpect(status().isOk());

        GoalRequest req = new GoalRequest(); req.setGoalName("N"); req.setTargetAmount(BigDecimal.TEN); req.setTargetDate(LocalDate.now().plusDays(1));
        when(goalService.createGoal(any())).thenReturn(new GoalResponse(1L, "N", BigDecimal.TEN, LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, 0, BigDecimal.TEN));
        mockMvc.perform(post("/api/goals").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated());

        GoalUpdateRequest ureq = new GoalUpdateRequest(); ureq.setTargetAmount(BigDecimal.ONE);
        when(goalService.updateGoal(eq(1L), any())).thenReturn(new GoalResponse(1L, "N", BigDecimal.ONE, LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, 0, BigDecimal.ONE));
        mockMvc.perform(put("/api/goals/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(ureq)))
               .andExpect(status().isOk());

        when(goalService.deleteGoal(1L)).thenReturn(new MessageResponse("OK"));
        mockMvc.perform(delete("/api/goals/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testReportController() throws Exception {
        when(reportService.getMonthlyReport(2024, 1)).thenReturn(new MonthlyReportResponse(1, 2024, Collections.emptyMap(), Collections.emptyMap(), BigDecimal.ZERO));
        mockMvc.perform(get("/api/reports/monthly/2024/1")).andExpect(status().isOk());

        when(reportService.getYearlyReport(2024)).thenReturn(new YearlyReportResponse(2024, Collections.emptyMap(), Collections.emptyMap(), BigDecimal.ZERO));
        mockMvc.perform(get("/api/reports/yearly/2024")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testAuthControllerLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")).andExpect(status().isOk());
    }
}
