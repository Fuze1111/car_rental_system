package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名/姓名
     */
    private String userName;
    
    /**
     * 角色类型
     */
    private String role;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 其他信息（可选）
     */
    private String extra;
    
    /**
     * 登录消息
     */
    private String message;
    
    /**
     * 是否需要修改密码（使用默认密码时为true）
     */
    private Boolean needChangePassword;
    
    public LoginResponse(String userId, String userName, String role, String phone, String extra, String message) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.phone = phone;
        this.extra = extra;
        this.message = message;
        this.needChangePassword = false;
    }
}
