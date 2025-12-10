package com.example.backend.dto;

import lombok.Data;

/**
 * 客户注册请求 DTO
 */
@Data
public class CustomerRegisterRequest {
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 公司
     */
    private String company;
    
    /**
     * 地址
     */
    private String address;
}
