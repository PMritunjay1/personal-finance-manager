package com.syfe.pfm.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransactionUpdateRequest {
    @Positive
    private BigDecimal amount;
    private String description;
    private String category;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
