package com.codeinspire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotNull(message = "规划ID不能为空")
    private Long planId;
    
    @NotBlank(message = "任务标题不能为空")
    private String title;
    
    private String description;
    
    private Integer priority = 0;
    
    private LocalDate dueDate;
}
