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
}
