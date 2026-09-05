package com.syfe.pfm.service;

import com.syfe.pfm.dto.CategoryRequest;
import com.syfe.pfm.dto.CategoryResponse;
import com.syfe.pfm.dto.CategoryListResponse;
import com.syfe.pfm.entity.Category;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.entity.User;
import com.syfe.pfm.exception.ConflictException;
import com.syfe.pfm.exception.ForbiddenException;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private CategoryService categoryService;
    private User user;

    @BeforeEach
    void setUp() { user = new User(); user.setId(1L); }

    @Test
    void testGetAllCategories() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Category c = new Category("C1", CategoryType.INCOME, true, user);
        when(categoryRepository.findAllByUserIdOrNotCustom(1L)).thenReturn(Collections.singletonList(c));
        CategoryListResponse res = categoryService.getAllCategories();
        assertEquals(1, res.getCategories().size());
        assertEquals("C1", res.getCategories().get(0).getName());
        assertEquals(CategoryType.INCOME, res.getCategories().get(0).getType());
    }

    @Test
    void testCreateCategorySuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUserId("C1", 1L)).thenReturn(false);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", null)).thenReturn(Optional.empty());
        
        Category c = new Category("C1", CategoryType.INCOME, true, user);
        c.setId(1L);
        when(categoryRepository.save(any(Category.class))).thenReturn(c);
        
        CategoryRequest req = new CategoryRequest(); req.setName("C1"); req.setType(CategoryType.INCOME);
        CategoryResponse res = categoryService.createCategory(req);
        assertEquals("C1", res.getName());
    }

    @Test
    void testCreateCategoryUserNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.createCategory(new CategoryRequest()));
    }

    @Test
    void testCreateCategoryDuplicateNameForUser() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUserId("C1", 1L)).thenReturn(true);
        CategoryRequest req = new CategoryRequest(); req.setName("C1");
        assertThrows(ConflictException.class, () -> categoryService.createCategory(req));
    }

    @Test
    void testCreateCategoryDuplicateDefaultName() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUserId("C1", 1L)).thenReturn(false);
        Category def = new Category("C1", CategoryType.INCOME, false, null);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", null)).thenReturn(Optional.of(def));
        CategoryRequest req = new CategoryRequest(); req.setName("C1");
        assertThrows(ConflictException.class, () -> categoryService.createCategory(req));
    }

    @Test
    void testDeleteCategorySuccess() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Category c = new Category("C1", CategoryType.INCOME, true, user); c.setId(1L);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", 1L)).thenReturn(Optional.of(c));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);
        categoryService.deleteCategory("C1");
        verify(categoryRepository).delete(c);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory("C1"));
    }

    @Test
    void testDeleteCategoryDefault() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Category c = new Category("C1", CategoryType.INCOME, false, null);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", 1L)).thenReturn(Optional.of(c));
        assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory("C1"));
    }

    @Test
    void testDeleteCategoryOtherUser() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        User u2 = new User(); u2.setId(2L);
        Category c = new Category("C1", CategoryType.INCOME, true, u2);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", 1L)).thenReturn(Optional.of(c));
        assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory("C1"));
    }

    @Test
    void testDeleteCategoryInUse() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        Category c = new Category("C1", CategoryType.INCOME, true, user); c.setId(1L);
        when(categoryRepository.findByNameAndUserIdOrNotCustom("C1", 1L)).thenReturn(Optional.of(c));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> categoryService.deleteCategory("C1"));
    }
}
