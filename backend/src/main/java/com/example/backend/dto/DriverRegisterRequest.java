package com.example.backend.dto;

import lombok.Data;

/**
 * 司机注册请求 DTO
 */
@Data
public class DriverRegisterRequest {
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 准驾车型
     */
    private String licenseType;
}
