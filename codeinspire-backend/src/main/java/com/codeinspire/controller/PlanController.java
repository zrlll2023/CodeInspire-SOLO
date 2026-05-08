package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.*;
import com.codeinspire.security.JwtTokenProvider;
import com.codeinspire.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Result<PlanVO> createPlan(@Valid @RequestBody PlanRequest request,
                                     HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        PlanVO plan = planService.createPlan(userId, request);
        return Result.success(plan);
    }

    @GetMapping
    public Result<List<PlanVO>> getPlans(HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        List<PlanVO> plans = planService.getPlans(userId);
        return Result.success(plans);
    }

    @GetMapping("/{planId}")
    public Result<PlanVO> getPlan(@PathVariable Long planId,
                                   HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        PlanVO plan = planService.getPlan(userId, planId);
        return Result.success(plan);
    }

    @PutMapping("/{planId}")
    public Result<PlanVO> updatePlan(@PathVariable Long planId,
                                      @Valid @RequestBody PlanRequest request,
                                      HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        PlanVO plan = planService.updatePlan(userId, planId, request);
        return Result.success(plan);
    }

    @DeleteMapping("/{planId}")
    public Result<Void> deletePlan(@PathVariable Long planId,
                                    HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        planService.deletePlan(userId, planId);
        return Result.success();
    }

    @PostMapping("/{planId}/tasks")
    public Result<PlanVO.TaskVO> addTask(@PathVariable Long planId,
                                         @Valid @RequestBody TaskRequest request,
                                         HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        request.setPlanId(planId);
        PlanVO.TaskVO task = planService.addTask(userId, planId, request);
        return Result.success(task);
    }

    @GetMapping("/{planId}/tasks")
    public Result<List<PlanVO.TaskVO>> getTasks(@PathVariable Long planId,
                                                 HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        List<PlanVO.TaskVO> tasks = planService.getTasks(userId, planId);
        return Result.success(tasks);
    }

    @PutMapping("/tasks/{taskId}")
    public Result<PlanVO.TaskVO> updateTask(@PathVariable Long taskId,
                                            @Valid @RequestBody TaskRequest request,
                                            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        PlanVO.TaskVO task = planService.updateTask(userId, taskId, request);
        return Result.success(task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId,
                                    HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        planService.deleteTask(userId, taskId);
        return Result.success();
    }

    @PatchMapping("/tasks/{taskId}/status")
    public Result<Void> updateTaskStatus(@PathVariable Long taskId,
                                          @RequestParam String status,
                                          HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        planService.updateTaskStatus(userId, taskId, status);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
