package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 司机请求DTO
 * 用于接收前端发送的司机信息
 */
@Data
public class DriverRequestDTO {

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
}

