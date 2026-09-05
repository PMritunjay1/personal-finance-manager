package com.syfe.pfm.dto;

import com.syfe.pfm.entity.CategoryType;

public class CategoryResponse {
    private String name;
    private CategoryType type;
    private boolean isCustom;

    public CategoryResponse(String name, CategoryType type, boolean isCustom) {
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
    
    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
}
