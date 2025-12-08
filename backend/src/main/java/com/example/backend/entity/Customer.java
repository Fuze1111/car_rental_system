package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户实体类
 * 对应数据库中的customer表
 */
@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    /**
     * 客户号 - 主键
     */
    @Id
    @Column(name = "CNo", length = 8, nullable = false)
    private String cNo;

    /**
     * 客户姓名
     */
    @Column(name = "CName", length = 50, nullable = false)
    private String cName;

    /**
     * 客户单位
     */
    @Column(name = "Company", length = 100)
    private String company;

    /**
     * 客户电话
     */
    @Column(name = "Phone", length = 20)
    private String phone;

    /**
     * 客户地址
     */
    @Column(name = "Address", length = 200)
    private String address;

    /**
     * 邮编
     */
    @Column(name = "PostCode", length = 6)
    private String postCode;
}

