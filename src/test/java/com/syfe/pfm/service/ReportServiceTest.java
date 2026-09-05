package com.syfe.pfm.service;

import com.syfe.pfm.dto.MonthlyReportResponse;
import com.syfe.pfm.dto.YearlyReportResponse;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock private TransactionRepository transactionRepository;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private ReportService reportService;

    @Test
    void testGetMonthlyReport() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t1 = new Transaction(); t1.setAmount(new BigDecimal("100")); t1.setType(CategoryType.INCOME); Category c1 = new Category(); c1.setName("C1"); t1.setCategory(c1);
        Transaction t2 = new Transaction(); t2.setAmount(new BigDecimal("100")); t2.setType(CategoryType.INCOME); t2.setCategory(c1);
        Transaction t3 = new Transaction(); t3.setAmount(new BigDecimal("50")); t3.setType(CategoryType.EXPENSE); Category c2 = new Category(); c2.setName("C2"); t3.setCategory(c2);
        
        when(transactionRepository.findAllByUserIdAndMonthAndYear(1L, 1, 2024)).thenReturn(Arrays.asList(t1, t2, t3));
        
        MonthlyReportResponse res = reportService.getMonthlyReport(2024, 1);
        assertEquals(new BigDecimal("150"), res.getNetSavings());
        assertEquals(new BigDecimal("200"), res.getTotalIncome().get("C1"));
        assertEquals(new BigDecimal("50"), res.getTotalExpenses().get("C2"));
        
        // Coverage for getters/setters of DTO
        assertEquals(2024, res.getYear());
        assertEquals(1, res.getMonth());
    }

    @Test
    void testGetYearlyReport() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Transaction t1 = new Transaction(); t1.setAmount(new BigDecimal("100")); t1.setType(CategoryType.INCOME); Category c1 = new Category(); c1.setName("C1"); t1.setCategory(c1);
        Transaction t3 = new Transaction(); t3.setAmount(new BigDecimal("50")); t3.setType(CategoryType.EXPENSE); Category c2 = new Category(); c2.setName("C2"); t3.setCategory(c2);
        
        when(transactionRepository.findAllByUserIdAndYear(1L, 2024)).thenReturn(Arrays.asList(t1, t3));
        
        YearlyReportResponse res = reportService.getYearlyReport(2024);
        assertEquals(new BigDecimal("50"), res.getNetSavings());
        assertEquals(new BigDecimal("100"), res.getTotalIncome().get("C1"));
        assertEquals(new BigDecimal("50"), res.getTotalExpenses().get("C2"));
        assertEquals(2024, res.getYear());
    }
}
