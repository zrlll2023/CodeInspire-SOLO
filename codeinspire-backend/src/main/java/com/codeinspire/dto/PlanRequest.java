package com.codeinspire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlanRequest {
    @NotBlank(message = "规划标题不能为空")
    private String title;
    
    private String description;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
}
