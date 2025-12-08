package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 司机响应DTO
 * 用于向前端返回司机信息
 */
@Data
public class DriverResponseDTO {

    /**
     * 司机号
     */
    private String dNo;

    /**
     * 姓名
     */
    private String dName;

    /**
     * 入职年月
     */
    private LocalDate startWork;

    /**
     * 基本工资
     */
    private BigDecimal salary;

    /**
     * 司机状态
     */
    private String dStatus;

    /**
     * 准驾车型
     */
    private String licenseType;

    /**
     * 工龄（年）
     */
    private Integer workYears;

    /**
     * 工龄（月）
     */
    private Integer workMonths;

    /**
     * 是否在岗
     */
    private Boolean isOnDuty;
}

