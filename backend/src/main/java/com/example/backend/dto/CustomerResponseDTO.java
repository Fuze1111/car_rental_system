package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 客户响应DTO
 * 用于向前端返回客户信息
 */
@Data
public class CustomerResponseDTO {

    /**
     * 客户号
     */
    @JsonProperty("cNo")
    private String cNo;

    /**
     * 客户姓名
     */
    @JsonProperty("cName")
    private String cName;

    /**
     * 客户单位
     */
    private String company;

    /**
     * 客户电话
     */
    private String phone;

    /**
     * 客户地址
     */
    private String address;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 租赁次数（额外信息）
     */
    private Long rentalCount;
}

