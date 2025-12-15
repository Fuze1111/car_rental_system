package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
@IdClass(CarType.CarTypeId.class)
public class CarType {

    /**
     * 类型编号 - 复合主键之一
     */
    @Id
    @Column(name = "TNo", length = 4, nullable = false)
    private String tNo;

    /**
     * 汽车型号 - 复合主键之一
     */
    @Id
    @Column(name = "TName", length = 20, nullable = false)
    private String tName;

    /**
     * 租金率（元/小时）
     */
    @Column(name = "RentRate", precision = 4, scale = 2)
    private BigDecimal rentRate;

    /**
     * 复合主键类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarTypeId implements Serializable {
        private String tNo;
        private String tName;
    }
}

