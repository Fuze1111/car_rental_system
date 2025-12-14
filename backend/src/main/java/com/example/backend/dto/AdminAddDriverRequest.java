package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 管理员添加司机请求DTO
 */
@Data
public class AdminAddDriverRequest {

    /**
     * 姓名
     */
    @JsonProperty("dName")
    private String dName;

    /**
     * 手机号（用于登录）
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 入职年月
     */
    @JsonProperty("startWork")
    private LocalDate startWork;

    /**
     * 基本工资
     */
    @JsonProperty("salary")
    private BigDecimal salary;

    /**
     * 司机状态
     */
    @JsonProperty("dStatus")
    private String dStatus;

    /**
     * 准驾车型
     */
    @JsonProperty("licenseType")
    private String licenseType;
}
