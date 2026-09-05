package com.syfe.pfm.service;

import com.syfe.pfm.dto.CategoryRequest;
import com.syfe.pfm.dto.CategoryResponse;
import com.syfe.pfm.dto.CategoryListResponse;
import com.syfe.pfm.dto.MessageResponse;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.ConflictException;
import com.syfe.pfm.exception.ForbiddenException;
import com.syfe.pfm.exception.ResourceNotFoundException;
import com.syfe.pfm.exception.BadRequestException;
import com.syfe.pfm.repository.CategoryRepository;
import com.syfe.pfm.repository.TransactionRepository;
import com.syfe.pfm.repository.UserRepository;
import com.syfe.pfm.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository, UserRepository userRepository, SecurityUtils securityUtils) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public CategoryListResponse getAllCategories() {
        Long userId = securityUtils.getCurrentUserId();
        List<Category> categories = categoryRepository.findAllByUserIdOrNotCustom(userId);
        
        List<CategoryResponse> responses = categories.stream()
            .map(c -> new CategoryResponse(c.getName(), c.getType(), c.getIsCustom()))
            .collect(Collectors.toList());
            
        return new CategoryListResponse(responses);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new ConflictException("Category already exists for this user");
        }
        
        // Also check if default category with same name exists
        if (categoryRepository.findByNameAndUserIdOrNotCustom(request.getName(), null).filter(c -> !c.getIsCustom()).isPresent()) {
            throw new ConflictException("Category name conflicts with a default category");
        }

        Category category = new Category(request.getName(), request.getType(), true, user);
        Category saved = categoryRepository.save(category);
        
        return new CategoryResponse(saved.getName(), saved.getType(), saved.getIsCustom());
    }

    @Transactional
    public MessageResponse deleteCategory(String name) {
        Long userId = securityUtils.getCurrentUserId();
        
        Category category = categoryRepository.findByNameAndUserIdOrNotCustom(name, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            
        if (!category.getIsCustom()) {
            throw new ForbiddenException("Cannot delete a default category");
        }
        
        if (!category.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot delete another user's category");
        }
        
        if (transactionRepository.existsByCategoryId(category.getId())) {
            throw new ConflictException("Cannot delete category currently referenced by transactions");
        }
        
        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }
}
