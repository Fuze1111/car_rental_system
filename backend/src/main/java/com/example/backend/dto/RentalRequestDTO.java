package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租赁业务请求DTO
 * 用于接收前端发送的租赁业务信息
 */
@Data
public class RentalRequestDTO {

    /**
     * 发票号
     */
    private String invoiceNo;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 地点
     */
    private String place;

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
}

