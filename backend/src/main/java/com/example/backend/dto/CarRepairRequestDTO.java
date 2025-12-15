package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆维修记录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRepairRequestDTO {

    /**
     * 维修日期
     */
    private LocalDateTime rDate;

    /**
     * 运行公里
     */
    private BigDecimal runKm;

    /**
     * 运行小时
     */
    private BigDecimal runHour;

    /**
     * 车辆号
     */
    private String carId;
}

