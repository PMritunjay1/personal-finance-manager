package com.syfe.pfm.dto;

import com.syfe.pfm.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {
    @NotBlank
    private String name;
    
    @NotNull
    private CategoryType type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
}
