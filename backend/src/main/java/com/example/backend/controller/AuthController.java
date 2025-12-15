package com.example.backend.controller;

import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.CustomerRegisterRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录接口
     * POST /api/auth/login
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 客户注册接口
     * POST /api/auth/register/customer
     * @param request 客户注册请求
     * @return 注册响应
     */
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegisterRequest request) {
        try {
            LoginResponse response = authService.registerCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 司机修改密码
     * POST /api/auth/change-password/driver
     * @param request 修改密码请求
     * @return 成功消息
     */
    @PostMapping("/change-password/driver")
    public ResponseEntity<?> changeDriverPassword(@RequestBody ChangePasswordRequest request) {
        try {
            String message = authService.changeDriverPassword(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "修改密码失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 客户修改密码
     * POST /api/auth/change-password/customer
     * @param request 修改密码请求
     * @return 成功消息
     */
    @PostMapping("/change-password/customer")
    public ResponseEntity<?> changeCustomerPassword(@RequestBody ChangePasswordRequest request) {
        try {
            String message = authService.changeCustomerPassword(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "修改密码失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 管理员修改密码
     * POST /api/auth/change-password/admin
     * @param request 修改密码请求
     * @return 成功消息
     */
    @PostMapping("/change-password/admin")
    public ResponseEntity<?> changeAdminPassword(@RequestBody ChangePasswordRequest request) {
        try {
            String message = authService.changeAdminPassword(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "修改密码失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 测试接口
     * GET /api/auth/test
     * @return 测试消息
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth API is working");
        return ResponseEntity.ok(response);
    }
}
