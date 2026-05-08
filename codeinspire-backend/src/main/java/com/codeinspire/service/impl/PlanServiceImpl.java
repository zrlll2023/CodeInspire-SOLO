package com.codeinspire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeinspire.dto.*;
import com.codeinspire.entity.Plan;
import com.codeinspire.entity.Task;
import com.codeinspire.exception.BusinessException;
import com.codeinspire.repository.PlanMapper;
import com.codeinspire.repository.TaskMapper;
import com.codeinspire.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;
    private final TaskMapper taskMapper;

    @Override
    public PlanVO createPlan(Long userId, PlanRequest request) {
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setStatus("active");
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        
        planMapper.insert(plan);
        
        return convertToVO(plan);
    }

    @Override
    public List<PlanVO> getPlans(Long userId) {
        List<Plan> plans = planMapper.selectList(
                new LambdaQueryWrapper<Plan>()
                        .eq(Plan::getUserId, userId)
                        .orderByDesc(Plan::getCreatedAt)
        );
        
        return plans.stream()
                .map(this::convertToVOWithTasks)
                .collect(Collectors.toList());
    }

    @Override
    public PlanVO getPlan(Long userId, Long planId) {
        Plan plan = validatePlanOwnership(userId, planId);
        return convertToVOWithTasks(plan);
    }

    @Override
    public PlanVO updatePlan(Long userId, Long planId, PlanRequest request) {
        Plan plan = validatePlanOwnership(userId, planId);
        
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        if (request.getStartDate() != null) {
            plan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            plan.setEndDate(request.getEndDate());
        }
        
        planMapper.updateById(plan);
        
        return convertToVOWithTasks(plan);
    }

    @Override
    public void deletePlan(Long userId, Long planId) {
        validatePlanOwnership(userId, planId);
        
        taskMapper.delete(
                new LambdaQueryWrapper<Task>().eq(Task::getPlanId, planId)
        );
        
        planMapper.deleteById(planId);
    }

    @Override
    public TaskVO addTask(Long userId, Long planId, TaskRequest request) {
        validatePlanOwnership(userId, planId);
        
        Task task = new Task();
        task.setPlanId(planId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus("pending");
        
        taskMapper.insert(task);
        
        return convertTaskToVO(task);
    }

    @Override
    public List<TaskVO> getTasks(Long userId, Long planId) {
        validatePlanOwnership(userId, planId);
        
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getPlanId, planId)
                        .orderByAsc(Task::getPriority)
                        .orderByAsc(Task::getDueDate)
        );
        
        return tasks.stream()
                .map(this::convertTaskToVO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskVO updateTask(Long userId, Long taskId, TaskRequest request) {
        Task task = validateTaskOwnership(userId, taskId);
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        taskMapper.updateById(task);
        
        return convertTaskToVO(task);
    }

    @Override
    public void deleteTask(Long userId, Long taskId) {
        validateTaskOwnership(userId, taskId);
        taskMapper.deleteById(taskId);
    }

    @Override
    public void updateTaskStatus(Long userId, Long taskId, String status) {
        Task task = validateTaskOwnership(userId, taskId);
        
        if ("completed".equals(status)) {
            task.setStatus("completed");
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setStatus(status);
            task.setCompletedAt(null);
        }
        
        taskMapper.updateById(task);
    }

    private Plan validatePlanOwnership(Long userId, Long planId) {
        Plan plan = planMapper.selectById(planId);
        
        if (plan == null) {
            throw new BusinessException(404, "规划不存在");
        }
        
        if (!plan.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此规划");
        }
        
        return plan;
    }

    private Task validateTaskOwnership(Long userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        
        Plan plan = planMapper.selectById(task.getPlanId());
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此任务");
        }
        
        return task;
    }

    private PlanVO convertToVO(Plan plan) {
        return PlanVO.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .status(plan.getStatus())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private PlanVO convertToVOWithTasks(Plan plan) {
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getPlanId, plan.getId())
                        .orderByAsc(Task::getPriority)
        );
        
        List<PlanVO.TaskVO> taskVOs = tasks.stream()
                .map(this::convertTaskToVO)
                .collect(Collectors.toList());
        
        PlanVO vo = convertToVO(plan);
        vo.setTasks(taskVOs);
        
        return vo;
    }

    private PlanVO.TaskVO convertTaskToVO(Task task) {
        return PlanVO.TaskVO.builder()
                .id(task.getId())
                .planId(task.getPlanId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
