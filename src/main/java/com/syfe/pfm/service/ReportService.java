package com.syfe.pfm.service;

import com.syfe.pfm.dto.MonthlyReportResponse;
import com.syfe.pfm.dto.YearlyReportResponse;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public ReportService(TransactionRepository transactionRepository, SecurityUtils securityUtils) {
        this.transactionRepository = transactionRepository;
        this.securityUtils = securityUtils;
    }

    public MonthlyReportResponse getMonthlyReport(int year, int month) {
        Long userId = securityUtils.getCurrentUserId();
        List<Transaction> transactions = transactionRepository.findAllByUserIdAndMonthAndYear(userId, month, year);
        
        Map<String, BigDecimal> income = new HashMap<>();
        Map<String, BigDecimal> expenses = new HashMap<>();
        BigDecimal totalIncomeAmount = BigDecimal.ZERO;
        BigDecimal totalExpenseAmount = BigDecimal.ZERO;
        
        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            if (t.getType() == CategoryType.INCOME) {
                income.put(categoryName, income.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));
                totalIncomeAmount = totalIncomeAmount.add(t.getAmount());
            } else {
                expenses.put(categoryName, expenses.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));
                totalExpenseAmount = totalExpenseAmount.add(t.getAmount());
            }
        }
        
        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);
        
        return new MonthlyReportResponse(month, year, income, expenses, netSavings);
    }
    
    public YearlyReportResponse getYearlyReport(int year) {
        Long userId = securityUtils.getCurrentUserId();
        List<Transaction> transactions = transactionRepository.findAllByUserIdAndYear(userId, year);
        
        Map<String, BigDecimal> income = new HashMap<>();
        Map<String, BigDecimal> expenses = new HashMap<>();
        BigDecimal totalIncomeAmount = BigDecimal.ZERO;
        BigDecimal totalExpenseAmount = BigDecimal.ZERO;
        
        for (Transaction t : transactions) {
            String categoryName = t.getCategory().getName();
            if (t.getType() == CategoryType.INCOME) {
                income.put(categoryName, income.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));
                totalIncomeAmount = totalIncomeAmount.add(t.getAmount());
            } else {
                expenses.put(categoryName, expenses.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));
                totalExpenseAmount = totalExpenseAmount.add(t.getAmount());
            }
        }
        
        BigDecimal netSavings = totalIncomeAmount.subtract(totalExpenseAmount);
        
        return new YearlyReportResponse(year, income, expenses, netSavings);
    }
}
