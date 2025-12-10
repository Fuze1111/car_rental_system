package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}