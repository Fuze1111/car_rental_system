package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 司机实体类
 * 对应数据库中的driver表
 */
@Entity
@Table(name = "driver")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    /**
     * 司机号 - 主键
     */
    @Id
    @Column(name = "DNo", length = 8, nullable = false)
    private String dNo;

    /**
     * 姓名
     */
    @Column(name = "DName", length = 50, nullable = false)
    private String dName;

    /**
     * 入职年月
     */
    @Column(name = "StartWork")
    private LocalDate startWork;

    /**
     * 基本工资
     */
    @Column(name = "Salary", precision = 6, scale = 2)
    private BigDecimal salary;

    /**
     * 司机状态（在岗/空闲等）
     */
    @Column(name = "DStatus", length = 5)
    private String dStatus;

    /**
     * 准驾车型
     */
    @Column(name = "LicenseType", length = 10)
    private String licenseType;
}

