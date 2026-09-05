package com.syfe.pfm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syfe.pfm.dto.GoalRequest;
import com.syfe.pfm.dto.GoalResponse;
import com.syfe.pfm.service.GoalService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private GoalService goalService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateGoal() throws Exception {
        GoalRequest req = new GoalRequest();
        req.setGoalName("Car");
        req.setTargetAmount(new BigDecimal("10000"));
        req.setTargetDate(LocalDate.now().plusYears(1));
        
        GoalResponse res = new GoalResponse(1L, "Car", new BigDecimal("10000"), LocalDate.now().plusYears(1), LocalDate.now(), BigDecimal.ZERO, 0, new BigDecimal("10000"));
        when(goalService.createGoal(any(GoalRequest.class))).thenReturn(res);
        
        mockMvc.perform(post("/api/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
