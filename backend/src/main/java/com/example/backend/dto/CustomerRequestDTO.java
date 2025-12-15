package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 客户请求DTO
 * 用于接收前端发送的客户信息
 */
@Data
public class CustomerRequestDTO {

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
    @JsonProperty("company")
    private String company;

    /**
     * 客户电话
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 客户地址
     */
    @JsonProperty("address")
    private String address;

    /**
     * 邮编
     */
    @JsonProperty("postCode")
    private String postCode;
}

