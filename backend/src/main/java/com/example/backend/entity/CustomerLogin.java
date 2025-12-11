package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户登录实体类
 * 对应数据库中的customer_login表
 */
@Entity
@Table(name = "customer_login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLogin {

    /**
     * 客户号 - 主键（同时也是外键）
     */
    @Id
    @Column(name = "CNo", length = 8, nullable = false)
    private String cNo;

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
     * 关联的客户信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CNo", referencedColumnName = "CNo", insertable = false, updatable = false)
    private Customer customer;
}
