package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 汽车类型实体类
 * 对应数据库中的car_type表
 */
@Entity
@Table(name = "car_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarType {

    /**
     * 类型编号 - 主键
     */
    @Id
    @Column(name = "TNo", length = 4, nullable = false)
    private String tNo;

    /**
     * 汽车型号 - 唯一索引
     */
    @Column(name = "TName", length = 20, nullable = false, unique = true)
    private String tName;

    /**
     * 租金率（元/小时）
     */
    @Column(name = "RentRate", precision = 6, scale = 2)
    private BigDecimal rentRate;
}
