package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录实体类
 * 对应数据库中的admin_login表
 */
@Entity
@Table(name = "admin_login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogin {

    /**
     * 管理员ID - 主键
     */
    @Id
    @Column(name = "AdminID", length = 8, nullable = false)
    private String adminId;

    /**
     * 用户名（唯一）
     */
    @Column(name = "Username", length = 50, nullable = false, unique = true)
    private String username;

    /**
     * 密码
     */
    @Column(name = "Password", length = 100, nullable = false)
    private String password;
}
