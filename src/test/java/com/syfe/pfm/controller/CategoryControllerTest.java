package com.syfe.pfm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.pfm.dto.CategoryRequest;
import com.syfe.pfm.dto.CategoryResponse;
import com.syfe.pfm.entity.CategoryType;
import com.syfe.pfm.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateCategory() throws Exception {
        CategoryRequest req = new CategoryRequest();
        req.setName("Bonus");
        req.setType(CategoryType.INCOME);
        
        CategoryResponse res = new CategoryResponse("Bonus", CategoryType.INCOME, true);
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(res);
        
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
