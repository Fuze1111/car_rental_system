package com.example.backend.dto;

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
    private String cNo;

    /**
     * 客户姓名
     */
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
}

