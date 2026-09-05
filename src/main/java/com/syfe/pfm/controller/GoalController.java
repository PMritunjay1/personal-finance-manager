package com.syfe.pfm.controller;

import com.syfe.pfm.dto.GoalListResponse;
import com.syfe.pfm.dto.GoalRequest;
import com.syfe.pfm.dto.GoalResponse;
import com.syfe.pfm.dto.GoalUpdateRequest;
import com.syfe.pfm.dto.MessageResponse;
import com.syfe.pfm.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoal(request));
    }

    @GetMapping
    public ResponseEntity<GoalListResponse> getAllGoals() {
        return ResponseEntity.ok(goalService.getAllGoals());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoal(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id, 
            @Valid @RequestBody GoalUpdateRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(@PathVariable Long id) {
        return ResponseEntity.ok(goalService.deleteGoal(id));
    }
}
