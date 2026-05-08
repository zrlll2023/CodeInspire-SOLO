package com.codeinspire.service;

import com.codeinspire.dto.*;
import java.util.List;

public interface PlanService {
    PlanVO createPlan(Long userId, PlanRequest request);
    List<PlanVO> getPlans(Long userId);
    PlanVO getPlan(Long userId, Long planId);
    PlanVO updatePlan(Long userId, Long planId, PlanRequest request);
    void deletePlan(Long userId, Long planId);
    
    TaskVO addTask(Long userId, Long planId, TaskRequest request);
    List<TaskVO> getTasks(Long userId, Long planId);
    TaskVO updateTask(Long userId, Long taskId, TaskRequest request);
    void deleteTask(Long userId, Long taskId);
    void updateTaskStatus(Long userId, Long taskId, String status);
}
