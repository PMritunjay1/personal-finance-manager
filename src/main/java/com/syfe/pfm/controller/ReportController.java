package com.syfe.pfm.controller;

import com.syfe.pfm.dto.MonthlyReportResponse;
import com.syfe.pfm.dto.YearlyReportResponse;
import com.syfe.pfm.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@org.springframework.validation.annotation.Validated
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year, 
            @PathVariable @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(12) int month) {
        return ResponseEntity.ok(reportService.getMonthlyReport(year, month));
    }
    
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(@PathVariable int year) {
        return ResponseEntity.ok(reportService.getYearlyReport(year));
    }
}
