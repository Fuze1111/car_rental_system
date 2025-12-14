package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("invoiceNo")
    private String invoiceNo;

    /**
     * 借车日期
     */
    @JsonProperty("loanDate")
    private LocalDateTime loanDate;

    /**
     * 还车日期
     */
    @JsonProperty("returnDate")
    private LocalDateTime returnDate;

    /**
     * 借车地点
     */
    @JsonProperty("loanPlace")
    private String loanPlace;

    /**
     * 还车地点
     */
    @JsonProperty("returnPlace")
    private String returnPlace;

    /**
     * 车辆ID
     */
    @JsonProperty("carId")
    private String carId;

    /**
     * 客户号
     */
    @JsonProperty("cNo")
    private String cNo;

    /**
     * 司机号
     */
    @JsonProperty("dNo")
    private String dNo;

    /**
     * 往返里程
     */
    @JsonProperty("roundTripDistance")
    private BigDecimal roundTripDistance;

    /**
     * 行驶时间
     */
    @JsonProperty("driveTime")
    private BigDecimal driveTime;

    /**
     * 应付金额
     */
    @JsonProperty("amount")
    private BigDecimal amount;
}