package com.codeinspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanVO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskVO> tasks;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskVO {
        private Long id;
        private Long planId;
        private String title;
        private String description;
        private String status;
        private Integer priority;
        private LocalDate dueDate;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
    }
}
