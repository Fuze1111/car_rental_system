package com.example.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一错误响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误时间戳
     */
    private LocalDateTime timestamp;

    /**
     * HTTP状态码
     */
    private int status;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误详情
     */
    private String details;

    public ErrorResponse(int status, String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.details = details;
    }
}

