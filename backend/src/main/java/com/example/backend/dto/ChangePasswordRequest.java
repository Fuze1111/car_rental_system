package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordRequest {

    /**
     * 用户ID（司机号）
     */
    @JsonProperty("userId")
    private String userId;

    /**
     * 当前密码
     */
    @JsonProperty("currentPassword")
    private String currentPassword;

    /**
     * 新密码
     */
    @JsonProperty("newPassword")
    private String newPassword;
}
