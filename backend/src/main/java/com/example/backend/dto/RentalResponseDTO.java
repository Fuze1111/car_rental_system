package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租赁业务响应DTO
 * 用于向前端返回租赁业务信息
 */
@Data
public class RentalResponseDTO {

    /**
     * 发票号
     */
    private String invoiceNo;

    /**
     * 借车日期
     */
    private LocalDateTime loanDate;

    /**
     * 还车日期
     */
    private LocalDateTime returnDate;

    /**
     * 借车地点
     */
    private String loanPlace;

    /**
     * 还车地点
     */
    private String returnPlace;

    /**
     * 车辆ID
     */
    private String carId;

    /**
     * 客户号
     */
    private String cNo;

    /**
     * 司机号
     */
    private String dNo;

    /**
     * 往返里程
     */
    private BigDecimal roundTripDistance;

    /**
     * 行驶时间
     */
    private BigDecimal driveTime;

    /**
     * 应付金额
     */
    private BigDecimal amount;

    /**
     * 客户姓名（关联信息）
     */
    private String customerName;

    /**
     * 客户电话（关联信息）
     */
    private String customerPhone;

    /**
     * 司机姓名（关联信息）
     */
    private String driverName;

    /**
     * 车辆车型（关联信息）
     */
    private String carType;

    /**
     * 车辆品牌（关联信息）
     */
    private String carBrand;
}