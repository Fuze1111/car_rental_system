package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 司机登录实体类
 * 对应数据库中的driver_login表
 */
@Entity
@Table(name = "driver_login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLogin {

    /**
     * 司机号 - 主键（同时也是外键）
     */
    @Id
    @Column(name = "DNo", length = 8, nullable = false)
    private String dNo;

    /**
     * 手机号（用于登录）
     */
    @Column(name = "Phone", length = 20, nullable = false)
    private String phone;

    /**
     * 密码
     */
    @Column(name = "Password", length = 100, nullable = false)
    private String password;

    /**
     * 关联的司机信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DNo", referencedColumnName = "DNo", insertable = false, updatable = false)
    private Driver driver;
}
