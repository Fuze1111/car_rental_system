package com.example.backend.dto;

import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {
    
    /**
     * 用户名/账号/手机号
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 角色类型：admin, customer, driver
     */
    private String role;
}
