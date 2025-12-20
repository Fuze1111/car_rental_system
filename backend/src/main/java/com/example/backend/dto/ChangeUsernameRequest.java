package com.example.backend.dto;

import lombok.Data;

/**
 * 管理员修改用户名请求 DTO
 */
@Data
public class ChangeUsernameRequest {

    /**
     * 管理员ID（AdminID）
     */
    private String userId;

    /**
     * 新用户名
     */
    private String newUsername;
}

